package DHDMTG_Utils;

import MTGPNG_Utils.DecompressMTG;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DecompressDHD {
    public final static byte[] ourMagic = "dhd!".getBytes(StandardCharsets.US_ASCII);
    static int[] allColors;

    public static BufferedImage read(String fnam) throws IOException {
        InputStream in = new FileInputStream(fnam);
        allColors = CompressDHD.createpalette();
        for (int i = 0; i < ourMagic.length; i++) {
            try {
                if (in.read() != ourMagic[i]) {
                    throw new DecompressMTG.InvalidMegatronFileException();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int width = read4bytes(in);
        int height = read4bytes(in);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        byte[] pxlBytes = new byte[3];
        int[] pxl = new int[3];

        WritableRaster imgr = img.getRaster();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                try {
                    if (in.read(pxlBytes) != 3) {
                        throw new EOFException();
                    }
                    pxl[0] = pxlBytes[0];
                    pxl[1] = pxlBytes[1];
                    pxl[2] = pxlBytes[2];
                    imgr.setPixel(i, j, pxl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int[][] imgArray = new int[img.getWidth()][img.getHeight()];

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                imgArray[i][j] = img.getRGB(i, j);
            }
        }
        return findEqualColors(imgArray);
    }

    public static int getClosestColor(int rgb) {
        double min = Double.MAX_VALUE;
        int saveIndex = 0;
        for (int index = 0; index < allColors.length; index++) {
            double distance = CompressDHD.distanceInLAB(allColors[index], rgb);
            if (distance <= min) {
                min = distance;
                saveIndex = index;
            }
        }
        return allColors[saveIndex];
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
        return bufferedImage;
    }

    private static int read4bytes(InputStream in) {
        int b, v = 0;
        try {
            b = in.read();
            if (b < 0) {
                throw new EOFException();
            }
            v = b << 3 * 8;
            b = in.read();
            if (b < 0) {
                throw new EOFException();
            }
            v |= b << 2 * 8;
            b = in.read();
            if (b < 0) {
                throw new EOFException();
            }
            v |= b << 1 * 8;
            b = in.read();
            if (b < 0) {
                throw new EOFException();
            }
            v |= b;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;
    }
}