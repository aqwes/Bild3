package DHDMTG_Utils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import static DHDMTG_Utils.DecompressDHD.ourMagic;

/**
 * Created by Dennis, Henrik and Daniel on 2016-11-03v: 44.
 * This class reads a mtg file and reduce the colors.
 */
public class CompressDHD {
    static int[] allColors;

    /**
     * Reads a image, start the compression and then writes out the new file.
     *
     * @param img
     * @throws IOException
     */
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

    /**
     * Go though each pixel and search for the in our colorpalette.
     * When all done we present the image so the user can se the result.
     *
     * @param img
     * @return
     */
    public static BufferedImage run(BufferedImage img) {
        allColors = Colors.createpalette();
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                bufferedImage.setRGB(i, j, getClosestColor(img.getRGB(i, j)));
            }
        }
        showImage(bufferedImage);
        return bufferedImage;
    }

    /**
     * Shows the image in a frame
     *
     * @param img
     */
    public static void showImage(BufferedImage img) {
        ImageIcon icon = new ImageIcon(img);
        JPanel jPanel = new JPanel();
        jPanel.add(new JLabel(icon));
        JFrame frame = new JFrame();
        frame.add(jPanel);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * This method return the closes color in  the palette.
     *
     * @param rgb
     * @return
     */
    public static int getClosestColor(int rgb) {
        double min = Double.MAX_VALUE;
        int saveIndex = 0;
        for (int index = 0; index < allColors.length; index++) {
            double distance = distanceInLAB(allColors[index], rgb);
            if (distance <= min) {
                min = distance;
                saveIndex = index;
            }
        }
        return allColors[saveIndex];
    }

    /**
     * This formula is done so we can get the distance between the colors. Its easier to check with this method instead of comparing rgb values directly.
     *
     * @param c
     * @return
     */
    // Converts RGB to XYZ. Link http://www.easyrgb.com/index.php?X=MATH&H=02#text2
    private static double[] RGBToXYZ(int c) {
        double r = ((c >> 16) & 0xFF) / (double) 255;
        double g = ((c >> 8) & 0xFF) / (double) 255;
        double b = (c & 0xFF) / (double) 255;

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

    /**
     * Returns the distance between two colors
     *
     * @param c1
     * @param c2
     * @return
     */
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