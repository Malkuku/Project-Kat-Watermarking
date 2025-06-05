package com.gdut.kacat.katwatermarking.service.impl;

import com.gdut.kacat.katwatermarking.service.WatermarkService;
import com.gdut.kacat.katwatermarking.utils.DigitalWatermarking;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
public class WatermarkServiceImpl implements WatermarkService {
    @Override
    public byte[] generateTextWatermarkToPng(MultipartFile imageFile, String text) throws IOException {
        // 创建临时文件
        File tempFile = File.createTempFile("upload-", ".png");
        imageFile.transferTo(tempFile); // 保存到临时文件

        // 处理完成后删除临时文件
        BufferedImage image = DigitalWatermarking.embedLSBImage(tempFile, text);
        tempFile.delete(); // 清理临时文件

        //将BufferedImage转换回字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        return baos.toByteArray();
    }

    @Override
    public String extractTextWatermarkFromPng(MultipartFile imageFile) throws IOException {
        // 创建临时文件
        File tempFile = File.createTempFile("upload-", ".png");
        imageFile.transferTo(tempFile); // 保存到临时文件

        String text = DigitalWatermarking.extractLSBImage(tempFile, String.class);

        // 处理完成后删除临时文件
        tempFile.delete(); // 清理临时文件
        return text;
    }

    @Override
    public byte[] generateFileWatermarkToPng(MultipartFile imageFile, MultipartFile file) throws IOException {
        // 创建临时文件
        File tempFile = File.createTempFile("upload-", ".png");
        imageFile.transferTo(tempFile); // 保存到临时文件

        //检查文件类型（png/mp3/mp4）
        File tempScFile;
        if(Objects.equals(file.getContentType(), "image/png")){
            tempScFile  = File.createTempFile("upload-", ".png");
        }else if(Objects.equals(file.getContentType(), "audio/mpeg") || Objects.equals(file.getContentType(), "audio/mp3")){
            tempScFile  = File.createTempFile("upload-", ".mp3");
        }else if(Objects.equals(file.getContentType(), "video/mp4")){
            tempScFile = File.createTempFile("upload-", ".mp4");
        }else{
            throw new RuntimeException("不支持的文件类型");
        }
        file.transferTo(tempScFile);

        // 处理完成后删除临时文件
        BufferedImage image = DigitalWatermarking.embedLSBImage(tempFile, tempScFile);
        // 清理临时文件
        tempFile.delete();
        tempScFile.delete();

        //将BufferedImage转换回字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        return baos.toByteArray();
    }

    @Override
    public byte[] extractFileWatermarkFromPng(MultipartFile imageFile, String fileType) throws IOException {
        // 创建临时文件
        File tempFile = File.createTempFile("upload-", ".png");
        imageFile.transferTo(tempFile); // 保存到临时文件

        //检查文件类型（png/mp3/mp4）
        if(!Objects.equals(fileType, "png")
                && !Objects.equals(fileType, "mp3")
                && !Objects.equals(fileType, "mp4")){
            throw new RuntimeException("不支持的文件类型");
        }
        byte[] bytes = DigitalWatermarking.extractLSBImage(tempFile, byte[].class);

        // 处理完成后删除临时文件
        tempFile.delete(); // 清理临时文件
        return bytes;
    }
}
