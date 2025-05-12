package com.yu.service.impl;

import com.yu.Mapper.BookMapper;
import com.yu.pojo.Book;
import com.yu.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;
    @Override
    public List<Book> findAll() {
        return bookMapper.selectAll();
    }

    public List<Book> findByPage(int offset, int limit) {
        return bookMapper.selectByPage(offset, limit);
    }

    @Override
    public void add(Book book) {
        Date date = new Date();
        System.out.println(date);
        book.setCreateTime(new Date());
        bookMapper.insert(book);
    }

    @Override
    public int delete(Integer id) {
        return bookMapper.deleteByBookId(id);
    }

    @Override
    public int update(Book book) {
        return bookMapper.updateByBookId(book);
    }

    @Override
    public List<Book> selectByBooks(Book book) {
        if (book.getBookName().isEmpty())
            book.setBookName(null);
        if (book.getIsbn().isEmpty())
            book.setIsbn(null);
        return bookMapper.selectByBooks(book);
    }
}
