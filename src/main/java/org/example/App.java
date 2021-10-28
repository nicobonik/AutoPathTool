package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.lib.control.DiffMoveToPoint;
import org.example.lib.control.DifferentialPurePursuit;
import org.example.lib.control.PurePursuit;
import org.example.lib.math.CurvePoint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * JavaFX App
 */
public class App extends Application {
    boolean rectDragged = false;
    ArrayList<PathPoint> pointList = new ArrayList<>();
    Pane pathLines = new Pane();
    ImageView robotView;
    boolean runningAuto = false;
    double robotX;
    double robotY;
    double robotTheta;

    @Override
    public void start(Stage stage) throws FileNotFoundException {

        var debugMousePositionCM = new Label();
        var startPathButton = new Button("start");

        startPathButton.setOnAction(ae -> {
           runningAuto = true;
           new Thread(this::run).start();
        });

        var board = new Image(new FileInputStream("res/img/UltimateGoalFieldDark.png"));
        var boardView = new ImageView(board);
        boardView.setFitHeight(800);
        boardView.setFitWidth(800);
        boardView.setPreserveRatio(true);
        var robot = new Image(new FileInputStream("res/img/robot-auto-ui.png"));
        robotView = new ImageView(robot);
        robotView.setFitWidth(100);
        robotView.setFitHeight(100);
        robotView.setPreserveRatio(true);

        var debugPane = new GridPane();
        setGridTabOptions(debugPane);

        var boardPane = new Pane(boardView);
        boardPane.getChildren().add(robotView);
        boardPane.getChildren().add(pathLines);
        boardPane.setOnMouseClicked(e -> {

            if(!rectDragged && !e.isShiftDown()) {
                //rectangle creation
                Rectangle rectPoint = new Rectangle(e.getX() - 25, e.getY() - 12.5, 50, 25);
                PathPoint point = new PathPoint(e.getX(), e.getY(), rectPoint);
                pointList.add(point);
                updatePathVisuals();
                rectPoint.setStroke(Color.BLACK);
                rectPoint.setOpacity(0.5);
                rectPoint.setStrokeWidth(2);
                rectPoint.setCursor(Cursor.OPEN_HAND);

                boardPane.getChildren().add(rectPoint);

                //debugger text box creation
                double convertedX = round((365.76 / 800.0) * (point.x - (800.0 / 3.0)), 2);
                double convertedY = round(365.76 - ((365.76 / 800.0) * point.y), 2);
                var xField = new TextField(Double.toString(convertedX));
                var yField = new TextField(Double.toString(convertedY));

                xField.setOnAction(event -> {
                    double fieldX = Double.parseDouble(xField.getText());
                    double cmConvertedX = round(((800.0 / 365.76) * fieldX) + (800.0 / 3.0), 2);
                    rectPoint.setX(cmConvertedX - 25);
                    point.x = cmConvertedX;
                    updatePathVisuals();

                });
                yField.setOnAction(event -> {
                    double fieldY = Double.parseDouble(yField.getText());
                    double cmConvertedY = round(800 - ((800.0 / 365.76) * fieldY), 2);
                    point.y = cmConvertedY;
                    rectPoint.setY(cmConvertedY - 12.5);
                    updatePathVisuals();
                });

                debugPane.add(xField, 1, point.id);
                debugPane.add(yField, 2, point.id);

                //point removal
                rectPoint.setOnMouseClicked(clickEvent -> {
                    if(clickEvent.isShiftDown()) {
                        boardPane.getChildren().remove(rectPoint);
                        pointList.remove(point);
                        debugPane.getChildren().remove(xField);
                        debugPane.getChildren().remove(yField);
                        updatePathVisuals();
                    }
                });
                rectPoint.setOnMouseDragged(mouseEvent -> {
                    rectDragged = true;
                    rectPoint.setCursor(Cursor.CLOSED_HAND);
                    point.x = mouseEvent.getX();
                    point.y = mouseEvent.getY();
                    rectPoint.setX(mouseEvent.getX() - 25);
                    rectPoint.setY(mouseEvent.getY() - 12.5);
                    double convertX = round((365.76 / 800.0) * (point.x - (800.0 / 3.0)), 2);
                    double convertY = round(365.76 - ((365.76 / 800.0) * point.y), 2);
                    xField.setText(Double.toString(convertX));
                    yField.setText(Double.toString(convertY));
                    updatePathVisuals();
                });
                rectPoint.setOnMouseReleased(mouseEvent -> {
                    rectPoint.setCursor(Cursor.OPEN_HAND);
                });
            }
            rectDragged = false;
        });

        boardPane.setOnMouseMoved(e -> {
            double convertedX = round((365.76 / 800.0) * (e.getSceneX() - (800.0 / 3.0)), 2);
            double convertedY = round(365.76 - ((365.76 / 800.0) * e.getSceneY()), 2);

            debugMousePositionCM.setText(convertedX + ", " + convertedY);
        });
        boardPane.setOnMouseDragged(e -> {
            double convertedX = round((365.76 / 800.0) * (e.getSceneX() - (800.0 / 3.0)), 2);
            double convertedY = round(365.76 - ((365.76 / 800.0) * e.getSceneY()), 2);

            debugMousePositionCM.setText(convertedX + ", " + convertedY);
        });

        var container = new VBox();
        container.getChildren().add(boardPane);
        var scene = new Scene(container, 800, 800);
        stage.setScene(scene);
        stage.setTitle("Path Tool");
        stage.show();



        var debugContainer = new VBox();
        debugContainer.getChildren().add(debugMousePositionCM);
        debugContainer.getChildren().add(startPathButton);
        debugContainer.getChildren().add(debugPane);

        var debugStage = new Stage();
        debugStage.setScene(new Scene(debugContainer, 400, 800));
        debugStage.setTitle("Debugger");
        debugStage.show();

    }

