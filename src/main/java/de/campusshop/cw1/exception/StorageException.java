package de.campusshop.cw1.exception;

/** Thrown when reading or writing the JSON order store fails. Mapped to HTTP 500. */
public class StorageException extends RuntimeException {
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
