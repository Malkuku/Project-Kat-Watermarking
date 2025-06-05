package com.gdut.kacat.katwatermarking.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface WatermarkService {
    //  生成文字水印
    byte[] generateTextWatermarkToPng(MultipartFile imageFile, String text) throws IOException;

    //  提取文字水印
    String extractTextWatermarkFromPng(MultipartFile imageFile) throws IOException;

    //  生成文件水印
    byte[] generateFileWatermarkToPng(MultipartFile imageFile, MultipartFile file) throws IOException;

    //   提取文件水印
    byte[] extractFileWatermarkFromPng(MultipartFile imageFile, String fileType) throws IOException;
}
