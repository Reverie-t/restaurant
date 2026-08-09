package org.example.restaurantmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.example.restaurantmanagement.dto.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class UploadController {

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            return Result.error("请选择图片");
        }

        // 获取后缀
        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(
                originalName.lastIndexOf(".")
        ).toLowerCase();

        // 类型校验
        if (!suffix.equals(".jpg")
                && !suffix.equals(".jpeg")
                && !suffix.equals(".png")
                && !suffix.equals(".webp")) {

            return Result.error("仅支持 jpg、jpeg、png、webp 图片");
        }

        // 大小限制
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("图片不能超过5MB");
        }

        // 新文件名
        String newName = UUID.randomUUID() + suffix;

        // 项目目录/upload
        String uploadPath =
                System.getProperty("user.dir")
                        + File.separator
                        + "upload";

        File dir = new File(uploadPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, newName);

        System.out.println("保存路径：" + dest.getAbsolutePath());

        file.transferTo(dest);

        return Result.success("/upload/" + newName);
    }}