package com.gdut.kacat.katwatermarking.entity;

import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class RGB {
    private int r, g, b;
    //提取r,g,b的值
    public RGB(int rgb) {
        this.r = (rgb >> 16) & 0xFF;
        this.g = (rgb >> 8) & 0xFF;
        this.b = rgb & 0xFF;
    }
    public RGB(BufferedImage image,int x,int y){
        new RGB(image.getRGB(x, y));
    }

    //根据当前颜色通道索引嵌入bit
    public void setRGB(int bit, int channelIndex) {
        if(channelIndex == 0){
            r = (r & 0xFE) | bit;
        }else if(channelIndex == 1){
            g = (g & 0xFE) | bit;
        }else if(channelIndex == 2){
            b = (b & 0xFE) | bit;
        }
    }

    //获取当前rgb的值
    public int getRGB(){
        return (r << 16) | (g << 8) | b;
    }

    //根据当前颜色通道索引获取最低位bit
    public int getLowBit(int channelIndex){
        return (channelIndex == 0)
                ? (r & 1) :(channelIndex == 1)
                ? (g & 1) :(b & 1);
    }
}
