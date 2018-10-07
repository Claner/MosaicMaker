package mosaic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MosaicMaker {
    //图库路径
    private String dbPath;
    //目标图片路径
    private String aimPath;
    //图片输出路径
    private String outPath;
    //默认子图宽
    private int subWidth = 64;
    //默认子图高
    private int subHeight = 64;
    //粒度
    private int unitMin;
    //成像方式
    private String mode;
    //默认生成图宽
    private int defaultW;
    //默认生成图高
    private int defaultH;
    //每张素材最多出现的次数
    private int max;
    //加载图库使用的线程数
    private int threadNum;
    private Map<String, ImageInfo> map = new ConcurrentHashMap<>();

    public MosaicMaker(String dbPath, String aimPath, String outPath) {
        this(dbPath, aimPath, outPath, 64, 64, 5, "RGB", 1920, 1080, 300, 20);
    }

    public MosaicMaker(String dbPath, String aimPath, String outPath, int subWidth, int subHeight, int unitMin, String mode, int defaultW, int defaultH, int max, int threadNum) {
        this.dbPath = dbPath;
        this.aimPath = aimPath;
        this.outPath = outPath;
        this.subWidth = subWidth;
        this.subHeight = subHeight;
        this.unitMin = unitMin;
        this.mode = mode;
        this.defaultW = defaultW;
        this.defaultH = defaultH;
        this.max = max;
        this.threadNum = threadNum;
    }

    public String getDBPath() {
        return dbPath;
    }

    public void setDBPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getAimPath() {
        return aimPath;
    }

    public void setAimPath(String aimPath) {
        this.aimPath = aimPath;
    }

    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public int getSubWidth() {
        return subWidth;
    }

    public void setSubWidth(int subWidth) {
        this.subWidth = subWidth;
    }

    public int getSubHeight() {
        return subHeight;
    }

    public void setSubHeight(int subHeight) {
        this.subHeight = subHeight;
    }

    public int getUnitMin() {
        return unitMin;
    }

    public void setUnitMin(int unitMin) {
        this.unitMin = unitMin;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getDefaultW() {
        return defaultW;
    }

    public void setDefaultW(int defaultW) {
        this.defaultW = defaultW;
    }

    public int getDefaultH() {
        return defaultH;
    }

    public void setDefaultH(int defaultH) {
        this.defaultH = defaultH;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public void make() throws IOException {
        File aimFile = new File(aimPath);
        BufferedImage aimIm = ImageIO.read(aimFile);
        int aimWidth = aimIm.getWidth();
        int aimHeight = aimIm.getHeight();
        if (!calSubIm(aimWidth, aimHeight)) {
            aimWidth = defaultW;
            aimHeight = defaultH;
            //使用默认尺寸
            aimIm = ImageUtil.resize(aimIm, aimWidth, aimHeight);
        }
        readAllImage();
        core(aimIm);
    }

    private void core(BufferedImage aimIm) throws IOException {
        int width = aimIm.getWidth();
        int height = aimIm.getHeight();
        BufferedImage newIm = new BufferedImage(width, height, aimIm.getType());
        Graphics2D g = newIm.createGraphics();
        int w = width / subWidth;
        long start = System.currentTimeMillis();
        ExecutorService pool = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(w);
        for (int i = 0; i < w; i++) {
            int finalI = i;
            pool.execute(() -> {
                for (int j = 0; j < w; j++) {
//                    System.out.printf("正在拼第%d张图片\n", (finalI + 1) * (j + 1));
                    int x = finalI * subWidth;
                    int y = j * subHeight;
                    BufferedImage curAimSubIm = aimIm.getSubimage(x, y, subWidth, subHeight);
                    BufferedImage fitSubIm = findFitIm(curAimSubIm);
                    g.drawImage(fitSubIm, x, y, subWidth, subHeight, null);
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("计时器抛异常:");
        } finally {
            pool.shutdown();
        }
        System.out.println("拼图完成，耗时" + (System.currentTimeMillis() - start) + "毫秒");
        ImageUtil.save(newIm, outPath);
    }

    //搜索合适子图
    private BufferedImage findFitIm(BufferedImage image) {
        String[] keys = ImageUtil.calKey(image, mode).split("-");
        float r = Float.parseFloat(keys[0]);
        float g = Float.parseFloat(keys[1]);
        float b = Float.parseFloat(keys[2]);
        float min = Float.MAX_VALUE;
        String indexK = null;
        for (String k : map.keySet()) {
            String[] mk = k.split("-");
            float mr = Float.parseFloat(mk[0]);
            float mg = Float.parseFloat(mk[1]);
            float mb = Float.parseFloat(mk[2]);
            float curDif = Math.abs(mr - r) + Math.abs(mg - g) + Math.abs(mb - b);
            if (min > curDif && map.get(k).max > 0) {
                min = curDif;
                indexK = k;
            }
        }
        ImageInfo info = map.get(indexK);
        info.max = info.max - 1;
        return info.im;
    }

    //读取图库
    private void readAllImage() {
        File dir = new File(this.dbPath);
        File[] files = dir.listFiles();
        long start = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(threadNum);
        int size = files.length;
        ReadTask[] readTask = new ReadTask[threadNum];
        CountDownLatch latch = new CountDownLatch(threadNum);
        for (int i = 0; i < size; i++) {
            if (files[i].isFile()) {
                int index = i % threadNum;
                if (readTask[index] == null) readTask[index] = new ReadTask(latch, subWidth, subHeight);
                readTask[index].add(files[i]);
            }
        }
        System.out.println("开始加载图库");
        for (int i = 0; i < threadNum; i++) {
            pool.execute(readTask[i]);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
        System.out.println("共读取" + map.size() + "张图片");
        System.out.println("读取图库完成，耗时" + (System.currentTimeMillis() - start) + "毫秒");
    }

    private class ReadTask implements Runnable {

        private CountDownLatch latch;
        private List<File> files = new ArrayList<>();
        private int w;
        private int h;

        public ReadTask(CountDownLatch latch, int w, int h) {
            this.latch = latch;
            this.w = w;
            this.h = h;
        }

        public void add(File file) {
            files.add(file);
        }

        @Override
        public void run() {
            for (File f : files) {
                if (f.isFile()) {
                    BufferedImage im = null;
                    try {
                        im = ImageUtil.resize(ImageIO.read(f), w, h);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    map.put(ImageUtil.calKey(im, "RGB"), new ImageInfo(max, im));
                }
            }
            latch.countDown();
        }
    }

    /**
     * 计算子团尺寸
     */
    private boolean calSubIm(int w, int h) {
        boolean flag = true;
        int g = gcd(w, h);
        //g太小则使用默认尺寸
        if (g < 20) {
            flag = false;
            w = defaultW;
            h = defaultH;
            g = 320;
        }
        //长宽一样时使用320
        if (g == w) {
            g = 320;
        }
        subWidth = unitMin * (w / g);
        subHeight = unitMin * (h / g);
        return flag;
    }

    private int gcd(int a, int b) {
        int m = Math.max(a, b);
        int n = Math.min(a, b);
        int r = m % n;
        while (r != 0) {
            m = n;
            n = r;
            r = m % n;
        }
        // 返回最大公约数
        return n;
    }
}
