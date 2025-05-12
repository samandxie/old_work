package com.yu.Mapper;

import com.yu.pojo.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookMapper {

    @Delete("delete from book where book_id = #{bookId}")
    int deleteByBookId(Integer bookId);


    @Insert("insert into book " +
            "(category_code, book_name, isbn, author, press, pub_date,image, description, " +
            "price, stock, create_time) VALUES " +
            "(#{categoryCode},#{bookName},#{isbn},#{author},#{press},#{pubDate},#{image}," +
            "#{description},#{price},#{stock},#{createTime})")
    int insert(Book record);


    @Update("update book set book_id = #{bookId}, category_code = #{categoryCode}, book_name = #{bookName}, " +
            "isbn = #{isbn}, author = #{author}, press = #{press}, pub_date = #{pubDate}, " +
            "description = #{description}, price = #{price}, stock = #{stock} " +
            "where book_id = #{bookId}")
    int updateByBookId(Book record);

    @Select("select * from book where category_code = #{categoryCode}")
    @ResultMap("BaseResultMap")
    List<Book> selectAllByCategoryCode(String categoryCode);

    List<Book> selectByBooks(Book book);

    @Select("select * from book order by book_id")
    @ResultMap("BaseResultMap")
    List<Book> selectAll();

    @Select("select * from book order by book.book_id limit #{offset}, #{limit} ")
    @ResultMap("BaseResultMap")
    List<Book> selectByPage(int offset, int limit);

    @Select("select * from book where book_id = #{bookId}")
    @ResultMap("BaseResultMap")
    Book selectByBookId(Integer bookId);

    @Select("select * from book where book_name like concat('%',#{bookName},'%')")
    @ResultMap("BaseResultMap")
    List<Book> selectByBookName(String bookName);

    @Select("select book_name from book where book_name like concat('%',#{keyword},'%') or author like concat('%',#{keyword},'%')")
    @ResultMap("BaseResultMap")
    List<String> selectByKeyword(String keyword);

    @Select("select * from book order by book.create_time desc limit 5")
    List<Book> selectByCreateTime();

    @Select("select * from book order by book.stock desc limit 5")
    List<Book> selectBySales();
}
