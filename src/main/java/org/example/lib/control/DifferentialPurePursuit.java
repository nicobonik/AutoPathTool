package org.example.lib.control;

import org.example.lib.math.CurvePoint;
import org.example.lib.math.MathFunctions;
import org.example.lib.math.PID;
import org.example.lib.math.Point;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.Arrays;

public class DifferentialPurePursuit extends Controller {
    public Differential model;

    public ArrayList<ArrayList<CurvePoint>> sections;

    public CurvePoint currentPoint = new CurvePoint();

    private PID turnPID = new PID(-3, 0, -2, 0.05, 0, -10, 10);

    private volatile boolean start = false;
    public volatile int currentSection = 0;

    XYSeries points;
    XYSeries series;

    public DifferentialPurePursuit(ArrayList<ArrayList<CurvePoint>> sections) {
        this.sections = sections;
    }

    public DifferentialPurePursuit(ArrayList<ArrayList<CurvePoint>> sections, double radius) {
        this.sections = sections;
        model = new Differential(1.6, 10.3, 40, new Point(138.34, 21.6));
    }

    public void run() throws InterruptedException {
        int sectionNum = 0;
        for (ArrayList<CurvePoint> section : sections) {
            start = false;
            while(Math.abs(model.model_x - section.get(section.size() - 1).x) + Math.abs(model.model_y - section.get(section.size() - 1).y) > 0.5) {
                CurvePoint point = findFollowPoint(section);
                moveToPoint(point);
                updateGraph();
            }
            sectionNum++;
            currentSection = sectionNum;
            waitForStart();

        }
    }

    public CurvePoint findFollowPoint(ArrayList<CurvePoint> path) {
        CurvePoint followPoint = new CurvePoint(path.get(0));

        ArrayList<Point> circleIntersections = new ArrayList<>();

        //make 2 point path compatible with acceleration curve
        if(path.size() == 2) {
            CurvePoint placeholder = path.get(0);
            path.add(0, placeholder);
        }

        for(int i = 0; i < path.size() - 1; i++) {
            CurvePoint start = path.get(i);
            CurvePoint end = path.get(i + 1);

            double tempTheta = start.speed < 0 ? model.model_theta - Math.PI : model.model_theta;
//            double tempTheta = model.model_theta;

            if(path.indexOf(end) == path.size() - 1) {
                if(end.x < start.x) {
                    circleIntersections = MathFunctions.lineCircleIntersect(start.toPoint(), end.toPoint(), end.lookAhead, new Point(model.model_x, model.model_y), false, true);
                }else {
                    circleIntersections = MathFunctions.lineCircleIntersect(start.toPoint(), end.toPoint(), end.lookAhead, new Point(model.model_x, model.model_y), true, false);
                }

                double closestAngle = Double.MAX_VALUE;
                for (Point intersection : circleIntersections) {
                    System.out.println(circleIntersections.size());
                    double angle = Math.atan2(intersection.y - model.model_y, intersection.x - model.model_x);
                    double deltaAngle = Math.abs((angle - tempTheta) - (Math.PI / 2.0));
                    if (deltaAngle < closestAngle) {
                        closestAngle = deltaAngle;
                        followPoint.setPathPoint(end);
                        followPoint.setPoint(intersection);
//                        return end;
                    }

                    double maxX = Math.max(start.x, end.x);
                    double minX = Math.min(start.x, end.x);


                    if (followPoint.x > maxX || followPoint.x < minX) {
                        followPoint.setPoint(end.toPoint());
//                        System.out.println("wrapping lookahead to end of path");
//                        System.out.println(followPoint.x + ", " + followPoint.y);
                    }
                }

                double slowDownStart = 15;
                double minSpeed = 0.4;
                double distanceToTarget = Math.hypot(end.x - model.model_x, end.y - model.model_y);
                if(distanceToTarget < slowDownStart) {
                    double m = (1 - minSpeed) / slowDownStart;
                    followPoint.speed *= m * (distanceToTarget - slowDownStart) + 1;
//                    System.out.println("changing speed");
                }

            } else {

                circleIntersections = MathFunctions.lineCircleIntersect(start.toPoint(), end.toPoint(), end.lookAhead, new Point(model.model_x, model.model_y), false, true);


                double closestAngle = Double.MAX_VALUE;
                for (Point intersection : circleIntersections) {
                System.out.println(circleIntersections.size());
                    double angle = Math.atan2(intersection.y - model.model_y, intersection.x - model.model_x);
                    double deltaAngle = Math.abs((angle - tempTheta) - (Math.PI / 2.0));
                    if (deltaAngle < closestAngle) {
                        closestAngle = deltaAngle;
                        followPoint.setPathPoint(end);
                        followPoint.setPoint(intersection);
                    }
                }


//                System.out.println("hello world??");
                if(path.indexOf(start) == 0) {
                    double slowDownStart = 15;
                    double minSpeed = 0.1;
                    double distanceToTarget = Math.hypot(start.x - model.model_x, start.y - model.model_y);
//                    System.out.println(distanceToTarget);
                    if (distanceToTarget < slowDownStart) {
                        double m = (1 - minSpeed) / slowDownStart;
                        followPoint.speed *= m * (distanceToTarget - slowDownStart) + 1;
//                        System.out.println("changing speed " + currentSection);
//                        System.out.println(followPoint.x + ", " + followPoint.y);
                    }
                }
            }
        }
//        System.out.println(circleIntersections.toString());

//        System.out.println(followPoint.x + ", " + followPoint.y);
        return followPoint;
    }

