/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MixerFrame.java
 *
 * Created on 2009-12-04, 16:39:17
 */
package mixer2.gui;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import mixer2.Tools;
import mixer2.Tools.Tool;

/**
 *
 * @author szymon
 */
public class MixerFrame extends javax.swing.JFrame {

    private Tools tools;
    private Tools.Tool tool;
    private ImagePanel imagePanel;
    private BufferedImage image1, image2;
    private int[] im1buff, im2buff, outBuff;
    private int width, height;

    /** Creates new form MixerFrame */
    public MixerFrame() {
        tools = new Tools();
        tool = null;
        image1 = null;
        image2 = null;
        imagePanel = new ImagePanel();
        initComponents();
        jScrollPane1.setViewportView(imagePanel);

        // Dodawanie narzedzi:
        for (final Entry<String, Tool> entry : tools.getAll()) {
            JRadioButton button = new JRadioButton(entry.getKey());
            button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    tool = entry.getValue();
                    mixImages(true);
                }
            });
            toolsGroup.add(button);
            toolsPanel.add(button);
        }
    }

    public Entry<String, BufferedImage> getImage() {
        try {
            FileDialog dialog = new FileDialog(this, "Wybierz plik", FileDialog.LOAD);
            dialog.setModal(true);
            dialog.setVisible(true);
            //if(dialog.)
            String path = dialog.getDirectory() + dialog.getFile();
            System.out.println("Path: " + path);
            File f = new File(path);
            return new SimpleEntry(path, ImageIO.read(f));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Nie moge odczytac pliku: " + ex, "Blad", JOptionPane.ERROR_MESSAGE);
        }
        return new SimpleEntry(null, null);
    }

    public void reBuffImages() {
        if(image1 == null || image2 == null) return;
        width = Math.min(image1.getWidth(), image2.getWidth());
        height = Math.min(image1.getHeight(), image2.getHeight());
        int size = width * height;
        if(im1buff == null || im1buff.length < size)
            im1buff = new int [size];
        if(im2buff == null || im2buff.length < size)
            im2buff = new int [size];
        if(outBuff == null || outBuff.length < size)
            outBuff = new int [size];
        imagePanel.resizeImage(width, height);
        Tools.genPixelArrays(image1, im1buff, image2, im2buff, width, height);
    }

    public void mixImages(boolean warnings) {
        if (tool == null) {
            return;
        } else if (im1buff == null) {
            if (warnings) {
                JOptionPane.showMessageDialog(this, "Nie wybrano obrazka 1", "Powiadomienie", JOptionPane.WARNING_MESSAGE);
            }
            return;
        } else if (im2buff == null) {
            if (warnings) {
                JOptionPane.showMessageDialog(this, "Nie wybrano obrazka 2", "Powiadomienie", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }
        
        //Dimension dim = new Dimension(Math.min(image1.getWidth(), image2.getWidth()), Math.min(image1.getHeight(), image2.getHeight()));
        tool.mix(im1buff, im2buff, outBuff, width * height);
        WritableRaster outRaster = imagePanel.getImage().getRaster();

        outRaster.setDataElements(0, 0, width, height, outBuff);
        //jScrollPane1.getViewport().setBounds(0, 0, Math.min(image1.getWidth(), image2.getWidth()), Math.min(image1.getHeight(), image2.getHeight()));
        //jScrollPane1.re

        //imagePanel.setMaximumSize(dim);
        //imagePanel.setMinimumSize(dim);
        //imagePanel.setSize(width, height);
        imagePanel.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolsGroup = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        toolsPanel = new javax.swing.JPanel();
        image1Button = new javax.swing.JButton();
        image2Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mixer");

        jScrollPane1.setMinimumSize(new java.awt.Dimension(100, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(500, 500));
        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        toolsPanel.setLayout(new javax.swing.BoxLayout(toolsPanel, javax.swing.BoxLayout.PAGE_AXIS));

        image1Button.setText("Obrazek 1");
        image1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                image1ButtonActionPerformed(evt);
            }
        });
        toolsPanel.add(image1Button);

        image2Button.setText("Obrazek 2");
        image2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                image2ButtonActionPerformed(evt);
            }
        });
        toolsPanel.add(image2Button);

        getContentPane().add(toolsPanel, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void image1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_image1ButtonActionPerformed
        // TODO add your handling code here:
        Entry<String, BufferedImage> entry = getImage();
        if (entry.getValue() != null && entry.getKey() != null) {
            image1 = entry.getValue();
            image1Button.setText(entry.getKey());
            reBuffImages();
            mixImages(false);
        }
    }//GEN-LAST:event_image1ButtonActionPerformed

    private void image2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_image2ButtonActionPerformed
        Entry<String, BufferedImage> entry = getImage();
        if (entry.getValue() != null && entry.getKey() != null) {
            image2 = entry.getValue();
            image2Button.setText(entry.getKey());
            reBuffImages();
            mixImages(false);
        }
    }//GEN-LAST:event_image2ButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MixerFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton image1Button;
    private javax.swing.JButton image2Button;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.ButtonGroup toolsGroup;
    private javax.swing.JPanel toolsPanel;
    // End of variables declaration//GEN-END:variables
}
