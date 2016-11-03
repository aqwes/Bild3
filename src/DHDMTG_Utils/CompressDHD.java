package DHDMTG_Utils;

import MTGPNG_Utils.DecompressMTG;


import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;


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
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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
        createFile(img,fnam);
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

    private void createFile(BufferedImage img, String fnam) throws IOException {
        int W = img.getWidth();
        int H = img.getHeight();

//        OutputStream out = new FileOutputStream(fnam);
//
//        // Write the watermark to the file
//        out.write(ourMagic);
//
//        // Write the width and height to the file
//        write4bytes(W, out);
//        write4bytes(H, out);

        compress(img);
//        // Run the compression
//        byte[] bytes =
//
//        for (int i = 0; i < bytes.length; i++) {
//            out.write(bytes[i]);
//        }
//
//        out.close();

    }

    /** Writes an int as 4 bytes, big endian. */
    private static void write4bytes(int v, OutputStream out) throws IOException {
        out.write(v>>>3*8);
        out.write(v>>>2*8 & 255);
        out.write(v>>>1*8 & 255);
        out.write(v       & 255);
    }

    private byte[] compress(BufferedImage image) throws IOException {

        File compressedImageFile = new File("compress.jpg");
        OutputStream os =new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.5f);
        writer.write(null, new IIOImage(image, null, null), param);

        os.close();
        ios.close();
        writer.dispose();
        return null;

    }


    }

