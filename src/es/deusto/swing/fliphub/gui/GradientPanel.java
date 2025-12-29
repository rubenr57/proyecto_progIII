package es.deusto.swing.fliphub.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Color;

import javax.swing.JPanel;

//Pinta un degradado
//Uso de IA generativa
public class GradientPanel extends JPanel {

    private final Color top;
    private final Color bottom;

    public GradientPanel(Color top, Color bottom) {
        this.top = top;
        this.bottom = bottom;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bottom);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        g2.dispose();
    }
}
