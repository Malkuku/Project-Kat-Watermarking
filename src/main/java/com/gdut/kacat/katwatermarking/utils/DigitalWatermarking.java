package com.gdut.kacat.katwatermarking.utils;

import com.gdut.kacat.katwatermarking.entity.BitStream;
import com.gdut.kacat.katwatermarking.entity.RGB;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DigitalWatermarking {
    static final Charset CHARSET = StandardCharsets.UTF_8;
    public enum MarkingType { //  水印类型
        s, //String
        m, //mp4视频
        n, //mp3音频
        p, //png图片
    }
    public static MarkingType fromByte(byte b) {
        return switch (b) {
            case 's' -> MarkingType.s;
            case 'm' -> MarkingType.m;
            case 'n' -> MarkingType.n;
            case 'p' -> MarkingType.p;
            default -> throw new IllegalArgumentException("未知类型: " + (char) b);
        };
    }

    public static BufferedImage embedLSBImage (File imageFile, String text) throws IOException {
        //生成水印头 长度+类型+数据
        byte[] secretData = generateHeader(text);
        BufferedImage image = ImageIO.read(imageFile);
        //检查容量
        if (8L*secretData.length > 3L*image.getWidth() * image.getHeight()) {
            throw new RuntimeException("数据过大,无法注入图片");
        }
        embedLSBImage(image, secretData);
        return image;
    }

    public static <T> T extractLSBImage(File imageFile, Class<T> expectedType) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        BitStream bitStream = new BitStream(image);

        //读取长度
        int length = bitStream.readInt();

        //读取类型
        MarkingType type = fromByte(bitStream.readByte());

        //读取实际数据
        byte[] secretData = new byte[length];
        for (int i = 0; i < length; i++) {
            secretData[i] = bitStream.readByte();
        }

        switch (type){
            case s:
                if(expectedType == String.class)
                    return expectedType.cast(new String(secretData, CHARSET));
                else throw new RuntimeException("类型不匹配");
            case m:
                //TODO 处理mp4
                break;
            case n:
                //TODO 处理mp3
                break;
            case p:
                //TODO 处理png
                break;
            default:
                throw new RuntimeException("类型读取有误");
        }
        return null;
    }

    private static void embedLSBImage (BufferedImage image, byte[] secretData){
        int bitIndex = 0;
        int dataIndex = 0;
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                if(dataIndex >= secretData.length) return;
                RGB rgb = new RGB(image.getRGB(x, y));
                for(int i = 0; i < 3; i++){
                    if(dataIndex >= secretData.length) break;
                    int bit = (secretData[dataIndex] >> (7 - bitIndex)) & 1;
                    rgb.setRGB(bit, i);

                    if (++bitIndex >= 8) {
                        bitIndex = 0;
                        dataIndex++;
                    }
                }
                image.setRGB(x, y, rgb.getRGB());
            }
        }
    }



    private static byte[] generateHeader(Object obj) {
        byte[] type = new byte[0];
        if(obj instanceof String){
            type = MarkingType.s.toString().getBytes();
        }
        else if(obj instanceof File){
            if(((File) obj).getName().endsWith(".png")){
                type = MarkingType.p.toString().getBytes();
            }else if(((File) obj).getName().endsWith(".mp3")){
                type = MarkingType.n.toString().getBytes();
            }else if(((File) obj).getName().endsWith(".mp4")){
                type = MarkingType.m.toString().getBytes();
            }
        }else{
            throw new RuntimeException("不支持的数据类型");
        }

        byte[] data = obj.toString().getBytes(CHARSET);
        int length = data.length;
        byte[] header = new byte[4];
        header[0] = (byte) (length >>> 24);
        header[1] = (byte) (length >>> 16);
        header[2] = (byte) (length >>> 8);
        header[3] = (byte) (length);
        return ByteBuffer.allocate(header.length + type.length + data.length)
                .put(header)
                .put(type)
                .put(data)
                .array();
    }
}
