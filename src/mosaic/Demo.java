package mosaic;
import java.io.IOException;

/**
 * Created by Clanner on 2018/10/6.
 */
public class Demo {
    public static void main(String[] args) throws IOException {
        MosaicMaker mosaicMaker = new MosaicMaker("G:\\avatar", "G:\\image\\YUI.jpg", "G:\\image\\YUI-tree-gray-5.jpg");
        mosaicMaker.setUseTree(true);
        mosaicMaker.setMode(Mode.GRAY);
        try {
            mosaicMaker.make();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
