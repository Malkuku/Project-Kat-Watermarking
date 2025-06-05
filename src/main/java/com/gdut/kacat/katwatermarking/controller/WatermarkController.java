package com.gdut.kacat.katwatermarking.controller;

import com.gdut.kacat.katwatermarking.entity.Result;
import com.gdut.kacat.katwatermarking.service.WatermarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/watermark")
public class WatermarkController {
    @Autowired
    WatermarkService watermarkService;

    // 提取文件水印
    @PostMapping("/extract/png/file")
    public Result extractFileWatermarkFromPng(
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("fileType") String fileType
    ) throws IOException {
        if (!"image/png".equals(imageFile.getContentType())) {
            return Result.error("文件格式错误：非png格式");
        }
        byte[] bytes = watermarkService.extractFileWatermarkFromPng(imageFile,fileType);
        return Result.success(bytes);
    }

    //  生成文件水印
    @PostMapping("/generate/png/file")
    public Result generateFileWatermarkToPng(
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (!"image/png".equals(imageFile.getContentType())) {
            return Result.error("文件格式错误：非png格式");
        }
        byte[] resultImage = watermarkService.generateFileWatermarkToPng(imageFile,file);
        return Result.success(resultImage);
    }

    //  生成文字水印
    @PostMapping("/generate/png/text")
    public Result generateTextWatermarkToPng(
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("text") String text
    ) throws IOException {
        if (!"image/png".equals(imageFile.getContentType())) {
            return Result.error("文件格式错误：非png格式");
        }
        byte[] resultImage = watermarkService.generateTextWatermarkToPng(imageFile,text);
        return Result.success(resultImage);
    }

    //  提取文字水印
    @PostMapping("/extract/png/text")

    public Result extractTextWatermarkFromPng(
            @RequestParam("imageFile") MultipartFile imageFile
    ) throws IOException {
        if (!"image/png".equals(imageFile.getContentType())) {
            return Result.error("文件格式错误：非png格式");
        }
        String text = watermarkService.extractTextWatermarkFromPng(imageFile);
        return Result.success(text);
    }
}
