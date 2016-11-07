package DHDMTG_Utils;

import MTGPNG_Utils.DecompressMTG;

import java.awt.image.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by Dennis, Henrik on 2016-11-03v: 44.
 */
    public class CompressDHD {
    final static byte[] magic = "mEgaMADNZ!".getBytes(StandardCharsets.US_ASCII);
    final static byte[] ourMagic = "HKDK!".getBytes(StandardCharsets.US_ASCII);


    public CompressDHD(String fnam) throws IOException {
            InputStream in = null;
            try {
                in = new FileInputStream(fnam);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < magic.length; i++) {
                try {if (in.read() != magic[i]) { throw new DecompressMTG.InvalidMegatronFileException();}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        int width  = read4bytes(in);
        int height = read4bytes(in);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        byte[] pxlBytes = new byte[3];
        int[] pxl = new int[3];
        WritableRaster imgr  = img.getRaster();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                try {
                    if (in.read(pxlBytes) != 3) { throw new EOFException(); }
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
        compress(img,fnam);
        }

    private void compress(BufferedImage img, String fnam) throws IOException {
        int H = img.getHeight();
        int W = img.getWidth();

        OutputStream out = new FileOutputStream(new File("src/resources/img.dhd"));
        out.write(ourMagic);

        write4bytes(W, out);
        write4bytes(H, out);

        byte[] bytes = CompressAlgorithm.run(img);

        System.out.println(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            out.write(bytes[i]);
        }
        out.close();
    }

    private static int read4bytes(InputStream in) {
        int b, v = 0;
        try {
            b = in.read();
            if (b < 0) { throw new EOFException(); }
            v = b<<3*8;
            b = in.read(); if (b < 0) { throw new EOFException(); }
            v |= b<<2*8;
            b = in.read(); if (b < 0) { throw new EOFException(); }
            v |= b<<1*8;
            b = in.read(); if (b < 0) { throw new EOFException(); }
            v |= b;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;
    }


    /** Writes an int as 4 bytes, big endian. */
    private static void write4bytes(int v, OutputStream out) throws IOException {
        out.write(v>>>3*8);
        out.write(v>>>2*8 & 255);
        out.write(v>>>1*8 & 255);
        out.write(v       & 255);
    }


    }

