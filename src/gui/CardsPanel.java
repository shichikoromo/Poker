package gui;

import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

public class CardsPanel extends Panel {

    //"/Users/N/Java/Poker/src/gui/img/Kartenbilder/clubs/2.png"

    public void paint(Graphics g) {
        BufferedImage myImage = null;
        try{ myImage = ImageIO.read(new File("picture.gif")); }
        catch (IOException e) { }
        g.drawImage(myImage, 100, 100, this);
    }
}
