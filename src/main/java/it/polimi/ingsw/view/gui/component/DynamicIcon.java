package it.polimi.ingsw.view.gui.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.util.Objects;
import javax.swing.ImageIcon;

/**
 * An ImageIcon that adapts to the size of its container
 */
public class DynamicIcon extends ImageIcon {
    /**
     * @see ImageIcon#ImageIcon(Image)  
     */
    public DynamicIcon(Image image) {
        super(image);
    }

    /**
     * getIconWidth()
     * @return Always zero (the image adapts to the size of its container)
     */
    @Override
    public int getIconWidth() {
        return 0;
    }

    /**
     * getIconHeight()
     * @return Always zero (the image adapts to the size of its container)
     */
    @Override
    public int getIconHeight() {
        return 0;
    }

    /**
     * @see ImageIcon#paintIcon(Component, Graphics, int, int) 
     */
    @Override
    public synchronized void paintIcon(Component c, Graphics g, int _x, int _y) {
        Image image = getImage();
        if (image == null) {
            return;
        }

        Insets insets = ((Container) c).getInsets();
        int x = insets.left;
        int y = insets.top;

        int w = c.getWidth() - x - insets.right;
        int h = c.getHeight() - y - insets.bottom;

        g.drawImage(image, x, y, w, h, Objects.requireNonNullElse(getImageObserver(), c));
    }
}
