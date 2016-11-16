package DHDMTG_Utils;

import MTGPNG_Utils.DecompressMTG;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by Dennis, Henrik and Daniel on 2016-11-04v: 44.
 * Decompress our format and recreate it by using the colors in our palette.
 */
public class DecompressDHD {
    public final static byte[] ourMagic = "dhd!".getBytes(StandardCharsets.US_ASCII);
    static int[] allColors;

    public static BufferedImage read(String fnam) throws IOException {
        InputStream in = new FileInputStream(fnam);
        allColors = Colors.createpalette();
        for (int i = 0; i < ourMagic.length; i++) {
            try {
                if (in.read() != ourMagic[i]) {
                    throw new DecompressMTG.InvalidMegatronFileException();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int width = DecompressMTG.read4bytes(in);
        int height = DecompressMTG.read4bytes(in);

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
        return CompressDHD.run(img);
    }
}