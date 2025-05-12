package com.yu.Mapper;

import com.yu.pojo.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Delete("delete from category where category_code = #{code}")
    int deleteByCategoryCode(String code);

    @Insert("insert into category values (#{categoryCode},#{categoryName})")
    int insert(Category record);

    @Select("select * from category where category_code = #{categoryCode}")
    Category selectByCategoryCode(String categoryCode);

    @Update("update category set category_name = #{categoryName} where category_code = #{categoryCode}")
    int update(Category record);

    @Select("select * from category")
    List<Category> selectAll();

}
