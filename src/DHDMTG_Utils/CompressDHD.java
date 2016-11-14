package DHDMTG_Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import static DHDMTG_Utils.DecompressDHD.ourMagic;

/**
 * Created by Dennis, Henrik on 2016-11-03v: 44.
 */
public class CompressDHD {
    static int [] allColors;

    public CompressDHD(BufferedImage img) throws IOException {
        Random rand = new Random();
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pxl = new int[3];
        Raster imgr = run(img).getRaster();
        OutputStream out = new FileOutputStream("src/resources/img" + rand.nextInt(100) + ".dhd");
        out.write(ourMagic);
        write4bytes(width, out);
        write4bytes(height, out);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                imgr.getPixel(i, j, pxl);
                out.write(pxl[0]);
                out.write(pxl[1]);
                out.write(pxl[2]);
            }
        }
        out.close();
    }
    /**
     * Writes an int as 4 bytes, big endian.
     */
    private static void write4bytes(int v, OutputStream out) throws IOException {
        out.write(v >>> 3 * 8);
        out.write(v >>> 2 * 8 & 255);
        out.write(v >>> 1 * 8 & 255);
        out.write(v & 255);
    }

    public static BufferedImage run(BufferedImage img) {
        int[][] imgArray = new int[img.getWidth()][img.getHeight()];
        allColors= createpalette();

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                imgArray[i][j] = img.getRGB(i, j);
            }
        }
        return findEqualColors(imgArray);
    }


    public static int[] createpalette() {
        int r=0,g=0,b=0;
        int count = 0;
        int countColors = 0;
        int[] colors = new int[Colors.palette.length/3];

        for (int i = 0; i < Colors.palette.length; i++) {
            if(count==0){
                r = Colors.palette[i];
                count++;
            }else if (count==1){
                g = Colors.palette[i];
                count++;
            }else if (count==2){
                b = Colors.palette[i];
                Color color = new Color(r,g,b);
                colors[countColors] = color.getRGB();
                countColors++;
                count = 0;
            }
        }
        return colors;
    }


    private static BufferedImage findEqualColors(int[][] imgArray) {
        for (int i = 0; i < imgArray.length; i++) {
            for (int j = 0; j < imgArray[0].length; j++) {
                imgArray[i][j] = getClosestColor(imgArray[i][j]);
            }
        }
        return createBufferedImage(imgArray);
    }

    public static BufferedImage createBufferedImage(int[][] imgArray) {
        BufferedImage bufferedImage = new BufferedImage(imgArray.length, imgArray[0].length, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < imgArray.length; i++) {
            for (int j = 0; j < imgArray[0].length; j++) {
                int pixel = Integer.parseInt(String.valueOf(imgArray[i][j]));
                bufferedImage.setRGB(i, j, pixel);
            }
        }
        ImageIcon icon = new ImageIcon(bufferedImage);
        JPanel jPanel = new JPanel();
        jPanel.add(new JLabel(icon));
        JFrame frame = new JFrame();
        frame.add(jPanel);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return bufferedImage;
    }

    public static int getClosestColor(int rgb) {
        double min = Double.MAX_VALUE;
        int saveIndex=0;
        for (int index = 0; index < allColors.length; index++) {
            double distance = distanceInLAB(allColors[index], rgb);
         if(distance <= min){
             min = distance;
             saveIndex = index;
         }
        }
        return  allColors[saveIndex];
    }

    // Converts RGB to XYZ. Link http://www.easyrgb.com/index.php?X=MATH&H=02#text2
    private static double[] RGBToXYZ(int c) {
        double r = ((c >> 16) & 0xFF) / (double)255;
        double g = ((c >> 8) & 0xFF) / (double)255;
        double b = (c & 0xFF) / (double)255;

        r = r > 0.04045 ? Math.pow(((r + 0.055) / 1.055), 2.4) : r / 12.92;
        g = g > 0.04045 ? Math.pow(((g + 0.055) / 1.055), 2.4) : g / 12.92;
        b = b > 0.04045 ? Math.pow(((b + 0.055) / 1.055), 2.4) : b / 12.92;

        r *= 100;
        g *= 100;
        b *= 100;

        double[] xyz = new double[3];
        xyz[0] = r * 0.4124 + g * 0.3576 + b * 0.1805;
        xyz[1] = r * 0.2126 + g * 0.7152 + b * 0.0722;
        xyz[2] = r * 0.0193 + g * 0.1192 + b * 0.9505;

        return xyz;
    }

    public static double distanceInLAB(int c1, int c2) {
        double[] lab1 = XYZtoLAB(RGBToXYZ(c1));
        double[] lab2 = XYZtoLAB(RGBToXYZ(c2));
        double distance = Math.sqrt(Math.pow(lab1[0] - lab2[0], 2) + Math.pow(lab1[1] - lab2[1], 2) + Math.pow(lab1[2] - lab2[2], 2));

        return distance;
    }

    /**
     * Converts XYZ color to CIE-L*ab. http://www.easyrgb.com/index.php?X=MATH&H=07#text7
     */
    private static double[] XYZtoLAB(double[] xyz) {
        double x = xyz[0] / 95.047;    //ref_X =     Observer= 2°, Illuminant= D65
        double y = xyz[1] / 100.000;   //ref_Y =
        double z = xyz[2] / 108.883;   //ref_Z =
        x = x > 0.00856 ? Math.pow(x, (double) 1 / 3) : (7.787 * x) + ((double) 16 / 116);
        y = y > 0.00856 ? Math.pow(y, (double) 1 / 3) : (7.787 * y) + ((double) 16 / 116);
        z = z > 0.00856 ? Math.pow(z, (double) 1 / 3) : (7.787 * z) + ((double) 16 / 116);

        double[] lab = new double[3];
        lab[0] = (116 * y) - 16;
        lab[1] = 500 * (x - y);
        lab[2] = 200 * (y - z);
        return lab;
    }
}