package com.bfei.icrane.common.util;

import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.DivinationImage;
import com.bfei.icrane.core.models.DivinationTopic;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.service.impl.AliyunServiceImpl;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;

/**
 * @Description:图片处理工具
 * @author:liuyc
 * @time:2016年5月27日 上午10:18:00
 */
public class ImageHandleHelper {

    /**
     * @param srcFile 源图片、targetFile截好后图片全名、startAcross 开始截取位置横坐标、StartEndlong开始截图位置纵坐标、width截取的长，hight截取的高
     * @Description:截图
     * @author:liuyc
     * @time:2016年5月27日 上午10:18:23
     */
    public static void cutImage(String srcFile, String targetFile, int startAcross, int StartEndlong, int width,
                                int hight) throws Exception {
        // 取得图片读入器
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader reader = readers.next();
        // 取得图片读入流
        InputStream source = new FileInputStream(srcFile);
        ImageInputStream iis = ImageIO.createImageInputStream(source);
        reader.setInput(iis, true);
        // 图片参数对象
        ImageReadParam param = reader.getDefaultReadParam();
        Rectangle rect = new Rectangle(startAcross, StartEndlong, width, hight);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        ImageIO.write(bi, targetFile.split("\\.")[1], new File(targetFile));
    }

    /**
     * @param files 要拼接的文件列表
     * @param type 1 横向拼接， 2 纵向拼接
     * @Description:图片拼接 （注意：必须两张图片长宽一致哦）
     * @author:liuyc
     * @time:2016年5月27日 下午5:52:24
     */
    public static void mergeImage(String[] files, int type, String targetFile) {
        int len = files.length;
        if (len < 1) {
            throw new RuntimeException("图片数量小于1");
        }
        File[] src = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
                src[i] = new File(files[i]);
                images[i] = ImageIO.read(src[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            ImageArrays[i] = new int[width * height];
            ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);
        }
        int newHeight = 0;
        int newWidth = 0;
        for (int i = 0; i < images.length; i++) {
            // 横向
            if (type == 1) {
                newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();
                newWidth += images[i].getWidth();
            } else if (type == 2) {// 纵向
                newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();
                newHeight += images[i].getHeight();
            }
        }
        if (type == 1 && newWidth < 1) {
            return;
        }
        if (type == 2 && newHeight < 1) {
            return;
        }

        // 生成新图片
        try {
            BufferedImage ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            int width_i = 0;
            for (int i = 0; i < images.length; i++) {
                if (type == 1) {
                    ImageNew.setRGB(width_i, 0, images[i].getWidth(), newHeight, ImageArrays[i], 0,
                            images[i].getWidth());
                    width_i += images[i].getWidth();
                } else if (type == 2) {
                    ImageNew.setRGB(0, height_i, newWidth, images[i].getHeight(), ImageArrays[i], 0, newWidth);
                    height_i += images[i].getHeight();
                }
            }
            //输出想要的图片
            ImageIO.write(ImageNew, targetFile.split("\\.")[1], new File(targetFile));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:小图片贴到大图片形成一张图(合成)
     * @author:liuyc
     * @time:2016年5月27日 下午5:51:20
     */
    public static final void overlapImage(String bigPath, String smallPath, String outFile, Integer x, Integer y) {
        try {
            BufferedImage big = ImageIO.read(new File(bigPath));
            BufferedImage small = ImageIO.read(new File(smallPath));
            Graphics2D g = big.createGraphics();
            if (x == null) {
                x = (big.getWidth() - small.getWidth()) / 2;
            }
            if (y == null) {
                y = (big.getHeight() - small.getHeight()) / 2;
            }
            g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
            g.dispose();
            ImageIO.write(big, outFile.split("\\.")[1], new File(outFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 图片添加水印
     *
     * @param srcImgPath       需要添加水印的图片的路径
     * @param outImgPath       添加水印后图片输出路径
     * @param markContentColor 水印文字的颜色
     * @param waterMarkContent 水印的文字
     */
    public void mark(String srcImgPath, String outImgPath, Color markContentColor, String waterMarkContent) {
        try {
            // 读取原图片信息
            File srcImgFile = new File(srcImgPath);
            Image srcImg = ImageIO.read(srcImgFile);
            int srcImgWidth = srcImg.getWidth(null);
            int srcImgHeight = srcImg.getHeight(null);
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            //Font font = new Font("Courier New", Font.PLAIN, 12);
            Font font = new Font("宋体", Font.PLAIN, 50);
            g.setColor(markContentColor); //根据图片的背景设置水印颜色

            g.setFont(font);
            int x = srcImgWidth - getWatermarkLength(waterMarkContent, g) - 3;
            int y = srcImgHeight - 3;
            //int x = (srcImgWidth - getWatermarkLength(watermarkStr, g)) / 2;
            //int y = srcImgHeight / 2;
            g.drawString(waterMarkContent, x, y);
            g.dispose();
            // 输出图片
            FileOutputStream outImgStream = new FileOutputStream(outImgPath);
            ImageIO.write(bufImg, "jpg", outImgStream);
            outImgStream.flush();
            outImgStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取水印文字总长度
     *
     * @param waterMarkContent 水印的文字
     * @param g
     * @return 水印文字总长度
     */
    public int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }

    /*
   * 函数名：getFile
   * 作用：使用递归，输出指定文件夹内的所有文件
   * 参数：path：文件夹路径   deep：表示文件的层次深度，控制前置空格的个数
   * 前置空格缩进，显示文件层次结构
   */
    public void getFile(String path, int deep) {
        // 获得指定文件对象
        File file = new File(path);
        // 获得该文件夹内的所有文件
        File[] array = file.listFiles();

        for (int i = 0; i < array.length; i++) {
            if (array[i].isFile())//如果是文件
            {
                for (int j = 0; j < deep; j++)//输出前置空格
                    System.out.print(" ");
                // 只输出文件名字
                System.out.println(array[i].getName());
                // 输出当前文件的完整路径
                System.out.println("#####" + array[i]);
                // 同样输出当前文件的完整路径   大家可以去掉注释 测试一下
                System.out.println(array[i].getPath());
            } else if (array[i].isDirectory())//如果是文件夹
            {
                for (int j = 0; j < deep; j++)//输出前置空格
                    System.out.print(" ");

                System.out.println(array[i].getName());
                System.out.println(array[i].getPath());
                //文件夹需要调用递归 ，深度+1
                //getFile(array[i].getPath(), deep + 1);
            }
        }
    }


    public Font getDefinedFont(int ft, float fs) {

        Font definedFont = null;
        String fontUrl = "/home/font/hkwwtW5.TTF";
        /*switch (ft) {
            case 1:
                fontUrl = "hkwwtW5.TTF";//华文行楷
                break;
            case 2:
                fontUrl = "hkwwtW5.TTF";//华文行楷
                break;
            default:
                String fonturllocal = "hkwwtW5.TTF";
                if (!new File(fonturllocal).exists()) {
                    fontUrl = "ClassPath:/";
                } else {
                    fontUrl = fonturllocal;
                }
                break;
        }*/
        if (definedFont == null) {
            InputStream is = null;
            BufferedInputStream bis = null;
            try {
                is = new FileInputStream(new File(fontUrl));
                bis = new BufferedInputStream(is);
                definedFont = Font.createFont(Font.TRUETYPE_FONT, is);
                //设置字体大小，float型
                definedFont = definedFont.deriveFont(Font.BOLD, fs);
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != bis) {
                        bis.close();
                    }
                    if (null != is) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return definedFont;
    }

    public static void MyDrawString(String str, int x, int y, double rate, Graphics g) {
        String tempStr = new String();
        int orgStringWight = g.getFontMetrics().stringWidth(str);
        int orgStringLength = str.length();
        int tempx = x;
        int tempy = y;
        while (str.length() > 0) {
            tempStr = str.substring(0, 1);
            str = str.substring(1, str.length());
            g.drawString(tempStr, tempx, tempy);
            tempx = (int) (tempx + (double) orgStringWight / (double) orgStringLength * rate);
        }
    }


    public BufferedImage geticon(String iconRealPath) {
        URL url = null;
        try {
            if (StringUtils.isNotEmpty(iconRealPath)) {
                url = new URL(iconRealPath);
                BufferedImage icon = ImageIO.read(url);
                return getRoundedImage(icon, 100, 8, 2);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage icon = new BufferedImage(1, 1, 1);
        return getRoundedImage(icon, 95, 8, 2);
    }

    public BufferedImage getQRCard(String iconRealPath, int size) {
        URL url = null;
        try {
            if (StringUtils.isNotEmpty(iconRealPath)) {
                url = new URL(iconRealPath);
                BufferedImage icon = ImageIO.read(url);
                return getRoundedImage(icon, size, 0, 2);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage icon = new BufferedImage(1, 1, 1);
        return getRoundedImage(icon, 95, 8, 2);
    }

    public BufferedImage getImg(String resultPath) {
        URL url = null;
        try {
            url = new URL(resultPath);
            return ImageIO.read(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 先按最小宽高为size等比例绽放,      然后图像居中抠出半径为radius的圆形图像
     *
     * @param img
     * @param size   要缩放到的尺寸
     * @param radius 圆角半径
     * @param type   1:高度与宽度的最大值为maxSize进行等比缩放      ,      2:高度与宽度的最小值为maxSize进行等比缩放
     * @return
     */
    private static BufferedImage getRoundedImage(BufferedImage img, int size, int radius, int type) {

        BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();

        //先按最小宽高为size等比例绽放,      然后图像居中抠出直径为size的圆形图像
        Image fixedImg = getScaledImage(img, size, type);
        g.drawImage(fixedImg, (size - fixedImg.getWidth(null)) / 2, (size - fixedImg.getHeight(null)) / 2, null);//在适当的位置画出

        //圆角
        if (radius > 0) {
            RoundRectangle2D round = new RoundRectangle2D.Double(0, 0, size, size, radius * 2, radius * 2);
            Area clear = new Area(new Rectangle(0, 0, size, size));
            clear.subtract(new Area(round));
            g.setComposite(AlphaComposite.Clear);

            //抗锯齿
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.fill(clear);
            g.dispose();
        }
        return result;
    }

    /**
     * 针对高度与宽度进行等比缩放
     *
     * @param img
     * @param maxSize 要缩放到的尺寸
     * @param type    1:高度与宽度的最大值为maxSize进行等比缩放 , 2:高度与宽度的最小值为maxSize进行等比缩放
     * @return
     */
    private static Image getScaledImage(BufferedImage img, int maxSize, int type) {
        int w0 = img.getWidth();
        int h0 = img.getHeight();
        int w = w0;
        int h = h0;
        if (type == 1) {
            w = w0 > h0 ? maxSize : (maxSize * w0 / h0);
            h = w0 > h0 ? (maxSize * h0 / w0) : maxSize;
        } else if (type == 2) {
            w = w0 > h0 ? (maxSize * w0 / h0) : maxSize;
            h = w0 > h0 ? maxSize : (maxSize * h0 / w0);
        }
        Image image = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, null);//在适当的位置画出  
        return result;
    }

    public String getImgUrl(DivinationTopic divinationTopic, String divinationTopicImg, Member member, String qrUrl) {
        BufferedImage big = getImg(divinationTopic.getModeUrl());
        BufferedImage small = getImg(divinationTopicImg);
        BufferedImage icon = geticon(member.getIconRealPath());
        BufferedImage qrCard = getQRCard(qrUrl, 120);
        String waterMarkContent = divinationTopic.getDivinationName();
        Graphics2D g = big.createGraphics();
        //字体抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = (big.getWidth() - small.getWidth()) / 2;
        int y = 323 - (small.getHeight() - 473) / 2;
        //贴结果
        g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
        x = 84;
        y = 184;
        //贴头像
        g.drawImage(icon, x, y, icon.getWidth(), icon.getHeight(), null);
        x = 445;
        y = 818;
        //贴二维码
        g.drawImage(qrCard, x, y, qrCard.getWidth(), qrCard.getHeight(), null);
        //贴标题
        Font font = getDefinedFont(2, 46);
        g.setColor(new Color(44, 44, 44)); //根据图片的背景设置水印颜色
        g.setFont(font);
        x = (big.getWidth() - (int) ((double) getWatermarkLength(waterMarkContent, g) * 0.9)) / 2;
        y = 92;
        //g.drawString(waterMarkContent, x, y);
        String tempStr;
        int orgStringWight = g.getFontMetrics().stringWidth(waterMarkContent);
        int orgStringLength = waterMarkContent.length();
        int tempx = x;
        int tempy = y;
        while (waterMarkContent.length() > 0) {
            tempStr = waterMarkContent.substring(0, 1);
            waterMarkContent = waterMarkContent.substring(1, waterMarkContent.length());
            g.drawString(tempStr, tempx, tempy);
            tempx = (int) (tempx + (double) orgStringWight / (double) orgStringLength * 0.9);
        }
        //贴用户信息
        String sMember = "ID：" + member.getMemberID();
        Font font2 = getDefinedFont(2, 30);
        g.setColor(new Color(66, 66, 66)); //根据图片的背景设置水印颜色
        g.setFont(font2);
        orgStringWight = g.getFontMetrics().stringWidth(sMember);
        orgStringLength = sMember.length();
        x = 230;
        y = 230;
        while (sMember.length() > 0) {
            tempStr = sMember.substring(0, 1);
            sMember = sMember.substring(1, sMember.length());
            g.drawString(tempStr, x, y);
            x = (int) (x + (double) orgStringWight / (double) orgStringLength * 0.85);
        }
        //贴名字
        String sName = "昵称：" + (StringUtils.isEmpty(member.getName()) ? "不愿意透露姓名的玩家" : member.getName());
        Font font3 = getDefinedFont(2, 30);
        g.setColor(new Color(66, 66, 66)); //根据图片的背景设置水印颜色
        g.setFont(font3);
        orgStringWight = g.getFontMetrics().stringWidth(sName);
        orgStringLength = sName.length();
        x = 230;
        y = 270;
        while (sName.length() > 0) {
            tempStr = sName.substring(0, 1);
            sName = sName.substring(1, sName.length());
            g.drawString(tempStr, x, y);
            x = (int) (x + (double) orgStringWight / (double) orgStringLength * 0.85);
            if (x > 600) {
                x = 230;
                y = 310;
            }
        }
        g.dispose();
        //上传云
        PropFileManager propFileMgr = new PropFileManager("interface.properties");
        String ossBucketName = propFileMgr.getProperty("aliyun.ossBucketName");
        String fileKey = StringUtils.getRandomUUID();
        String NewFileKey = fileKey + ".jpg";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(big, "123.jpg".split("\\.")[1], os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        AliyunServiceImpl.getInstance().putFileStreamToOSS(ossBucketName, NewFileKey, is);
        String newFileUrl = AliyunServiceImpl.getInstance().generatePresignedUrl(ossBucketName, NewFileKey, divinationTopic.getWxpireTime() / 3600 + 1).toString();
        return newFileUrl;
    }


    public String getshareUrl(Member member, String qrUrl, Integer version) {
        File baseImgFile  = new File("/home/share/1_3/20180608140425.jpg");
        /*if (version == null) {
            baseImgFile // 原始图片文件
        } else if (version == 2) {
            baseImgFile = new File("/home/share/1_3/xiaoyaojing_dai.jpg"); // 原始图片文件
        } else {
            baseImgFile = new File("/home/share/1_3/365_dai.jpg"); // 原始图片文件
        }*/
        BufferedImage baseImg = null;
        try {
            baseImg = ImageIO.read(baseImgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage qrCard = getQRCard(qrUrl, 359);
        String MemberID = member.getMemberID();
        Graphics2D g = baseImg.createGraphics();
        //字体抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = (baseImg.getWidth() - qrCard.getWidth()) / 2;
        int y = 647;
        //贴二维码
        g.drawImage(qrCard, x, y, qrCard.getWidth(), qrCard.getHeight(), null);
        //贴名字
        int orgStringWight = g.getFontMetrics().stringWidth(MemberID);
        int orgStringLength = MemberID.length();
        Font font = getDefinedFont(2, 64);
        g.setColor(new Color(0, 0, 0)); //根据图片的背景设置水印颜色
        g.setFont(font);
        String tempStr;
        orgStringWight = g.getFontMetrics().stringWidth(MemberID);
        orgStringLength = MemberID.length();
        x = (int) (baseImg.getWidth() - orgStringWight * 0.9) / 2;
        y = 1490;
        while (MemberID.length() > 0) {
            tempStr = MemberID.substring(0, 1);
            MemberID = MemberID.substring(1, MemberID.length());
            g.drawString(tempStr, x, y);
            x = (int) (x + (double) orgStringWight / (double) orgStringLength * 0.9);
        }
        g.dispose();


        //上传云
        PropFileManager propFileMgr = new PropFileManager("interface.properties");
        String ossBucketName = propFileMgr.getProperty("aliyun.ossBucketName");
        String fileKey = StringUtils.getRandomUUID();
        String NewFileKey = fileKey + ".jpg";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(baseImg, "123.jpg".split("\\.")[1], os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        AliyunServiceImpl.getInstance().putFileStreamToOSS(ossBucketName, NewFileKey, is);
        String newFileUrl = AliyunServiceImpl.getInstance().generatePresignedUrl(ossBucketName, NewFileKey, 10 * 24 * 365
        ).toString();
        return newFileUrl;
    }

    public String getAgentShareUrl(Agent agent, String qrUrl, Integer version) {
        File baseImgFile  = new File("/home/share/1_3/20180608140425.jpg");
       /* if (version == null) {
            baseImgFile = new File("/home/share/1_3/365_dai.jpg"); // 原始图片文件
        } else if (version == 2) {
            baseImgFile = new File("/home/share/1_3/xiaoyaojing_dai.jpg"); // 原始图片文件
        } else {
            baseImgFile = new File("/home/share/1_3/365_dai.jpg"); // 原始图片文件
        }*/
        BufferedImage baseImg = null;
        try {
            baseImg = ImageIO.read(baseImgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage qrCard = getQRCard(qrUrl, 359);
        String agentId = agent.getId().toString();
        Graphics2D g = baseImg.createGraphics();
        //字体抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = (baseImg.getWidth() - qrCard.getWidth()) / 2;
        int y = 647;
        //贴二维码
        g.drawImage(qrCard, x, y, qrCard.getWidth(), qrCard.getHeight(), null);
        //贴名字
        int orgStringWight = g.getFontMetrics().stringWidth(agentId);
        int orgStringLength = agentId.length();
        Font font = getDefinedFont(2, 64);
        g.setColor(new Color(0, 0, 0)); //根据图片的背景设置水印颜色
        g.setFont(font);
        String tempStr;
        orgStringWight = g.getFontMetrics().stringWidth(agentId);
        orgStringLength = agentId.length();
        x = (int) (baseImg.getWidth() - orgStringWight * 0.9) / 2;
        y = 1490;
        while (agentId.length() > 0) {
            tempStr = agentId.substring(0, 1);
            agentId = agentId.substring(1, agentId.length());
            g.drawString(tempStr, x, y);
            x = (int) (x + (double) orgStringWight / (double) orgStringLength * 0.9);
        }
        g.dispose();


        //上传云
        PropFileManager propFileMgr = new PropFileManager("interface.properties");
        String ossBucketName = propFileMgr.getProperty("aliyun.ossBucketName");
        String fileKey = StringUtils.getRandomUUID();
        String NewFileKey = fileKey + ".jpg";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(baseImg, "123.jpg".split("\\.")[1], os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        AliyunServiceImpl.getInstance().putFileStreamToOSS(ossBucketName, NewFileKey, is);
        String newFileUrl = AliyunServiceImpl.getInstance().generatePresignedUrl(ossBucketName, NewFileKey, 10 * 24 * 365
        ).toString();
        return newFileUrl;
    }
}