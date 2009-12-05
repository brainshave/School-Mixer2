/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mixer2.gui;

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
        image = new BufferedImage(2048, 2048, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    public BufferedImage getImage() {
        return image;
    }
}
