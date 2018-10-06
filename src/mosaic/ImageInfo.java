package mosaic;

import java.awt.image.BufferedImage;

/**
 * Created by Clanner on 2018/10/6.
 */
public class ImageInfo {
    public int max;
    public BufferedImage im;

    public ImageInfo(int max, BufferedImage im) {
        this.max = max;
        this.im = im;
    }
}
