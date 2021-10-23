package org.example.lib.math;

public class CurvePointBuilder {

    private final CurvePoint placeholder = new CurvePoint();

    public CurvePointBuilder() {}

    public CurvePointBuilder x(double x) {
        placeholder.x = x;
        return this;
    }

    public CurvePointBuilder y(double y) {
        placeholder.y = y;
        return this;
    }

    public CurvePointBuilder angle(double angle) {
        placeholder.angle = angle;
        return this;
    }

    public CurvePointBuilder lookahead(double lookahead) {
        placeholder.lookAhead = lookahead;
        return this;
    }

    public CurvePointBuilder speed(double speed) {
        placeholder.speed = speed;
        return this;
    }

    public CurvePointBuilder turnSpeed(double turnSpeed) {
        placeholder.turnSpeed = turnSpeed;
        return this;
    }

    public CurvePoint build() {
        return placeholder;
    }

}
