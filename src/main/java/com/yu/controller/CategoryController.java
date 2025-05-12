package com.yu.controller;

import com.yu.pojo.Category;
import com.yu.pojo.Result;
import com.yu.service.CategoryService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public Result<List<Category>> selectAllCategory() {
        List<Category> categorieList = categoryService.selectAll();
        Integer count = categorieList.size();
        return Result.success(categorieList, count);
    }

    @PostMapping
    public Result add(@Validated Category category){
        categoryService.add(category);
        return Result.success();
    }

    @GetMapping("/detail")
    public Result<Category> detail(String id){
        Category category = categoryService.selectByCategoryCode(id);
        return Result.success(category);
    }

    @PutMapping
    public Result update(@RequestBody @Validated Category category){
        return Result.success(categoryService.update(category));
    }

    @DeleteMapping("{code}")
    public Result delete(@PathVariable String code){
        return Result.success(categoryService.deleteByCategoryCode(code));
    }
}
