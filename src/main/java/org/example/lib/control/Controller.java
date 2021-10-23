package org.example.lib.control;

import java.io.IOException;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;


public abstract class Controller {

    protected XYChart chart;
    protected SwingWrapper<XYChart> window;

    public Controller() {}

    public void run() throws InterruptedException {}

    /**
     * default graphing options
     */
    public void graph() {
        chart  = new XYChartBuilder().build();

        chart.setTitle("Robot Position");
        chart.setXAxisTitle("X");
        chart.setYAxisTitle("Y");
        chart.getStyler().setYAxisMin(-10.0);
        chart.getStyler().setYAxisMax(10.0);
        chart.getStyler().setXAxisMin(-10.0);
        chart.getStyler().setXAxisMax(10.0);

    }

    public void saveGraph(String filename) throws IOException {
        BitmapEncoder.saveBitmapWithDPI(chart, "./" + filename, BitmapEncoder.BitmapFormat.PNG, 300);
        System.out.println("\nsaved graph");
    }

}

