package com.yu.service;

import com.yu.pojo.Category;

import java.util.List;

public interface CategoryService {

    public List<Category> selectAll();

    void add(Category category);

    Category selectByCategoryCode(String categoryCode);

    int update(Category category);

    int deleteByCategoryCode(String code);
}
