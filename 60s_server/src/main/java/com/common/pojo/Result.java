package com.common.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//统一响应结果
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result<T> {


    private Integer code;//业务状态码  0-成功  1-失败
    private String message;//提示信息
    private T data;//响应数据

    //快速返回操作成功响应结果(带响应数据)
    public static <E> Result<E> success(Integer code, String message, E data) {
        return new Result<>(code, message, data);
    }

    //快速返回操作成功响应结果
    public static Result success(Integer code, String message) {
        return new Result(code, message, null);
    }

    public static Result error(Integer code, String message) {
        return new Result(code, message, null);
    }
}
