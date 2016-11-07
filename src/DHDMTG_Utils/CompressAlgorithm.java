package DHDMTG_Utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;




public class CompressAlgorithm {


    public static byte[] run(BufferedImage image) {
        byte[] dataToCompress = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();


        return dataToCompress;
    }
}