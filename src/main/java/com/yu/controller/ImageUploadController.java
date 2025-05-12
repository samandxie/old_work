package com.yu.controller;

import com.yu.exception.CustomizeException;
import com.yu.pojo.Result;
import com.yu.pojo.User;
import com.yu.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


@RestController
@RequestMapping("/upload")
public class ImageUploadController {

    @Autowired
    UserService userService;

    @PostMapping("/book_image")
    public Result uploadBookImage(MultipartFile bookImage) {
        //获取项目上传文件夹路径
        String path = "src/main/resources/static/images/book_images";
        File pathFile=new File(path);
        System.out.println(pathFile.getAbsolutePath());
        
        // 获取原始文件名
        String originalFilename = bookImage.getOriginalFilename();
        // 获取文件扩展名
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成新的文件名（使用时间戳+随机数）
        String newFilename = System.currentTimeMillis() + "_" + (int)(Math.random()*1000) + extension;
        
        File targetFile = new File(pathFile.getAbsolutePath(), newFilename);

        //上传
        try {
            bookImage.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomizeException(404, "上传图片失败");
        }
        return Result.success(newFilename);
    }

    @PostMapping("/user_pic")
    public Result uploadUserPic(MultipartFile userPic, HttpSession session) {
        //获取项目上传文件夹路径
        String path = "src/main/resources/static/images/user_pic";
        String runtimePath = "target/classes/static/images/user_pic";
        File pathFile=new File(path);
        File runtimePathFile=new File(runtimePath);
        System.out.println(pathFile.getAbsolutePath());

        // 获取原始文件名
        String originalFilename = userPic.getOriginalFilename();
        // 获取文件扩展名
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成新的文件名（使用时间戳+随机数）
        String newFilename = System.currentTimeMillis() + "_" + (int)(Math.random()*1000) + extension;

        File targetFile = new File(pathFile.getAbsolutePath(), newFilename);
        File runtimeTargetFile = new File(runtimePathFile.getAbsolutePath(), newFilename);

        //上传
        try {
            // 确保目录存在
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            if (!runtimePathFile.exists()) {
                runtimePathFile.mkdirs();
            }

            // 先保存到临时文件再移动
            File tempFile = File.createTempFile("upload_", ".tmp");
            userPic.transferTo(tempFile);
            
            // 移动文件到目标位置
            Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(targetFile.toPath(), runtimeTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 先获取并验证用户
            User user = (User) session.getAttribute("user");
            if (user == null) {
                throw new CustomizeException(401, "用户未登录，请先登录");
            }
            
            // 创建User对象的深拷贝，避免修改原对象
            User updatedUser = new User(user);  // 假设User类有拷贝构造函数
            updatedUser.setUserPic(newFilename);

            // 先更新数据库
            int affectedRows = userService.updateAvatar(newFilename, user.getUserId());
            if (affectedRows == 0) {
                throw new CustomizeException(500, "更新数据库失败");
            }

            // 然后更新Session中的用户信息
            session.setAttribute("user", updatedUser);
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomizeException(404, "上传图片失败: " + e.getMessage());
        }
        return Result.success(newFilename);
    }
}
