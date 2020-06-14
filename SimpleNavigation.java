package ca.mcgill.ecse211.lab3;
import static ca.mcgill.ecse211.lab3.Resources.*;
public class SimpleNavigation extends Thread{
  //distance remaining to x and y
  public double distanceToX;
  public double distanceToY;
  //distance to the point
  double distance;
  //angle to point
  public double theta;
  //angle it needs to change to turn to point
  public double deltaTheta;
  public boolean isNavigating;
  //array to hold the data from ultrosonic sensor
  double position[] = new double[3];
 
  
  /**
   * This method lets the robots travel to each point.
   * @param x
   * @param y
   */
  public void travelTo(double x, double y) {
    isNavigating = true;
    position = odometer.getXYT();
    //store the value of x, y and theta into the postion array
    distanceToX = x - position[0];
    distanceToY = y - position[1];
    //calculate the distance from the robot and the point
    distance = Math.sqrt(Math.pow(distanceToX, 2)+Math.pow(distanceToY, 2));
    theta = convertToDegree(Math.atan2(distanceToX, distanceToY));
    //calculate the theta it needs to turn
    deltaTheta = theta - position[2];

    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    turnTo(deltaTheta);
    leftMotor.rotate(convertDistance(distance), true);
    rightMotor.rotate(convertDistance(distance), false);
    isNavigating = false;


  }
  /**
   * Make the turn using theta calculated.
   * @param theta
   */
  public void turnTo(double theta) {
    theta = getMinimalAngle(theta);
    leftMotor.rotate(convertAngle(theta),true);
    rightMotor.rotate(-convertAngle(theta),false);
  }
  
  /**
   * if it is navigating, return true, If not, returns false.
   * @return
   */
  public boolean isNavigating(){
    return isNavigating;
  }


  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * 
   * @param distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }


  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
   * angle.
   * 
   * @param angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * TRACK * angle / 360.0);
  }
  /**
   * This method converts the angle to degree.
   * @param theta
   * @return
   */
  public static double convertToDegree(double theta){
    return theta * 180 / Math.PI;
  }

  /**
   * This method returns the minimal angle. 
   * @param theta
   * @return
   */
  public double getMinimalAngle(double theta) {
    if (theta > 180) {
      theta =  theta - 360;
    } else if (theta < -180) {
      theta = theta + 360;
    }
    return theta; 
  }
}

