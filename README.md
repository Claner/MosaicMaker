# MosaicMaker
使用Java实现的马赛克拼图

## 使用
```
        MosaicMaker mosaicMaker = new MosaicMaker("图库路径", "目标图片路径", "输出路径");
        try {
            mosaicMaker.make();
        } catch (IOException e) {
            e.printStackTrace();
        }
```

## 原图
![image](https://github.com/Claner/MosaicMaker/blob/master/src/mosaic/YUI.jpg)

## 效果图(平均rgb值)
![image](https://github.com/Claner/MosaicMaker/blob/master/src/mosaic/YUI-5.jpg)

## 效果图(平均值)
![image](https://github.com/Claner/MosaicMaker/blob/master/src/mosaic/YUI-gray-5.jpg)

## 参数设置
```
//设置拼图粒度，取值范围1~20较为合适，值越小拼图所用的素材越多
mosaicMaker.setUnitMin(10);

//设置加载图库使用的线程数
mosaicMaker.setThreadNum(20);

//设置拼图模式（GRAY，RGB，PHASH），默认为RGB
mosaicMaker.setMode(Mode.RGB);

//设置每个素材最多出现的次数
mosaicMaker.setMax(30);

//设置最终拼图的默认宽高，仅在图片尺寸无法计算出有效最大公约数的情况下有效
mosaicMaker.setDefaultW(1920);
mosaicMaker.setDefaultH(1080);
```


## 基本思路
1：根据目标图片的尺寸计算出合适的素材大小，如目标图片尺寸为1600x1280。计算出最大公约数为320，则素材的尺寸为（1600/320 = 5）x（1280/320 = 4）。
为了防止素材的尺寸过小，设置一个参数unitMin（默认为5），素材的最终宽高为25x20。
2：读取图库中的所有素材，并将素材缩放到25x20。计算缩放后的key值，key值的计算方式有三种。平均rgb值，平均灰度值，图片指纹。将图片信息和key值一一对应
保存在map中。
3：划分目标图片，计算每个子块的key值，在map中搜索最合适的素材进行替换。
4：保存图片
