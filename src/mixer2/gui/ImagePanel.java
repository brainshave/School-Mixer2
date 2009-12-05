/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mixer2.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author szymon
 */
public class ImagePanel extends JPanel{
    private BufferedImage image;

    public ImagePanel() {
        //image = new BufferedImage(2048, 2048, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(image != null) g.drawImage(image, 0, 0, null);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void resizeImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        System.gc();
        this.setPreferredSize(new Dimension(width, height));
        this.revalidate();
    }
}
