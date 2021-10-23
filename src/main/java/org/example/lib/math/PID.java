package org.example.lib.math;

public class PID {

    public double Kp, Ki, Kd;
    public double loopTime; // in seconds
    public double tau;
    public double limMin;
    public double limMax;

    private double error, proportional, integral, derivative;
    private double lastError, lastMeasurement;

    public PID(double Kp, double Ki, double Kd, double loopTime, double tau, double limMin, double limMax) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
        this.loopTime = loopTime;
        this.tau = tau;
        this.limMin = limMin;
        this.limMax = limMax;
    }

    public void init() {
        error = 0;
        proportional = 0;
        integral = 0;
        derivative = 0;
        lastError = 0;
        lastMeasurement = 0;
    }

    public double update(double expected, double measurement) {

        error = expected - measurement;
        proportional = Kp * error;

        integral += Ki * loopTime * 0.5 * (error + lastError);

        //dynamic integrator clamping
        double limMinInt, limMaxInt;
        if(proportional < limMax) {
            limMaxInt = limMax - proportional;
        } else {
            limMaxInt = 0.0;
        }
        if(proportional > limMin) {
            limMinInt = limMin - proportional;
        } else {
            limMinInt = 0.0;
        }
        integral = MathFunctions.clip(integral, limMinInt, limMaxInt);

        //low pass filter derivative
        derivative = (2.0 * Kd * (measurement - lastMeasurement) * ((2.0 * tau) - loopTime) * derivative) / ((2.0 * tau) + loopTime);

        lastError = error;
        lastMeasurement = measurement;

        return MathFunctions.clip(proportional + integral + derivative, limMin, limMax);
    }

}
