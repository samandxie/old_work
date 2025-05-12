package com.yu.service;

import com.yu.pojo.Book;
import com.yu.pojo.Category;

import java.util.List;

public interface BookDisplayService {
    List<Book> getBooksByCategoryCode(String categoryCode);

    List<Category> getAllCategories();

    List<Book> selectAll();

    List<Book> searchBooks(String bookName);

    Book getBookDetailsByBookId(Integer bookId);

    List<String> getBookByKeyword(String keyword);

    List<Book> getNewBooks();

    Book getLikesBook();

    List<Book> selectBookBySales();
}
