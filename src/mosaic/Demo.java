package mosaic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Clanner on 2018/10/6.
 */
public class Demo {
    public static void main(String[] args) throws IOException {
        MosaicMaker mosaicMaker = new MosaicMaker("G:\\avatar", "G:\\image\\YUI.jpg", "G:\\image\\YUI-5.jpg");
        try {
            mosaicMaker.make();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
