package moarTest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by Daniel Hertzman-Ericson on 2016-01-25.
 */
public class ImageUtilities {

    public BufferedImage apply(BufferedImage image) {

        BufferedImage dest = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);

        Graphics g = dest.getGraphics();
        g.setColor(new Color(231,20,189));
        g.fillRect(0,0,dest.getWidth(),dest.getHeight());

        ColorModel cm = dest.getColorModel();

        IndexColorModel icm = (IndexColorModel) cm;
        WritableRaster raster = dest.getRaster();

        int pixel = raster.getSample(0,0,0);
        int size = icm.getMapSize();

        byte[] reds = new byte[size];
        byte[] greens = new byte[size];
        byte[] blues = new byte[size];

        icm.getReds(reds);
        icm.getGreens(greens);
        icm.getBlues(blues);

        IndexColorModel icm2 = new IndexColorModel(8,size,reds,greens,blues,pixel);
        dest = new BufferedImage(icm2, raster, dest.isAlphaPremultiplied(), null);

        dest.createGraphics().drawImage(image,0,0,null);
        return dest;
    }
}