    public void moveToPoint(CurvePoint point) throws InterruptedException {
        double absoluteAngleToPoint = Math.atan2(point.y - model.model_y, point.x - model.model_x);
        double relativeAngleToPoint = (absoluteAngleToPoint - model.model_theta) - (Math.PI / 2.0);

        if(point.speed < 0) {
            relativeAngleToPoint -= Math.PI;
        }

        relativeAngleToPoint = angleWrap(relativeAngleToPoint);

        if(Math.abs(relativeAngleToPoint) > Math.PI / 6.0) {
            point.speed = 0;
        }


//        System.out.println(relativeAngleToPoint);

        double vl = point.speed + (point.turnSpeed * relativeAngleToPoint);
        double vr = point.speed - (point.turnSpeed * relativeAngleToPoint);

        model.run(vl, vr);

    }

    private void waitForStart() {
        while (!start) {
            Thread.onSpinWait();
        }
    }

    public void start() {
        start = true;
    }

    public void graph() {
        super.graph();

        chart.getStyler().setXAxisMin(-125.0);
        chart.getStyler().setXAxisMax(245.0);
        chart.getStyler().setYAxisMin(-5.0);
        chart.getStyler().setYAxisMax(366.0);

        window = new SwingWrapper<XYChart>(chart);

        window.displayChart();

        points = chart.addSeries("path points", generateXPath(sections), generateYPath(sections));

        points.setLineColor(XChartSeriesColors.GREEN);
        points.setLineStyle(SeriesLines.DASH_DASH);
        points.setMarker(SeriesMarkers.DIAMOND);

        System.out.println(chart);
        System.out.println(model);
        series = chart.addSeries("robot position", model.xList, model.yList);

        series.setLineColor(XChartSeriesColors.BLUE);
        series.setLineStyle(SeriesLines.SOLID);
        series.setMarker(SeriesMarkers.NONE);
    }

    public void moveToNextSection() {
        start = true;
    }

    public void updateGraph() {
        chart.updateXYSeries("robot position", model.xList, model.yList, null);
        window.repaintChart();
    }

    public ArrayList<Double> generateXPath(ArrayList<ArrayList<CurvePoint>> sections) {
        ArrayList<Double> points = new ArrayList<>();

        for (ArrayList<CurvePoint> path : sections) {

            for (CurvePoint point : path) {
                points.add(point.x);
            }

        }

        return points;
    }

    public ArrayList<Double> generateYPath(ArrayList<ArrayList<CurvePoint>> sections) {
        ArrayList<Double> points = new ArrayList<Double>();

        for (ArrayList<CurvePoint> path : sections) {

            for (CurvePoint point : path) {
                points.add(point.y);
            }

        }

        return points;
    }

    public double fullAngleWrap(double angle) {
        while(angle <= 0) {
            angle += 2.0 * Math.PI;
        }
        while(angle > 2.0*Math.PI) {
            angle -= 2.0*Math.PI;
        }
        return angle;
    }

    public double angleWrap(double angle) {
        while(angle <= -Math.PI) {
            angle += 2.0 * Math.PI;
        }
        while(angle > Math.PI) {
            angle -= 2.0 * Math.PI;
        }
        return angle;
    }

}
