package com.twobomb.ui.pages.error;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
