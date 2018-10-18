package mosaic;

import java.awt.image.BufferedImage;

/**
 * Created by Clanner on 2018/10/6.
 */
public class ImageInfo implements Comparable<ImageInfo> {
    public int max;
    public String mode;
    public String key;
    public float dif = 0;
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
                dif = calDif(o);
                float door = 78.0f;
                if (dif > door) {
                    return 1;
                } else if (dif < door) {
                    return -1;
                } else {
                    return 0;
                }
            case Mode.GRAY:
                Float cur = Float.parseFloat(this.key);
                Float cmp = Float.parseFloat(o.key);
                if (cur > cmp) {
                    return 1;
                } else if (cur < cmp) {
                    return -1;
                } else {
                    return 0;
                }
            case Mode.PHASH:
                //d表示汉明距离
                float d = calDif(o);
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

    private float calDif(ImageInfo o) {
        switch (mode) {
            case Mode.RGB:
                String[] curKeys = this.key.split("-");
                float r = Float.parseFloat(curKeys[0]);
                float g = Float.parseFloat(curKeys[1]);
                float b = Float.parseFloat(curKeys[2]);
                String[] mk = o.key.split("-");
                float mr = Float.parseFloat(mk[0]);
                float mg = Float.parseFloat(mk[1]);
                float mb = Float.parseFloat(mk[2]);
                return (Math.abs(mr - r) + Math.abs(mg - g) + Math.abs(mb - b));
            case Mode.GRAY:
                return Math.abs(Float.parseFloat(this.key) - Float.parseFloat(o.key));
            case Mode.PHASH:
                int length = this.key.length();
                int d = 0;
                for (int i = 0; i < length; i++) {
                    if (this.key.charAt(i) != o.key.charAt(i)) d++;
                }
                return d;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "key=" + key +
                '}';
    }
}
