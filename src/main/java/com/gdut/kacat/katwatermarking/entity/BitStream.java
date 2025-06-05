package com.gdut.kacat.katwatermarking.entity;

import java.awt.image.BufferedImage;

public class BitStream{
    private final BufferedImage image;
    private int x = 0,y = 0;
    private int channelIndex = 0;

    public BitStream(BufferedImage image){
        this.image = image;
    }

    public int readBit(){
        if(y >= image.getHeight()) return 0;

        RGB rgb = new RGB(image.getRGB(x, y));
        int bit = rgb.getLowBit(channelIndex);

        //next bit
        channelIndex++;
        if(channelIndex >= 3){
            channelIndex = 0;
            x++;
            if(x >= image.getWidth()){
                x = 0;
                y++;
            }
        }

        return bit;
    }

    public byte readByte(){
        byte b = 0;
        for(int i = 0; i < 8; i++){
            b |= (byte)(readBit() << (7 - i));
        }
        return b;
    }

    public int readInt(){
        return (readByte() & 0xFF) << 24 |
                (readByte() & 0xFF) << 16 |
                (readByte() & 0xFF) << 8  |
                (readByte() & 0xFF);
    }
}
