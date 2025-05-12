package com.yu.service.impl;

import com.yu.Mapper.BookMapper;
import com.yu.Mapper.CategoryMapper;
import com.yu.pojo.Book;
import com.yu.pojo.Category;
import com.yu.pojo.Result;
import com.yu.service.BookDisplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookDisplayServiceImpl implements BookDisplayService {

    @Autowired
    BookMapper bookMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Override
    public List<Book> getBooksByCategoryCode(String categoryCode) {
        return bookMapper.selectAllByCategoryCode(categoryCode);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAll();
    }

    @Override
    public List<Book> selectAll() {
        return bookMapper.selectAll();
    }

    @Override
    public List<Book> searchBooks(String bookName) {
        return bookMapper.selectByBookName(bookName);
    }

    @Override
    public Book getBookDetailsByBookId(Integer bookId) {
        return bookMapper.selectByBookId(bookId);
    }

    @Override
    public List<String> getBookByKeyword(String keyword) {
        return bookMapper.selectByKeyword(keyword);
    }

    @Override
    public List<Book> getNewBooks() {
        return bookMapper.selectByCreateTime();
    }

    @Override
    public Book getLikesBook() {
        List<Book> books = bookMapper.selectAll();
        return books.get((int) (Math.random() * books.size()));
    }

    @Override
    public List<Book> selectBookBySales() {
        return bookMapper.selectBySales();
    }
}
