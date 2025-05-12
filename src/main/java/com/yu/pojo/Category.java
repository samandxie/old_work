package com.yu.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Category {

    // 分类编码
    @NotBlank(message = "分类代码不能为空")
    @Pattern(regexp = "[a-zA-Z]+",message = "分类代码只能为字母")
    private String categoryCode;

    // 分类名称
    @NotBlank(message = "分类名称不为空")
    private String categoryName;

}
