package org.example.lib.control;

import org.example.lib.math.CurvePoint;
import org.example.lib.math.Point;

public class DiffMoveToPoint extends Controller {
    public Differential model;
    private CurvePoint point;

    public DiffMoveToPoint(CurvePoint p) {
        point = p;
        model = new Differential(1.6, 70.3, 70, new Point(150, 125));
    }

    public void run() throws InterruptedException {
        moveToPoint(point);
    }

    public void moveToPoint(CurvePoint point) throws InterruptedException {
        double absoluteAngleToPoint = Math.atan2(point.y - model.model_y, point.x - model.model_x);
        double relativeAngleToPoint = (absoluteAngleToPoint - model.model_theta) - (Math.PI / 2.0);
//        System.out.println(relativeAngleToPoint);

        double vl = point.speed + (point.turnSpeed * relativeAngleToPoint);
        double vr = point.speed - (point.turnSpeed * relativeAngleToPoint);

        model.run(vl, vr);

    }

}
