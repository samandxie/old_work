package com.yu.service.impl;

import com.yu.Mapper.CategoryMapper;
import com.yu.pojo.Category;
import com.yu.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public List<Category> selectAll() {
        return categoryMapper.selectAll();
    }

    @Override
    public void add(Category category) {
        categoryMapper.insert(category);
    }

    @Override
    public Category selectByCategoryCode(String categoryCode) {
        return categoryMapper.selectByCategoryCode(categoryCode);
    }

    @Override
    public int update(Category category) {
        return categoryMapper.update(category);
    }

    @Override
    public int deleteByCategoryCode(String code) {
        return categoryMapper.deleteByCategoryCode(code);
    }
}
