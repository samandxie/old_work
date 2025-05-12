package com.yu.pojo;

import com.yu.pojo.Validation.Password;
import com.yu.pojo.Validation.UserName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Admin {
    Integer id;
    @NotBlank(groups = UserName.class, message = "用户名不能为空")
    String username;
    @NotBlank(groups = Password.class, message = "密码不能为空")
    String password;
}
