package logic;

import data.Airplane;
import data.Flight;

import javax.swing.*;
import java.awt.*;

public class Simulation {
    private int time;
    private Timer clock;

    public Simulation(Runnable runnable,Runnable finished) {
        clock=new Timer(200, e -> {
            if(!Flight.getFlights().isEmpty() && Airplane.allFinished()){
                clock.stop();
                finished.run();
                return;
            }
            time+=2;   // 200 ms -> 2 simulated minutes (1 s = 10 min)
            Airplane.move(time);
            runnable.run();
        });
    }

    public void startTimer(){
        clock.start();
    }
    public void stopTimer(){
        Airplane.resetAll();
        clock.stop();
        time=0;
    }

    public void pauseTimer(){
        clock.stop();
    }

    public int getTime() {
        return time;
    }
}
