package main;

import DHDMTG_Utils.CompressDHD;
import DHDMTG_Utils.DecompressDHD;
import MTGPNG_Utils.CompressMTG;
import MTGPNG_Utils.DecompressMTG;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Daniel Hertzman-Ericson on 2016-01-17.
 */
public class Start {

    public Start() {
        BufferedImage img = null;
        System.out.println("1- Komprimera bild från png till mtg.\n");
        System.out.println("2- Expandera bild från mtg png.\n");
        System.out.println("3- Komprimera bild från mtg till dhd.\n");
        System.out.println("4- Expandera bild från dhd till mtg.\n");
        System.out.println("Vänligen välj ett av ovanstående alternativ genom att mata in siffran '1' || '2 || 3 || 4: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        File folder = new File("src/resources");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            System.err.print("Glöm inte att göra resources till en resources mapp!\n");
        }
        for (int i = 0; i < (listOfFiles != null ? listOfFiles.length : 0); i++) {
            System.out.println("Fil " + i + ": " + listOfFiles[i].getName());
        }
        System.out.println("Skriv in ett filnummer: \n");

        switch (choice) {
            case 1:
                scanner = new Scanner(System.in);
                choice = scanner.nextInt();
                try {
                   img = ImageIO.read(new File(listOfFiles != null ? listOfFiles[choice].getPath() : null));
                    new CompressMTG(img, folder + "/" +listOfFiles[choice].getName().replaceAll(".png",".mtg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case 2:
                scanner = new Scanner(System.in);
                choice = scanner.nextInt();
                img = DecompressMTG.read(listOfFiles[choice].getPath());
                try {
                    ImageIO.write(img, "PNG", new File(folder + "/" +listOfFiles[choice].getName().replaceAll(".mtg",".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case 3:
                scanner = new Scanner(System.in);
                choice = scanner.nextInt();
                try {
                    img = ImageIO.read(new File(listOfFiles != null ? listOfFiles[choice].getPath() : null));
                    new CompressDHD(img, folder + "/" +listOfFiles[choice].getName().replaceAll(".png",".mtg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case 4:
                scanner = new Scanner(System.in);
                choice = scanner.nextInt();
                img = DecompressDHD.read(listOfFiles[choice].getPath());
                try {
                    ImageIO.write(img, "PNG", new File(folder + "/" +listOfFiles[choice].getName().replaceAll(".mtg",".png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
        }

    }
    public static void main(String [] args){
        new Start();
    }
}
