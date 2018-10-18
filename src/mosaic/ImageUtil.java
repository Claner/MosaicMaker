package mosaic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

public class ImageUtil {

    public static final void save(BufferedImage image, String outPath) throws IOException {
        File outFile = new File(outPath);
        ImageIO.write(image, "JPEG", outFile);
    }

    public static final String calKey(BufferedImage image, String mode) {
        switch (mode) {
            case Mode.GRAY:
                return "" + calAvgGRAY(image);
            case Mode.RGB:
                float[] res = calAvgRGB(image);
                return res[0] + "-" + res[1] + "-" + res[2];
            case Mode.PHASH:
                return calPHash(image);
            default:
                return "";
        }
    }

    //计算平均rgb
    private static float calAvgGRAY(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        float avgGray = 0.f;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int pixel = image.getRGB(x, y);
                int r = (pixel & 0xff0000) >> 16;
                int g = (pixel & 0xff00) >> 8;
                int b = (pixel & 0xff);
                avgGray += (77 * r + 150 * g + 29 * b + 128) >> 8;
            }
        }
        return avgGray / (w * h);
    }

    //计算平均灰度值
    private static float[] calAvgRGB(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        float[] res = new float[3];
        float avgR = 0.f;
        float avgG = 0.f;
        float avgB = 0.f;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int pixel = image.getRGB(x, y);
                int r = (pixel & 0xff0000) >> 16;
                int g = (pixel & 0xff00) >> 8;
                int b = (pixel & 0xff);
                avgR += r;
                avgG += g;
                avgB += b;
            }
        }
        res[0] = avgR / (w * h);
        res[1] = avgG / (w * h);
        res[2] = avgB / (w * h);
        return res;
    }

    private static String calPHash(BufferedImage image) {
        image = ImageUtil.resize(image, 8, 8);
        float avgGray = calAvgGRAY(image);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int pixel = image.getRGB(i, j);
                int r = (pixel & 0xff0000) >> 16;
                int g = (pixel & 0xff00) >> 8;
                int b = (pixel & 0xff);
                float curGray = (77 * r + 150 * g + 29 * b + 128) >> 8;
                if (curGray > avgGray) {
                    builder.append("1");
                } else {
                    builder.append("0");
                }
            }
        }
        return builder.toString();
    }

    //透明
    public static final BufferedImage blend(BufferedImage im1, BufferedImage im2, float a) {
        int width = im1.getWidth();
        int height = im1.getHeight();
        //尺寸不一样则返回空
        if (width != im2.getWidth() || height != im2.getHeight()) return null;
        int alpha = (int) (a * 100);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel1 = im1.getRGB(x, y);
                int pixel2 = im2.getRGB(x, y);
                int r1 = (pixel1 & 0xff0000) >> 16;
                int g1 = (pixel1 & 0xff00) >> 8;
                int b1 = (pixel1 & 0xff);
                int r2 = (pixel2 & 0xff0000) >> 16;
                int g2 = (pixel2 & 0xff00) >> 8;
                int b2 = (pixel2 & 0xff);
                int r = ((100 - alpha) * r1 + alpha * r2) / 100;
                int g = ((100 - alpha) * g1 + alpha * g2) / 100;
                int b = ((100 - alpha) * b1 + alpha * b2) / 100;
                int rgb = Integer.valueOf(Integer.toHexString(r & 0xff) + Integer.toHexString(g & 0xff) + Integer.toHexString(b & 0xff), 16);
                im1.setRGB(x, y, rgb);
            }
        }
        return im1;
    }

    public static final BufferedImage resize(BufferedImage im, int w, int h) {
        Image image = im.getScaledInstance(w, h, Image.SCALE_FAST);
        return convertToBufferImage(image, w, h);
    }

    private static BufferedImage convertToBufferImage(Image image, int w, int h) {
        BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
