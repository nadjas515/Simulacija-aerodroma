package gui;

import data.Airport;
import data.Data;
import data.Flight;
import util.MapProjection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapPanel extends JPanel {

    private static final int SIZE = 20;        // size of an airport square
    private static final int FONT_SIZE = 20;   // font size of the airport code

    private Airport selected = null;   // currently selected airport (or null)
    private boolean blinkOn = false;   // blink state (toggled by the timer)

    private InactivityTimer timer;


    public MapPanel() {
        // the MouseListener is added ONCE, in the constructor (NOT in paintComponent!)
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
                    // clicking the selected airport again deselects it; otherwise select it
                    if(selected == clicked) {
                        selected = null;
                        timer.cont();
                    }
                    else{
                        selected = clicked;
                        timer.pause();
                    }
                    repaint();   // don't paint here, just request a repaint
                }
            }
        });

        // Swing Timer: toggles blinkOn every 500 ms and repaints the panel -> blinking
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

        g.setColor(Color.black);
        g.drawLine(0,getHeight()/2, getWidth(), getHeight()/2);

        g.drawLine(getWidth()/2,0, getWidth()/2, getHeight());

        java.util.List<Airport> airports = Airport.getAirports();

        boolean showEverything = Airport.showAll();

        // PASS 1: squares (the selected one blinks red, the rest are grey)
        for (Airport a : airports) {
            if(!showEverything && !a.isVisible()) continue;
            Rectangle r = getRect(a);
            if (a == selected && blinkOn) g.setColor(Color.RED);
            else                          g.setColor(Color.GRAY);
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        java.util.List<Flight> flights = Flight.getFlights();

        for(Flight f : flights){
            if(((!f.getStart().isVisible() || !f.getEnd().isVisible()) && !showEverything) || !f.getPlane().getFlying()) continue;
            Rectangle r = getRect(f.getPlane());
            g.setColor(Color.BLUE);
            g.fillOval(r.x, r.y, r.width, r.height);
        }

        // PASS 2: codes drawn on top of all squares
        g.setColor(Color.BLACK);
        g.setFont(new Font("Verdana", Font.BOLD, FONT_SIZE));
        for (Airport a : airports) {
            if(!showEverything && !a.isVisible()) continue;
            Rectangle r = getRect(a);
            g.drawString(a.getCode(), r.x + r.width + 4, r.y + r.height / 2 + FONT_SIZE / 2-2);
        }
    }

    // square for the given airport (W/H computed from the current panel size)
    private Rectangle getRect(Data a) {
        int px = MapProjection.toPixelX(a.getX(), getWidth());
        int py = MapProjection.toPixelY(a.getY(), getHeight());
        return new Rectangle(px - SIZE / 2, py - SIZE / 2, SIZE, SIZE);
    }

}
