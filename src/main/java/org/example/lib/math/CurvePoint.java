package org.example.lib.math;

import java.util.ArrayList;

public class CurvePoint {
    public double x;
    public double y;
    public double speed;
    public double turnSpeed;
    public double lookAhead;
    public double angle;

    public CurvePoint() {}

    public CurvePoint(CurvePoint point) {
        this.x = point.x;
        this.y = point.y;
        this.speed = point.speed;
        this.turnSpeed = point.turnSpeed;
        this.lookAhead = point.lookAhead;
        this.angle = point.angle;

    }

    public CurvePoint(double x, double y, double speed, double turnSpeed, double lookAhead) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.turnSpeed = turnSpeed;
        this.lookAhead = lookAhead;
    }

    public CurvePoint(double x, double y, double speed, double turnSpeed, double lookAhead, double angle) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.turnSpeed = turnSpeed;
        this.lookAhead = lookAhead;
        this.angle = angle;
    }

    public CurvePoint(double x, double y, double speed, double turnSpeed) {
        this(x, y, speed, turnSpeed, 0);
    }

    public Point toPoint(){
        return new Point(this.x, this.y);
    }

    public void setPoint(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public void setPathPoint(CurvePoint p) {
        this.x = p.x;
        this.y = p.y;
        this.speed = p.speed;
        this.turnSpeed = p.turnSpeed;
        this.lookAhead = p.lookAhead;
        this.angle = p.angle;
    }

    public static ArrayList<Double> toXList(ArrayList<CurvePoint> points) {
        ArrayList<Double> values = new ArrayList<Double>();

        for (CurvePoint p : points) {
            values.add(p.x);
        }

        return values;

    }

    public static ArrayList<Double> toYList(ArrayList<CurvePoint> points) {
        ArrayList<Double> values = new ArrayList<Double>();

        for (CurvePoint p : points) {
            values.add(p.y);
        }

        return values;

    }

}
