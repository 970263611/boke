package util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.Hashtable;

/**
 * 二维码生成和读的工具类
 *
 */
public class QrCodeCreateUtil {
    
	private static Logger logger = LoggerFactory.getLogger(QrCodeCreateUtil.class);
	
    /**
     * 生成包含字符串信息的二维码图片
     * @param outputStream 文件输出流路径
     * @param content 二维码携带信息
     * @param qrCodeSize 二维码图片大小
     * @param imageFormat 二维码的格式
     * @throws WriterException 
     * @throws IOException
     */
    public static boolean createQrCode(OutputStream outputStream, String content, int qrCodeSize, String imageFormat) throws WriterException, IOException {
            //设置二维码纠错级别ＭＡＰ
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);  // 矫错级别  
            QRCodeWriter qrCodeWriter = new QRCodeWriter();  
            //创建比特矩阵(位矩阵)的QR码编码的字符串  
            BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);  
            // 使BufferedImage勾画QRCode  (matrixWidth 是行二维码像素点)
            int matrixWidth = byteMatrix.getWidth();  
            BufferedImage image = new BufferedImage(matrixWidth-200, matrixWidth-200, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();  
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, matrixWidth, matrixWidth);  
            // 使用比特矩阵画并保存图像
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < matrixWidth; i++){
                for (int j = 0; j < matrixWidth; j++){
                    if (byteMatrix.get(i, j)){
                        graphics.fillRect(i-100, j-100, 1, 1);  
                    }
                }
            }
            return ImageIO.write(image, imageFormat, outputStream);
    }  
      
    /**
     * 读二维码并输出携带的信息
     */
    public static void readQrCode(InputStream inputStream) throws IOException {
        //从输入流中获取字符串信息
        BufferedImage image = ImageIO.read(inputStream);
        //将图像转换为二进制位图源
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));  
        QRCodeReader reader = new QRCodeReader();  
        Result result = null ;  
        try {
         result = reader.decode(bitmap);  
        } catch (ReaderException e) {
            e.printStackTrace();  
        }
        System.out.println(result.getText());
    }
    
    /**
     *生成二维码 自带博客需要唯一参数 
     */
    public static void CreateQrCodeByHua(String jsonString, String imgName) {
    	try {
    		//createQrCode(new FileOutputStream(new File("C:\\Users\\97026\\Desktop\\"+imgName+".jpg")),jsonString,900,"JPEG");
    		createQrCode(new FileOutputStream(new File("/root/images/"+ "erweima" + "/"+imgName+".jpg")),jsonString,900,"JPEG");
		} catch (FileNotFoundException e) {
			logger.info(new Date()+"二维码生成文件失败");
			System.out.println(new Date()+"二维码生成文件失败");
			e.printStackTrace();
		} catch (WriterException e) {
			logger.info(new Date()+"二维码写入文件失败");
			System.out.println(new Date()+"二维码写入文件失败");
			e.printStackTrace();
		} catch (IOException e) {
			logger.info(new Date()+"二维码失败，原因未知");
			System.out.println(new Date()+"二维码失败，原因未知");
			e.printStackTrace();
		}
    }
    
    public static void ReadQrCodeByHua(String imgUrl) {
    	try {
			readQrCode(new FileInputStream(new File(imgUrl)));
		} catch (FileNotFoundException e) {
			logger.info(new Date()+"二维码读取文件失败");
			System.out.println(new Date()+"二维码读取文件失败");
			e.printStackTrace();
		} catch (IOException e) {
			logger.info(new Date()+"二维码读取文件失败，原因未知");
			System.out.println(new Date()+"二维码读取文件失败，原因未知");
			e.printStackTrace();
		}
    }
    
    /**
     * 测试代码
     * @throws WriterException 
     */
    public static void main(String[] args) throws IOException, WriterException {
        
        createQrCode(new FileOutputStream(new File("C:\\Users\\love-ding\\Desktop\\qrcode.jpg")),StaticAddressUtil.yuming + "/index",900,"JPEG");
        //readQrCode(new FileInputStream(new File("C:\\Users\\love-ding\\Desktop\\qrcode.jpg")));  
    }  
  
}
