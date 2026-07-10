package gui;

import data.Airport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapPanel extends JPanel {

    private static final int SIZE = 20;        // veličina kvadrata
    private static final int FONT_SIZE = 20;   // veličina koda

    private Airport selected = null;   // trenutno selektovani aerodrom (ili null)
    private boolean blinkOn = false;   // stanje treperenja (menja ga tajmer)

    private InactivityTimer timer;


    public MapPanel() {
        // MouseListener se dodaje JEDNOM, u konstruktoru (NE u paintComponent!)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Airport clicked = null;
                boolean showEverything = Airport.showAll();
                for (Airport a : Airport.getAirports()) {
                    if (getRect(a).contains(e.getX(), e.getY()) && (a.isVisible() || showEverything)) {
                        clicked = a;
                        break;
                    }
                }
                if (clicked != null) {
                    // ponovni klik na isti -> poništi selekciju; inače selektuj
                    if(selected == clicked) {
                        selected = null;
                        timer.cont();
                    }
                    else{
                        selected = clicked;
                        timer.pause();
                    }
                    repaint();   // NE crtamo ovde - samo tražimo ponovno iscrtavanje
                }
            }
        });

        // Swing Timer: svakih 500ms okrene blinkOn i prekrca panel -> treperenje
        new Timer(500, e -> {
            blinkOn = !blinkOn;
            repaint();
        }).start();
    }

    public void setTimer(InactivityTimer t) {
        timer=t;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        java.util.List<Airport> airports = Airport.getAirports();

        boolean showEverything = Airport.showAll();

        // 1. PROLAZ: kvadrati (selektovani treperi crveno, ostali sivi)
        for (Airport a : airports) {
            if(!showEverything && !a.isVisible()) continue;
            Rectangle r = getRect(a);
            if (a == selected && blinkOn) g.setColor(Color.RED);
            else                          g.setColor(Color.GRAY);
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        // 2. PROLAZ: kodovi preko svih kvadrata
        g.setColor(Color.BLACK);
        g.setFont(new Font("Verdana", Font.BOLD, FONT_SIZE));
        for (Airport a : airports) {
            if(!showEverything && !a.isVisible()) continue;
            Rectangle r = getRect(a);
            g.drawString(a.getCode(), r.x + r.width + 4, r.y + r.height / 2 + FONT_SIZE / 2-2);
        }
    }

    // pravougaonik kvadrata za dati aerodrom (računa W/H iz trenutne veličine panela)
    private Rectangle getRect(Airport a) {
        int W = getWidth() / 2;
        int H = getHeight() / 2;
        int px = (int) ((1 + a.getX() / 180.0) * W);
        int py = (int) ((1 - a.getY() / 90.0) * H);
        return new Rectangle(px - SIZE / 2, py - SIZE / 2, SIZE, SIZE);
    }
}
