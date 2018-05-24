package com.sawallianc.redis;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

public class QRCodeUtil {
    public static void main(String[] args) throws Exception{
        parse();
        generate();
    }

    private static void parse() throws Exception{
        MultiFormatReader formatReader=new MultiFormatReader();

        File file=new File("C:\\xingbianli.png");
        BufferedImage image= ImageIO.read(file);

        BinaryBitmap binaryBitmap=new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));

        //定义二维码的参数:
        HashMap hints=new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET,"utf-8");//定义字符集

        Result result=formatReader.decode(binaryBitmap,hints);//开始解析

        System.out.println("解析结果:"+result.toString());
        System.out.println("二维码的格式类型是:"+result.getBarcodeFormat());
        System.out.println("二维码的文本内容是:"+result.getText());
    }

    private static void generate() throws Exception{
        int height=300;
        int width=300;//图片大小
        String format="png";//图片格式
        String content="https://www.xingbianli.com/openrack/eb60d7bc-37eb-4caf-ae13-11d2ed2ab343/buy";//内容

        //定义二维码的参数:
        HashMap hints=new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET,"utf-8");//定义字符集
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//定义纠错级别
//        hints.put(EncodeHintType.MARGIN,2);//定义边距为2

        BitMatrix bitMatrix=new MultiFormatWriter().encode(content,BarcodeFormat.QR_CODE,width,height);//开始生成二维码

        Path file=new File("C:/img.png").toPath();//指定保存路径
        MatrixToImageWriter.writeToPath(bitMatrix,format,file);
    }

}
