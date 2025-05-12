package com.yu.pojo;

import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

@Data
public class Book {

    //书籍编号
    private Integer bookId;

    //分类编码
    @NotBlank(message = "分类不能为空")
    private String categoryCode;

    //书名
    @NotBlank(message = "书名不能为空")
    private String bookName;

    //书号
    @NotBlank(message = "ISBN不能为空")
    private String isbn;

    //作者
    @NotBlank(message = "作者不能为空")
    private String author;

    //出版社
    @NotBlank(message = "出版社不能为空")
    private String press;

    //出版日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @Past(message = "出版日期只能在当前时间之前")
    private Date pubDate;

    //封面图片
    private String image;

    //描述
    private String description;

    //价格
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    //库存
    @NotNull(message = "库存不能为空")
    private Integer stock;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    //分类
    private Category category;
}
