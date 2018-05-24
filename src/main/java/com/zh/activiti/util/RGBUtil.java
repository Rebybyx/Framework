package com.zh.activiti.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.PixelGrabber;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by rootming on 2017/11/07.
 */
public class RGBUtil {

    /**
     * @param image * @param bandOffset 用于判断通道顺序
     *              * @return
     */
    private static boolean equalBandOffsetWith3Byte(BufferedImage image, int[] bandOffset) {
        if (image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            if (image.getData().getSampleModel() instanceof ComponentSampleModel) {
                ComponentSampleModel sampleModel = (ComponentSampleModel) image.getData().getSampleModel();
                if (Arrays.equals(sampleModel.getBandOffsets(), bandOffset)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断图像是否为BGR格式
     * * @return
     */
    public static boolean isBGR3Byte(BufferedImage image) {
        return equalBandOffsetWith3Byte(image, new int[]{0, 1, 2});
    }

    /**
     * 判断图像是否为RGB格式
     * * @return
     */
    public static boolean isRGB3Byte(BufferedImage image) {
        return equalBandOffsetWith3Byte(image, new int[]{2, 1, 0});
    }


    /**
     * 对图像解码返回RGB格式矩阵数据,数组的大小为 （（宽度＋7）/8）×高度×颜色数
     * * @param image
     * * @return
     */
    public static byte[] getMatrixRGB(BufferedImage image) {
        byte red;
        byte blue;
        byte green;

        if (null == image)
            throw new NullPointerException();
        int width = image.getWidth();
        int height = image.getHeight();
        int imageChannelSize = width * height / 8;
        ByteBuffer rbuf = ByteBuffer.allocate(imageChannelSize);
        ByteBuffer gbuf = ByteBuffer.allocate(imageChannelSize);
        ByteBuffer bbuf = ByteBuffer.allocate(imageChannelSize);
        ByteBuffer ImageBuffer = ByteBuffer.allocate(imageChannelSize * 3);
        // 遍历像素
        for (int i = 0; i < height; i++) {
            for (int i1 = 0; i1 < width; i1 += 8) {
                red = 0;
                blue = 0;
                green = 0;
                for (int j = 0; j < 8; j++) {
                    // 一次遍历8个像素点, 将8个像素的内容压缩到一个字节里
                    red = red <<= 1;
                    blue = blue <<= 1;
                    green = green <<= 1;
                    Object data;
                    try {
                        //获取该点像素，并以object类型表示
                        data = image.getRaster().getDataElements(i1 + j, i, null);
                        red = (byte) (red | (image.getColorModel().getRed(data) > 30 ? 1 : 0));
                        blue = (byte) (blue | (image.getColorModel().getBlue(data) > 30 ? 1 : 0));
                        green = (byte) (green | (image.getColorModel().getGreen(data) > 30 ? 1 : 0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                rbuf.put(red);
                gbuf.put(green);
                bbuf.put(blue);
            }
        }
        byte[] rbytes = rbuf.array();
        byte[] gbytes = gbuf.array();
        byte[] bbytes = bbuf.array();
        ImageBuffer.put(rbytes);
        ImageBuffer.put(gbytes);
        ImageBuffer.put(bbytes);
        return ImageBuffer.array();
    }


    /**
     * 对图像解码返回BGR格式矩阵数据
     * * @param image
     * * @return
     */
    public static byte[] getMatrixBGR(BufferedImage image) {
        if (null == image)
            throw new NullPointerException();
        byte[] matrixBGR;
        if (isBGR3Byte(image)) {
            matrixBGR = (byte[]) image.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
        } else {
            // ARGB格式图像数据
            int intrgb[] = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            matrixBGR = new byte[image.getWidth() * image.getHeight() * 3];
            // ARGB转BGR格式
            for (int i = 0, j = 0; i < intrgb.length; ++i, j += 3) {
                matrixBGR[j] = (byte) (intrgb[i] & 0xff);
                matrixBGR[j + 1] = (byte) ((intrgb[i] >> 8) & 0xff);
                matrixBGR[j + 2] = (byte) ((intrgb[i] >> 16) & 0xff);
            }
        }
        return matrixBGR;
    }

    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent
        // Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the
        // screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image
                    .getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image
                    .getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

}
