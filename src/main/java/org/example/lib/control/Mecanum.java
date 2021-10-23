package org.example.lib.control;

import org.example.lib.math.Point;

public class Mecanum extends Model {

    /**
     * radius of the wheel
     */
    private double radius = 1.0;

    /**
     * Wheel base for mecanum is HALF of the wheel base, measure to the center and not the other wheel.
     */
    private double wheelBaseX = 1.0;
    private double wheelbaseY = 1.0;

    private double multiplier;

    public Mecanum() {
        model_x = 0;
        model_y = 0;
        model_theta = 0;
        loopTime = 5;
    }

    public Mecanum(Point startPos) {
        xList.clear();
        yList.clear();
        model_x = startPos.x;
        model_y = startPos.y;
        xList.add(model_x);
        yList.add(model_y);
        model_theta = 0;
        loopTime = 5;
    }

    public Mecanum(double radius, double wheelBaseX, double wheelbaseY, double multiplier) {
        this();

        this.radius = radius;
        this.wheelBaseX = wheelBaseX;
        this.wheelbaseY = wheelbaseY;
        this.multiplier = multiplier;
    }



    public void run(double x, double y, double w) throws InterruptedException {

        model_x += (clip(x * (loopTime / 1000.0), -1.0, 1.0)) * multiplier;
        model_y += (clip(y * (loopTime / 1000.0), -1.0, 1.0)) * multiplier;
        model_theta += (clip(w * (loopTime / 1000.0), -7.0, 7.0));

        super.run();
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }

}
