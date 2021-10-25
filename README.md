# AutoPathTool

This is my Autonomous Wheeled Mobile Robot simulator. It has a few boilerplate code classes that allow for full customization of robot behavior, allowing one to make their own models for their robot. 
If you have a kinematic model of your wheeled mobile robot, it is possible to use this simulator to test your code. 

![Auto Path GIF](https://github.com/nicobonik/AutoPathTool/blob/main/docs/images/2021-10-23%2010-13-40.gif)

*Example of Holonomic Mecanum model running a custom Pure Pursuit Algorithm with Motion Profiling*

# About This Project

This project has mostly been used for me to test out my path controllers for Autonomous control in FIRST Tech Challenge. It has been very useful to use as a sort of unit test for all of the math behind robot path following.
This project is unique because it uses true kinematic models of robots, which means every step has to be programmed down to the wheel velocities just like a real robot.

## Model

The `Model` object translates any user-defined input into an (x,y,theta) pose. 

![diff example](https://github.com/nicobonik/AutoPathTool/blob/main/docs/images/diffModel.png)

*Example of a tank-style robot Model*

This example model shows how a Model should be set up:
1. every loop, it updates the x, y, and theta positions with respect to the input and loopTime
2. it calls the Model superclass `run()` method to make sure everything in the backend system gets updated.

## Controller
 README WIP


# Using This Project
This project was made using intelliJ, so I am going to show how to set it up with that. If you are using a different IDE, the steps may be different but are most likely similar enough that you will be able to find a solution on the internet somewhere

## Step 1: Clone this Git Repository

`git clone https://github.com/nicobonik/AutoPathTool.git`

## Step 2: Open the project in IntelliJ and sync your maven project. 

If you are having issues with Maven building properly, you may need to add a line to `pom.xml`

![pom.xml edit](https://github.com/nicobonik/AutoPathTool/blob/main/docs/images/pomEdit.png)

add this `<executable>` line with a path to your java jdk.

## Step 3: Add Run Configuration

Go to the top right corner of IntelliJ and click "Add Configuration..."

![add config](https://github.com/nicobonik/AutoPathTool/blob/main/docs/images/addConfig.png)

Add a new Maven Configuration.

![maven add config](https://github.com/nicobonik/AutoPathTool/blob/main/docs/images/mavenAddConfig.png)

In the Command Line box, paste this line of code. 

`compiler:compile javafx:run -DforkCount=0 -DreuseForks=false`

![run parameters](https://github.com/nicobonik/AutoPathTool/blob/main/docs/images/runParams.png)

These are the tasks that maven runs along with some extra tags for javafx to run properly.

## Step 4: Run the project

The project should be set up with a small example that you can edit however you want. The example shows a mecanum drivetrain running a custom Pure Pursuit Algorithm.
