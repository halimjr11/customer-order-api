package com.halimjr11.api.model;

/**
 * Generic API response wrapper to standardize response structures.
 * 
 * @param <T> The type of the response payload (data).
 */
public class ApiResponse<T> {

    /**
     * HTTP status code representing the result of the API operation.
     */
    private int code;

    /**
     * Human-readable message describing the response.
     */
    private String message;

    /**
     * Optional data returned in the response.
     */
    private T data;

    /**
     * Default constructor for serialization/deserialization.
     */
    public ApiResponse() {
    }

    /**
     * Constructs a new ApiResponse with the given code, message, and data.
     *
     * @param code    HTTP status code.
     * @param message Response message.
     * @param data    Payload data.
     */
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Factory method for creating a successful response.
     *
     * @param message Success message.
     * @param data    Data payload.
     * @param <T>     Type of the payload.
     * @return ApiResponse with status code 200.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * Factory method for creating an error response.
     *
     * @param code    HTTP status code representing the error.
     * @param message Error message.
     * @param <T>     Type of the payload (usually null).
     * @return ApiResponse with no data.
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // Getters and Setters

    /**
     * Gets the HTTP status code.
     *
     * @return Status code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets the HTTP status code.
     *
     * @param code Status code.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Gets the response message.
     *
     * @return Message string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message.
     *
     * @param message Message string.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the payload data.
     *
     * @return Data object.
     */
    public T getData() {
        return data;
    }

    /**
     * Sets the payload data.
     *
     * @param data Data object.
     */
    public void setData(T data) {
        this.data = data;
    }
}
