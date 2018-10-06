package mosaic;

import java.io.IOException;

/**
 * Created by Clanner on 2018/10/6.
 */
public class Demo {
    public static void main(String[] args) {
        MosaicMaker mosaicMaker = new MosaicMaker("G:\\avatar", "G:\\image\\YUI.jpg", "G:\\image\\YUI-5.jpg");
        mosaicMaker.setThreadNum(20);
        mosaicMaker.setMax(20);
        try {
            mosaicMaker.make();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
