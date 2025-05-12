package com.yu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> {
    private Integer code;//状态码  0-成功  1-失败
    private String message;//提示信息
    private T data;//返回数据
    private Integer count;

    public static <T> Result<T> success(T data, Integer count){
        return new Result<>(0,"操作成功",data, count);
    }

    public static <T> Result<T> success(T data){
        return new Result<>(0,"操作成功",data, null);
    }
    public static Result success(){
        return new Result<>(0,"操作成功",null,null);
    }

    public static Result error(String message){
        return new Result<>(1,message,null,null);
    }
}
