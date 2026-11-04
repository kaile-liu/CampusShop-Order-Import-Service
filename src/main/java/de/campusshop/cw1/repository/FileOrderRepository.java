package de.campusshop.cw1.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.campusshop.cw1.config.StorageProperties;
import de.campusshop.cw1.exception.StorageException;
import de.campusshop.cw1.model.Order;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * JSON-file backed {@link OrderRepository}.
 * Store all orders in ONE file as a single JSON array - not one file per order. hold the orders in the in-memory
 * {@code ordersById} map for fast lookup and rewrite the whole array after each mutation, under the
 * {@code lock}. Create the parent directory and an empty {@code []} file if missing; write
 * atomically (temp file + {@code Files.move(..., ATOMIC_MOVE, REPLACE_EXISTING)}) so a crash never
 * leaves a half-written store. Date serialisation is handled by the {@code ObjectMapper} configuration.
 */
@Repository
public class FileOrderRepository implements OrderRepository {

    private static final Logger log = LoggerFactory.getLogger(FileOrderRepository.class);

    private final ObjectMapper objectMapper;
    private final Path storagePath;
    private final Map<String, Order> ordersById = new LinkedHashMap<>();
    private final Object lock = new Object();

    public FileOrderRepository(ObjectMapper objectMapper, StorageProperties properties) {
        this.objectMapper = objectMapper;
        this.storagePath = Path.of(properties.file());
    }

    /** Initialise the store: create parent dir + empty file if needed, then load existing orders. */
    @PostConstruct
    public void load() {
        synchronized (lock) {
            try {
                List<Order> orders = readAllFromDisk();
                refreshCache(orders);

                log.info(
                        "Loaded {} orders from {}",
                        orders.size(),
                        storagePath
                );
            } catch (IOException e) {
                throw new StorageException(
                        "Failed to load orders from " + storagePath, e
                );
            }
        }
    }

    @Override
    public Order save(Order order) {
        // persist to disk
        synchronized (lock) {
            try {
                List<Order> orders = readAllFromDisk();

                int existingIndex = -1;

                for (int i = 0; i < orders.size(); i++) {
                    if (orders.get(i).orderId().equals(order.orderId())) {
                        existingIndex = i;
                        break;
                    }
                }

                if (existingIndex >= 0) {
                    orders.set(existingIndex, order);
                } else {
                    orders.add(order);
                }

                writeAll(orders);
                refreshCache(orders);

                log.info("Saved order {}", order.orderId());

                return order;
            } catch (IOException e) {
                throw new StorageException(
                        "Failed to save order " + order.orderId(), e
                );
            }
        }
    }

    @Override
    public Optional<Order> findById(String orderId) {
        synchronized (lock) {
            return Optional.ofNullable(ordersById.get(orderId));
        }
    }

    @Override
    public List<Order> findAll() {
        synchronized (lock) {
            return new ArrayList<>(ordersById.values());
        }
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        synchronized (lock) {
            return ordersById.values().stream()
                    .filter(order -> order.customerId().equals(customerId))
                    .toList();
        }
    }

    @Override
    public boolean deleteById(String orderId) {
        synchronized (lock) {
            try {
                List<Order> orders = readAllFromDisk();

                boolean removed = orders.removeIf(
                        order -> order.orderId().equals(orderId)
                );

                if (!removed) {
                    refreshCache(orders);
                    return false;
                }

                writeAll(orders);
                refreshCache(orders);

                log.info("Deleted order {}", orderId);

                return true;
            } catch (IOException e) {
                throw new StorageException(
                        "Failed to delete order " + orderId, e
                );
            }
        }
    }

    @Override
    public boolean existsByExternalOrderId(String externalOrderId) {
        // return true if any stored order already has this externalOrderId (drives the 409 path)
        synchronized (lock) {
            return ordersById.values().stream()
                    .anyMatch(order ->
                            order.externalOrderId().equals(externalOrderId)
                    );
        }
    }

    // HELPERS
    private List<Order> readAllFromDisk() throws IOException {
        Path absolutePath = storagePath.toAbsolutePath();
        Path parent = absolutePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (!Files.exists(storagePath) || Files.size(storagePath) == 0) {
            Files.writeString(
                    storagePath,
                    "[]",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }
        List<Order> orders = objectMapper.readValue(
                storagePath.toFile(),
                new TypeReference<List<Order>>() {}
        );
        if (orders == null) {
            throw new IOException(
                    "Order store must contain a JSON array, not null"
            );
        }
        return new ArrayList<>(orders);
    }

    private void writeAll(List<Order> orders) throws IOException {
        Path absolutePath = storagePath.toAbsolutePath();
        Path parent = absolutePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path directory = parent != null
                ? parent
                : Path.of(".").toAbsolutePath();
        Path tempFile = Files.createTempFile(
                directory,
                "orders-",
                ".tmp"
        );
        try {
            objectMapper.writeValue(tempFile.toFile(), orders);
            try {
                Files.move(
                        tempFile,
                        storagePath,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (AtomicMoveNotSupportedException e) {
                // Fallback for file systems that do not support atomic moves.
                Files.move(
                        tempFile,
                        storagePath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private void refreshCache(List<Order> orders) {
        ordersById.clear();
        for (Order order : orders) {
            ordersById.put(order.orderId(), order);
        }
    }
}
