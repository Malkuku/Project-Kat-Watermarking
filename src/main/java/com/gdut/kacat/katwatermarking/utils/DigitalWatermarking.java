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
import java.nio.file.Files;

public class DigitalWatermarking {
    static final Charset CHARSET = StandardCharsets.UTF_8;
    public enum MarkingType { //  水印类型
        s, //String
        m, //mp4视频
        n, //mp3音频
        p, //png图片
        w, //wav音频
        t,  //txt文本
    }
    // 从字节获取水印类型
    public static MarkingType fromByte(byte b) {
        return switch (b) {
            case 's' -> MarkingType.s;
            case 'm' -> MarkingType.m;
            case 'n' -> MarkingType.n;
            case 'p' -> MarkingType.p;
            case 'w' -> MarkingType.w;
            case 't' -> MarkingType.t;
            default -> throw new IllegalArgumentException("未知类型: " + (char) b);
        };
    }

    //将String嵌入到图像的LSB中
    public static BufferedImage embedLSBImage (File imageFile, String text) throws IOException {
        //生成水印头 长度+类型+数据
        byte[] secretData = generateHeader(text);
        BufferedImage image = ImageIO.read(imageFile);
        checkSuitableSize(secretData, image);
        embedLSBImage(image, secretData);
        return image;
    }

    //从文件嵌入水印
    public static BufferedImage embedLSBImage (File imageFile, File secretFile) throws IOException {
        //生成水印头 长度+类型+数据
        byte[] secretData = generateHeader(secretFile);
        BufferedImage image = ImageIO.read(imageFile);
        //检查容量
        checkSuitableSize(secretData, image);
        embedLSBImage(image, secretData);
        return image;
    }

    //从图像中提取水印
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
            case n:
            case p:
            case w:
            case t:
                if(expectedType == byte[].class)
                    return expectedType.cast(secretData);
                break;
            default:
                throw new RuntimeException("类型读取有误");
        }
        return null;
    }

    //将bit数据嵌入
    private static void embedLSBImage (BufferedImage image, byte[] secretData){
        int bitIndex = 0; // 单byte的bit索引
        int dataIndex = 0; // byte数组索引
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                if(dataIndex >= secretData.length) return;
                RGB rgb = new RGB(image.getRGB(x, y));
                for(int i = 0; i < 3; i++){ //遍历R->G->B
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


    //生成水印头：4位长度+1位类型+数据
    private static byte[] generateHeader(Object obj) throws IOException {
        byte[] type;
        byte[] data;

        if (obj instanceof String) {
            type = MarkingType.s.toString().getBytes();
            data = ((String) obj).getBytes(CHARSET);
        } else if (obj instanceof File file) {
            if (file.getName().endsWith(".png")) {
                type = MarkingType.p.toString().getBytes();
            } else if (file.getName().endsWith(".mp3")) {
                type = MarkingType.n.toString().getBytes();
            } else if (file.getName().endsWith(".mp4")) {
                type = MarkingType.m.toString().getBytes();
            } else if (file.getName().endsWith(".wav")) {
                type = MarkingType.w.toString().getBytes();
            } else if(file.getName().endsWith(".txt")){
                type = MarkingType.t.toString().getBytes();
            } else {
                throw new RuntimeException("不支持的文件类型");
            }
            // 读取文件内容为字节数组
            data = Files.readAllBytes(file.toPath());
        } else {
            throw new RuntimeException("不支持的数据类型");
        }

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

    private static void checkSuitableSize(byte[] secretData, BufferedImage image) {
        //检查容量
        if (8L* secretData.length > 3L* image.getWidth() * image.getHeight()) {
            throw new RuntimeException("数据过大,无法注入图片");
        }
    }
}
