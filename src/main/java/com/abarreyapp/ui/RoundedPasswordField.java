package com.abarreyapp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPasswordField extends JPasswordField {
    private Shape shape;
    public RoundedPasswordField(int size) {
        super(size);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setFont(new Font("Arial", Font.PLAIN, 14));
    }
    protected void paintComponent(Graphics g) {
         g.setColor(getBackground());
         g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
         super.paintComponent(g);
    }
    protected void paintBorder(Graphics g) {
         Graphics2D g2 = (Graphics2D) g;
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setColor(new Color(200, 200, 200));
         g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
    }
    public boolean contains(int x, int y) {
         if (shape == null || !shape.getBounds().equals(getBounds())) {
             shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 20, 20);
         }
         return shape.contains(x, y);
    }
}