    private void updatePathVisuals() {
        //fill color update
        int idCounter = 0;
        for (PathPoint p : pointList) {
            if(idCounter == 0) {
                p.rect.setFill(Color.BLUE);
                robotView.setX(p.x - 50);
                robotView.setY(p.y - 50);
            }
            else if(idCounter == pointList.size() - 1) {
                p.rect.setFill(Color.RED);
            }
            else {
                p.rect.setFill(Color.LIGHTGRAY);
            }
            p.id = idCounter;
            idCounter++;
        }

        pathLines.getChildren().clear();
        for(int i = 1; i < pointList.size(); i++) {
            PathPoint startPoint = pointList.get(i - 1);
            PathPoint endPoint = pointList.get(i);
            pathLines.getChildren().add(new Line(startPoint.x, startPoint.y, endPoint.x, endPoint.y));
        }
    }

    public void setRobotPosition(double x, double y, double theta) {
        robotView.toFront();
        robotView.setY((800 - ((800.0 / 365.76) * y)) - 50);
        robotView.setX((((800.0 / 365.76) * x) + (800.0 / 3.0)) - 50);
        robotView.setRotate(-Math.toDegrees(theta));
    }

    private void run() {
        ArrayList<ArrayList<CurvePoint>> sections = new ArrayList<>();

        ArrayList<CurvePoint> section1 = new ArrayList<>();
        section1.add(new CurvePoint(138.34, 21.6, 1, 1, 30, 0));
        section1.add(new CurvePoint(80, 125, 1, 1, 30, 0));
        section1.add(new CurvePoint(92, 183, 1, 1, 30, 0));

        ArrayList<CurvePoint> section2 = new ArrayList<>();
        section2.add(new CurvePoint(92, 183, 1, 1, 15, 0));
        section2.add(new CurvePoint(204, 330, 0.8, 1, 15, 0));

        ArrayList<CurvePoint> section3 = new ArrayList<>();
        section3.add(new CurvePoint(204, 330, 1, 0.3, 15, Math.PI));
        section3.add(new CurvePoint(184, 69, 1,0.3, 15, Math.PI));

        ArrayList<CurvePoint> section4 = new ArrayList<>();
        section4.add(new CurvePoint(155, 89, 1, 1, 15, 0));
        section4.add(new CurvePoint(151, 141, 1, 1, 16, 0));

        ArrayList<CurvePoint> section5 = new ArrayList<>();
        section5.add(new CurvePoint(151, 141, 1, 1, 16, 0));
        section5.add(new CurvePoint(204, 315, 1, 1, 15, 0));

        ArrayList<CurvePoint> section6 = new ArrayList<>();
        section6.add(new CurvePoint(204, 315, 1, 1, 15, 0));
        section6.add(new CurvePoint(120, 215, 1, 1, 15, 0));

        sections.add(section1);
        sections.add(section2);
        sections.add(section3);
        sections.add(section4);
        sections.add(section5);
        sections.add(section6);

        PurePursuit controller = new PurePursuit(sections, 1, false);

//        DifferentialPurePursuit controller = new DifferentialPurePursuit(sections, 1);

        controller.graph();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                controller.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        int currentSection = 0;

        while(currentSection < sections.size()) {
            while (controller.currentSection == currentSection) {
                Platform.runLater(() -> setRobotPosition(controller.model.model_x, controller.model.model_y, controller.model.model_theta));
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            controller.start();
            currentSection++;
            Platform.runLater(() -> setRobotPosition(controller.model.model_x, controller.model.model_y, controller.model.model_theta));
        }

    }

//    private void run() {
//        DiffMoveToPoint controller = new DiffMoveToPoint(new CurvePoint(138, 21.6, 1, 2, 30, 0));
//
//        while(true) {
//            try {
//                controller.run();
//                Platform.runLater(() -> setRobotPosition(controller.model.model_x, controller.model.model_y, controller.model.model_theta));
//                Thread.sleep(3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

    public void updatePathVisualsFromListOfLists(ArrayList<ArrayList<CurvePoint>> sections) {
        pointList.clear();
        for (ArrayList<CurvePoint> section : sections) {
            for (CurvePoint point : section) {
                double convertedX = (((800.0 / 365.76) * point.x) + (800.0 / 3.0)) - 50;
                pointList.add(new PathPoint(point.x, point.y, new Rectangle()));

            }



        }
    }

    public void setGridTabOptions(GridPane pane) {
        pane.setAlignment(Pos.TOP_LEFT);
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setPadding(new Insets(10, 10, 10, 10));
    }

    public static double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void main(String[] args) {
        launch();
    }

}