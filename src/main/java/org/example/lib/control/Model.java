package org.example.lib.control;

import org.example.lib.framework.Message;
import org.example.lib.framework.MessageHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class with the essentials for both making a model and sending the data to the graphing utility.
 */
public abstract class Model {
    /**
     * world x position, in arbitrary units
     */
    public volatile double model_x;
    /**
     * world y position, in arbitrary units
     */
    public volatile double model_y;
    /**
     * world angle, in radians
     */
    public volatile double model_theta;
    /**
     * the time between loops in milliseconds to mimic the response of a real system
     */
    public long loopTime;

    /**
     * list of all x values
     */
    public List<Double> xList = new ArrayList<Double>();
    /**
     * list of all y values
     */
    public List<Double> yList = new ArrayList<Double>();
    /**
     * list of all angle values
     */
    public List<Double> thetaList = new ArrayList<Double>();

    private Message position = new Message("yeet");

    /**
     * Model constructor, adds a dummy value to all lists to avoid {@link NullPointerException} from being thrown
     */
    public Model(){
        xList.add(model_x);
        yList.add(model_y);
        MessageHandler.initialize();
        MessageHandler.addMessage(position);
        MessageHandler.update();
    }


    public String toString() {
        return "x: " + round(model_x, 2) + ", y: " + round(model_y, 2) + ", theta: " + round(model_theta, 2);
    }

    /**
     *
     * @throws InterruptedException  if the thread is interrupted during a sleep defined in ms by {@code loopTime}.
     */
    public void run() throws InterruptedException {
        xList.add(model_x);
        yList.add(model_y);
        thetaList.add(model_theta);
        Thread.sleep(loopTime);
        position.setMessage(this.toString());
        MessageHandler.update();
    }

    protected static double clip(double x, double min, double max) {
        if (x < min) {
            return min;
        } else if (x > max) {
            return max;
        } else {
            return x;
        }
    }

    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

}
