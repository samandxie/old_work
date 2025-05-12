package com.yu.controller;

import com.github.pagehelper.PageInfo;
import com.yu.pojo.Book;
import com.yu.pojo.Category;
import com.yu.pojo.Result;
import com.yu.service.BookDisplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/index")
public class BookDisplayController {

    @Autowired
    private BookDisplayService bookDisplayService;

    @GetMapping("/books")
    @ResponseBody
    public Result getBooksByCategoryCode(@RequestParam(required = false) String categoryCode, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit) {
        List<Book> books;
        if (categoryCode == null)
            books = bookDisplayService.selectAll();
        else
            books = bookDisplayService.getBooksByCategoryCode(categoryCode);
        int offset = (page - 1) * limit;
        if (offset + limit > books.size())
            limit = books.size() - offset;
        List<Book> booklist = books.subList(offset, offset + limit);
        Integer count = books.size();
        return Result.success(booklist, count);
    }

    @GetMapping("/category")
    @ResponseBody
    public Result getCategories() {
        List<Category> categories = bookDisplayService.getAllCategories();
        return Result.success(categories);
    }

    @GetMapping("book/search")
    @ResponseBody
    public Result searchBooks(@RequestParam(required = true) String bookName, Integer page, Integer limit) {
        int offset = (page - 1) * limit;
        List<Book> books = bookDisplayService.searchBooks(bookName);
        if (offset + limit > books.size())
            limit = books.size() - offset;
        List<Book> booklist = books.subList(offset, offset + limit);
        Integer count = books.size();
        return Result.success(booklist, count);
    }
    @GetMapping("/books/details/{bookId}")
    public String bookDetailsView(@PathVariable("bookId") Integer bookId, Model model) {
        Book book = bookDisplayService.getBookDetailsByBookId(bookId);
        model.addAttribute("book", book);
        return "details";
    }

    @GetMapping("/search/suggestions")
    @ResponseBody
    public Result getSuggestions(@RequestParam("keyword") String keyword) {
        List<String> suggestions = bookDisplayService.getBookByKeyword(keyword);
        return Result.success(suggestions, suggestions.size());
    }

    @GetMapping("/books/new")
    @ResponseBody
    public Result getNewBooks() {
        List<Book> books = bookDisplayService.getNewBooks();
        return Result.success(books, books.size());
    }

    @GetMapping("/books/likes")
    @ResponseBody
    public Result getLikesBook() {
        Book book = bookDisplayService.getLikesBook();
        return Result.success(book,1);
    }

    @GetMapping("/books/ranking")
    @ResponseBody
    public Result getRankingBooks() {
        List<Book> books = bookDisplayService.selectBookBySales();
        return Result.success(books, books.size());
    }
}
