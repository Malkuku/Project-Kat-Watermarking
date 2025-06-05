package com.gdut.kacat.katwatermarking.service.impl;

import com.gdut.kacat.katwatermarking.entity.Result;
import com.gdut.kacat.katwatermarking.service.WatermarkService;
import com.gdut.kacat.katwatermarking.utils.DigitalWatermarking;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

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
        ImageIO.write(image, "png", new File("output.png"));

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
}
