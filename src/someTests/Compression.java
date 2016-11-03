package someTests;

import javax.swing.*;
import java.awt.image.*;
import java.io.*;

/**
 * Created by Daniel Hertzman-Ericson on 2016-01-17.
 */
public class Compression {

    private WritableRaster raster;
    private RGB[][] colorArray;

    public Compression(BufferedImage image) {
        raster = image.getRaster();
        colorArray = new RGB[raster.getHeight()][raster.getWidth()];
        initColorArray();
    }

    private void initColorArray() {

        for (int row=0;row<colorArray.length;row++){
            for (int col=0;col<colorArray[row].length;col++){
                colorArray[row][col]=new RGB(); //init
                colorArray[row][col].setRed(raster.getSample(col, row, 0)); //Red
                colorArray[row][col].setGreen(raster.getSample(col,row,1)); //Green
                colorArray[row][col].setBlue(raster.getSample(col,row,2));  //Blue
            }
        }
        saveCompressedFile();
    }

    private void saveCompressedFile(){
        String filename= JOptionPane.showInputDialog(null,"Skriv in önskat filnamn.");
        try {
            OutputStream bos = new BufferedOutputStream(new FileOutputStream(filename+".png"));

            bos.write(colorArray[0].length);    //Write Width as int    32bits.
            bos.write(colorArray.length);       //Write Height as int   32bits.

            for (int row=0;row<colorArray.length;row++){
                for (int col=0;col<colorArray[row].length;col++){
                    byte[] pixelvalues=new byte[3];
                    //Get red value as Byte/8bits
                    Integer value=colorArray[row][col].getRed();
                    byte temp=value.byteValue();
                    pixelvalues[0]=temp;
                    //Get Green value as Byte/8bits
                    value=colorArray[row][col].getGreen();
                    temp=value.byteValue();
                    pixelvalues[1]=temp;
                    //Get Blue value as Byte/8bits
                    value=colorArray[row][col].getBlue();
                    temp=value.byteValue();
                    pixelvalues[1]=temp;
                    //     System.out.println("Red:"+pixelvalues[0]+" Green:"+pixelvalues[1]+" Blue:"+pixelvalues[2]);
                    bos.write(temp);

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
