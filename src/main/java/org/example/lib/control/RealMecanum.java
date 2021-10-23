package org.example.lib.control;

import org.example.lib.math.Point;

public class RealMecanum extends Model {

    double radius;
    double wheelBaseY;
    double wheelBaseX;

    double multiplier = 1.0;

    double fl_power, fr_power, bl_power, br_power;

    public RealMecanum(double radius, double wheelBaseX, double wheelBaseY, Point startPos) {
        xList.clear();
        yList.clear();
        model_x = startPos.x;
        model_y = startPos.y;
        xList.add(startPos.x);
        yList.add(startPos.y);
        model_theta = 0;
        this.radius = radius;
        this.wheelBaseX = wheelBaseX;
        this.wheelBaseY = wheelBaseY;
        loopTime = 5;
    }

    public void run(double fl, double fr, double bl, double br) throws InterruptedException {

        fl_power = clip(fl, -1.0, 1.0);
        fr_power = clip(fr, -1.0, 1.0);
        bl_power = clip(bl, -1.0, 1.0);
        br_power = clip(br, -1.0, 1.0);



        //XOZ vectors
        double vx = ((fl_power - fr_power - bl_power + br_power) * (radius / 4.0)) * 0.80; // mecanum strafe inefficiency
        double vy = ((fl_power + fr_power + bl_power + br_power) * (radius / 4.0));
        double w = ((fr_power - fl_power - bl_power + br_power) / (wheelBaseX + wheelBaseY)) * (radius / 4.0);

        //convert polar XOZ vectors to cartesian x,y,theta components
        double Ydy = vy * Math.sin(model_theta);
        double Ydx = vy * Math.cos(model_theta);
        double Xdy = vx * Math.sin(model_theta + (Math.PI / 2.0));
        double Xdx = vx * Math.cos(model_theta + (Math.PI / 2.0));

        model_x += (((Ydy + Xdy) / 2.0) * (loopTime / 1000.0)) * multiplier;
        model_y += (((Ydx + Xdx) / 2.0) * (loopTime / 1000.0)) * multiplier;
        model_theta += (w * (loopTime / 1000.0)) * multiplier;
        if(model_theta < 0) {
            model_theta += 2.0 * Math.PI;
        }
        if(model_theta > 2.0 * Math.PI) {
            model_theta -= 2.0 * Math.PI;
        }

        super.run();
    }


    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

}
