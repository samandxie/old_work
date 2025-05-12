package com.yu.controller;

import com.yu.pojo.Book;
import com.yu.pojo.Result;
import com.yu.pojo.User;
import com.yu.pojo.Validation.UserName;
import com.yu.service.BookService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;


    @GetMapping("/list")
    public Result list(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        int offset = (page - 1) * limit;
        List<Book> books = bookService.findAll();
        if (offset + limit > books.size())
            limit = books.size() - offset;
        List<Book> booklist = books.subList(offset, offset + limit);
        Integer count = books.size();
        return Result.success(booklist, count);
    }
    @PutMapping("/update")
    public Result update(@RequestBody Book book){
        int count = bookService.update(book);
        return Result.success(count);
    }

    @PostMapping
    public Result add(@Validated Book book){
        bookService.add(book);
        return Result.success();
    }

    @DeleteMapping("{id}")
    public Result delete(@PathVariable Integer id){
        int count = bookService.delete(id);
        return Result.success(count);
    }

    @GetMapping("/search")
    public Result search(Book book){
        List<Book> books = bookService.selectByBooks(book);
        return Result.success(books);
    }
}
