package mosaic;

import java.awt.image.BufferedImage;

/**
 * Created by Clanner on 2018/10/6.
 */
public class ImageInfo implements Comparable<ImageInfo> {
    public int max;
    private String mode;
    private String key;
    public BufferedImage im;

    public ImageInfo(int max, BufferedImage im) {
        this.max = max;
        this.im = im;
    }

    public ImageInfo(int max, String mode, String key, BufferedImage im) {
        this.max = max;
        this.mode = mode;
        this.key = key;
        this.im = im;
    }

    //返回1表示当前值大于比较值，返回-1表示当前值小于比较值,返回0表示相等
    @Override
    public int compareTo(ImageInfo o) {
        switch (o.mode) {
            case Mode.RGB:
                String[] curKeys = this.key.split("-");
                float r = Float.parseFloat(curKeys[0]);
                float g = Float.parseFloat(curKeys[1]);
                float b = Float.parseFloat(curKeys[2]);
                String[] mk = o.key.split("-");
                float mr = Float.parseFloat(mk[0]);
                float mg = Float.parseFloat(mk[1]);
                float mb = Float.parseFloat(mk[2]);
                float dif = (mr - r) + (mg - g) + (mb - b);
                if (dif > 0) {
                    return 1;
                } else if (dif < 0) {
                    return -1;
                } else {
                    return 0;
                }
            case Mode.GRAY:
                float f = Float.parseFloat(this.key) - Float.parseFloat(o.key);
                if (f > 0) {
                    return 1;
                } else if (f < 0) {
                    return -1;
                } else {
                    return 0;
                }
            case Mode.PHASH:
                int length = this.key.length();
                int d = 0;
                for (int i = 0; i < length; i++) {
                    if (this.key.charAt(i) != o.key.charAt(i)) d++;
                }
                //d表示汉明距离
                if (d > 5) {
                    return 1;
                } else if (d < 5) {
                    return -1;
                } else {
                    return 0;
                }
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "key='" + key +
                '}';
    }
}
