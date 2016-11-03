package someTests;

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
        System.out.println("1- Komprimera Bild\n");
        System.out.println("2- Expandera bild\n");
        System.out.println("Vänligen välj ett av ovanstående alternativ genom att mata in siffran '1' || '2: ");
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new Compression(img);
                break;
            case 2:
                scanner = new Scanner(System.in);
                choice = scanner.nextInt();
                break;
        }

    }
    public static void main(String [] args){
        new Start();
    }
}
