package org.example.lib.control;

import org.example.lib.math.MathFunctions;
import org.example.lib.math.CurvePoint;
import org.example.lib.math.PID;
import org.example.lib.math.Point;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;

public class PurePursuit extends Controller {
    public RealMecanum model;

    public ArrayList<ArrayList<CurvePoint>> sections;

    public CurvePoint currentPoint = new CurvePoint();

    private PID turnPID = new PID(-3, 0, -2, 0.05, 0, -10, 10);

    private volatile boolean start = false;
    public volatile int currentSection = 0;

    XYSeries points;
    XYSeries series;

    public PurePursuit(ArrayList<ArrayList<CurvePoint>> sections) {
        this.sections = sections;
    }

    public PurePursuit(ArrayList<ArrayList<CurvePoint>> sections, double radius, boolean fieldSide) {
        this.sections = sections;
        model = new RealMecanum(5, 40, 40, new Point(138.34, 21.6));
        model.setMultiplier(35.6);
        //flip for blue side
        if(fieldSide) {
            model.model_x = 245 - model.model_x - 122;
            for(ArrayList<CurvePoint> path : this.sections) {
                for (CurvePoint point : path) {
                    point.setPoint(new Point(245 - point.x - 122, point.y));
                }
            }
        }

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

        ArrayList<Point> circleIntersections;

        //make 2 point path compatible with acceleration curve
        if(path.size() == 2) {
            CurvePoint placeholder = path.get(0);
            path.add(0, placeholder);
        }

        for(int i = 0; i < path.size() - 1; i++) {
            CurvePoint start = path.get(i);
            CurvePoint end = path.get(i + 1);


            if(path.indexOf(end) == path.size() - 1) {
                if(end.x < start.x) {
                    circleIntersections = MathFunctions.lineCircleIntersect(start.toPoint(), end.toPoint(), end.lookAhead, new Point(model.model_x, model.model_y), false, true);
                }else {
                    circleIntersections = MathFunctions.lineCircleIntersect(start.toPoint(), end.toPoint(), end.lookAhead, new Point(model.model_x, model.model_y), true, false);
                }

                double closestAngle = Double.MAX_VALUE;
                for (Point intersection : circleIntersections) {
                    double angle = MathFunctions.fullAngleWrap(Math.atan2(intersection.y - model.model_y, intersection.x - model.model_x));
                    double relativePointAngle = MathFunctions.fullAngleWrap(Math.atan2(end.y - start.y, end.x - start.x));
                    double deltaAngle = Math.abs(MathFunctions.calcAngularError(relativePointAngle, angle));

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
                double minSpeed = 0.1;
                double distanceToTarget = Math.hypot(end.x - model.model_x, end.y - model.model_y);
                if(distanceToTarget < slowDownStart) {
                    double m = (1 - minSpeed) / slowDownStart;
                    followPoint.speed *= m * (distanceToTarget - slowDownStart) + 1;
                    System.out.println("changing speed");
                }

            } else {

                circleIntersections = MathFunctions.lineCircleIntersect(start.toPoint(), end.toPoint(), end.lookAhead, new Point(model.model_x, model.model_y), false, true);

//                System.out.println(circleIntersections.size());

                double closestAngle = Double.MAX_VALUE;
                for (Point intersection : circleIntersections) {
                    double angle = MathFunctions.fullAngleWrap(Math.atan2(intersection.y - model.model_y, intersection.x - model.model_x));
                    double relativePointAngle = MathFunctions.fullAngleWrap(Math.atan2(end.y - start.y, end.x - start.x));
                    double deltaAngle = Math.abs(MathFunctions.calcAngularError(relativePointAngle, angle));

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

        return followPoint;
    }

    private void waitForStart() {
        while(!start) {}
    }

    public void start() {
        start = true;
    }

    public void moveToPoint(CurvePoint point) throws InterruptedException {
        double absoluteAngleToPoint = Math.atan2(point.y - model.model_y, point.x - model.model_x);

        double robotAngleToPoint = point.angle - model.model_theta;

        double pointAngle = Math.abs(robotAngleToPoint) > Math.PI ? point.angle - (Math.signum(robotAngleToPoint)*2.0*Math.PI) : point.angle;

        double turnSpeed = turnPID.update(pointAngle, model.model_theta);

        fieldCentricMecanum(Math.cos(absoluteAngleToPoint) * point.speed, Math.sin(absoluteAngleToPoint) * point.speed, turnSpeed * point.turnSpeed);
    }

    private void fieldCentricMecanum(double x, double y, double turn) throws InterruptedException {
        x *= -1.0;
        turn *= -1.0;

        double magnitude = Math.abs(x) + Math.abs(y) + Math.abs(turn);
        if(magnitude > 1.0) {
            x *= 1.0 / magnitude;
            y *= 1.0 / magnitude;
            turn *= 1.0 / magnitude;
        }

        double power = (2.0 / Math.sqrt(2.0)) * Math.hypot(x, y);
        double theta = Math.atan2(y, x) - model.model_theta;

        double rx = (Math.cos(theta - (Math.PI / 4))) * power;
        double lx = (Math.sin(theta - (Math.PI / 4))) * power;

        double fl = lx - turn;
        double fr = rx + turn;
        double bl = rx - turn;
        double br = lx + turn;

        model.run(fl, fr, bl, br);

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

//        System.out.println("initializing robot pose series");
        series = chart.addSeries("robot position", model.xList, model.yList);
//        System.out.println(series);
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

    protected static double clip(double x, double min, double max) {
        if (x < min) {
            return min;
        } else if (x > max) {
            return max;
        } else {
            return x;
        }
    }

}
