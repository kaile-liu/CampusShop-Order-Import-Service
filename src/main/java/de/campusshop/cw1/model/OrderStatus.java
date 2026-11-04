package de.campusshop.cw1.model;

/**
 * Lifecycle states an imported order can be in. The numeric code mirrors the
 * "enum with fields" example from the Week 1 lecture.
 */
public enum OrderStatus {
    IMPORTED(10),
    FAILED(20),
    DELETED(30);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
