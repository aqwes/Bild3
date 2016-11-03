package MTGPNG_Utils;

import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by Daniel Hertzman-Ericson on 2016-01-17.
 */
public class CompressMTG {
    final static byte[] magic = "HKDK!".getBytes(StandardCharsets.US_ASCII);

    public CompressMTG(BufferedImage img, String fnam) throws IOException {
        int width  = img.getWidth();
        int height = img.getHeight();
        int[] pxl = new int[3];
        Raster imgr  = img.getRaster();
        OutputStream out = new FileOutputStream(fnam);
        out.write(magic);
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

    /** Writes an int as 4 bytes, big endian. */
    private static void write4bytes(int v, OutputStream out) throws IOException {
        out.write(v>>>3*8);
        out.write(v>>>2*8 & 255);
        out.write(v>>>1*8 & 255);
        out.write(v       & 255);
    }

}
