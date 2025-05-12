package com.yu.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yu.pojo.Validation.UserName;
import com.yu.pojo.Validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Integer userId;//主键id

    @NonNull
    @Pattern(groups = UserName.class, regexp = "[0-9A-Za-z_]{3,15}",message = "用户名只能是3~15位字母、数字或者下划线")
    private String userName;//用户名

    @NonNull
    @NotBlank(groups = Password.class, message = "密码不能为空")
    @Length(groups = Password.class, min = 6,message = "密码至少6个字符")
    private String password;//密码

    @Email(groups = UserName.class, message = "邮箱格式错误")
    private String email;//邮箱
    @NonNull
    private String userPic;//头像
    private Date joinTime;//加入时间

    public User(User source) {
        this.userId = source.userId;
        this.userName = source.userName;
    }
}
