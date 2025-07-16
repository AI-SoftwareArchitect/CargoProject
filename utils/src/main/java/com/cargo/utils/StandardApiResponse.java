package com.cargo.utils;

import org.springframework.http.HttpStatus;

public class StandardApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // Constructor (optional, but often useful)
    public StandardApiResponse() {
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Static factory methods (as seen in your code)
    public static <T> StandardApiResponse<T> success(String message, T data) {
        StandardApiResponse<T> response = new StandardApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> StandardApiResponse<T> created(String message, T data) {
        StandardApiResponse<T> response = new StandardApiResponse<>();
        response.setSuccess(true); // Assuming created is also a success
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> StandardApiResponse<T> error(String message, HttpStatus status) {
        StandardApiResponse<T> response = new StandardApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        // You might want to add status to the response class if you're using it
        // response.setStatus(status);
        return response;
    }
}