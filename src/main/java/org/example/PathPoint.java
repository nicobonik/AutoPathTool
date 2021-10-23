package org.example;

import javafx.scene.shape.Rectangle;

public class PathPoint {

    public int id;
    public double x;
    public double y;
    public Rectangle rect;

    public PathPoint(int id, double x, double y, Rectangle rect) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.rect = rect;
    }

    public PathPoint(double x, double y, Rectangle rect) {
        this.x = x;
        this.y = y;
        this.rect = rect;
    }

}
