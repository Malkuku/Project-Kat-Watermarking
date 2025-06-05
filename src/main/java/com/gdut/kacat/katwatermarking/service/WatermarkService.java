package com.gdut.kacat.katwatermarking.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface WatermarkService {
    byte[] generateTextWatermarkToPng(MultipartFile imageFile, String text) throws IOException;

    String extractTextWatermarkFromPng(MultipartFile imageFile) throws IOException;
}
