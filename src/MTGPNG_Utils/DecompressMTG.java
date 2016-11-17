package MTGPNG_Utils;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static MTGPNG_Utils.CompressMTG.magic;

/**
 * Created by Dennis, Daniel and Henrik on 2016-11-03v: 44.
 */
public class DecompressMTG {
    public final static class InvalidMegatronFileException extends IOException {
    }
    public static BufferedImage read(String fnam) throws IOException {
        InputStream in = new FileInputStream(fnam);

        for (int i = 0; i < magic.length; i++) {
            try {
                if (in.read() != magic[i]) {
                    throw new InvalidMegatronFileException();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int width = read4bytes(in);
        int height = read4bytes(in);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
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

        return img;
    }

    public static int read4bytes(InputStream in) {
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