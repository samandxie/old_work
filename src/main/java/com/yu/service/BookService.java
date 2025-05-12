package com.yu.service;

import com.yu.pojo.Book;

import java.util.List;

public interface BookService {
    public List<Book> findAll();

    public List<Book> findByPage(int offset, int limit);

    void add(Book book);

    int delete(Integer id);

    int update(Book book);

    List<Book> selectByBooks(Book book);
}
