package triple;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import static triple.MainPanel.multipleSecs;
import static triple.MainPanel.timeSlowSecs;


class StatePanel extends JPanel {
    int frameRate;
    int timeVal;
    int timeSlowLastTime;
    int multipleLastTime;
    double timeSlowScale;
    double multipleScale;
    int transLastTimes;
    StatePanel ptr=this;
    Timer drawer;

    StatePanel() {
        frameRate = 40;
        timeVal = 1000 / frameRate;
        drawer = new Timer();
        drawer.schedule(new TimerTask() {
            int count = 0;

            @Override
            public void run() {
                if(MainPanel.pauseMark.get()==0)
                    ptr.repaint();
            }
        }, 100, 25);
    }

    void Initialize() {
        timeSlowLastTime = 0;
        multipleLastTime = 0;
        timeSlowScale = 1.;
        multipleScale = 1.;
        transLastTimes = 0;
    }

    void UpdateTimeSlow(double scale) {
        timeSlowScale = scale;
        timeSlowLastTime = timeSlowSecs * 1000;
    }

    void UpdateMultiple(double scale) {
        multipleScale = scale;
        multipleLastTime = multipleSecs * 1000;
    }

    void UpdateTrans(int times) {
        transLastTimes = times;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.85f));
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
    }

    public void paint(Graphics g) {
        super.paint(g);
        /*if(true){*/
        g.setColor(Color.white);
        if (timeSlowLastTime > 0) {
            int angle = (int) (360. * (double) timeSlowLastTime / (timeSlowSecs * 1000.));
            g.setColor(new Color(194, 153, 215));
            g.drawString("时空断裂", 10, 50);
            g.drawString(Integer.toString((int) (100 * (1. - timeSlowScale + 0.001))) + "%", 10, 70);
            g.fillArc(60, 50, 50, 50, 90, angle);
            timeSlowLastTime -= timeVal;
            /*g.drawString(Integer.toString( timeSlowLastTime),10,130);*/
        }
        /*if(true){*/
        if (multipleLastTime > 0) {
            int angle = (int) (360. * (double) multipleLastTime / (multipleSecs * 1000.));
            g.setColor(new Color(90, 196, 227));
            g.drawString("影分身", 140, 50);
            g.drawString("x " + Double.toString(multipleScale), 140, 70);
            g.fillArc(190, 50, 50, 50, 90, angle);
            multipleLastTime -= timeVal;
        }
        /*if(true){*/
        if (transLastTimes > 0) {
            g.setColor(new Color(135, 110, 227));
            g.drawString("量子穿梭剩余： " + transLastTimes, 250, 50);
        }
    }

    void EndGame(){
        drawer.cancel();
    }
}
