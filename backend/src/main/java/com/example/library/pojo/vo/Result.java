package com.example.library.pojo.vo;

public class Result<T> {

    private boolean success;
    private String message;
    private T data;

    public Result() {
    }

    public Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, message, data);
    }

    // 成功响应（无数据）
    public static <T> Result<T> success(String message) {
        return new Result<>(true, message, null);
    }

    // 成功响应（仅数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(true, "请求成功", data);
    }

    // 失败响应
    public static <T> Result<T> fail(String message) {
        return new Result<>(false, message, null);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
