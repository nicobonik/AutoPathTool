package org.example.lib.control;

import org.example.lib.math.Point;

public class Differential extends Model {

    double radius, wheelBase;
    double multiplier;

    public Differential(double radius, double wheelBase, double multiplier, Point startPos) {
        this.radius = radius;
        this.wheelBase = wheelBase;
        this.multiplier = multiplier;
        xList.clear();
        yList.clear();
        xList.add(startPos.x);
        yList.add(startPos.y);
        loopTime = 5;
        model_x = startPos.x;
        model_y = startPos.y;

    }

    public void run(double left, double right) throws InterruptedException {

        left = clip(left, -1, 1);
        right = clip(right, -1, 1);

        double v = (left + right) / 2.0;
        double dTheta = (left - right) / wheelBase;

        model_theta += dTheta * (loopTime / 1000.0) * multiplier;
        model_x += (v * Math.cos(model_theta + (Math.PI / 2.0))) * (loopTime / 1000.0) * multiplier;
        model_y += (v * Math.sin(model_theta + (Math.PI / 2.0))) * (loopTime / 1000.0) * multiplier;

        super.run();
    }

}
