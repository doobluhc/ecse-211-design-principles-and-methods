package ca.mcgill.ecse211.lab4;

/**
 * This is the Light Localizer class.
 */
import lejos.hardware.Sound;
import lejos.hardware.sensor.SensorMode;
import static ca.mcgill.ecse211.lab4.Resources.*;

public class LightLocalizer {

  // create an object of SimpleNavigation class
  public SimpleNavigation navigation;


  private float sample;

  private SensorMode colorMode;

  double[] linesCount;

  public LightLocalizer() {
    colorMode = lightSensor.getRedMode();
    linesCount = new double[4];
    navigation = new SimpleNavigation();
  }

  /**
   * This method uses the light sensor to localize.
   */
  public void localize() {

    int linesNum = 0;
    double deltaX;
    double deltaY;
    double thetaX;
    double thetaY;

    // Set to rotating speed
    leftMotor.setSpeed(ROTATION_SPEED);
    rightMotor.setSpeed(ROTATION_SPEED);

    // go to origin first before rotating
    toOrigin();

    // Scan all four lines
    while (linesNum <= 3) {

      // spin around to count number of lines
      leftMotor.forward();
      rightMotor.backward();
      sample = fetchSample();

      if (sample < 0.3) {// black line detected
        linesCount[linesNum] = odometer.getXYT()[2];
        Sound.beep();
        linesNum++;
      }
    }

    leftMotor.stop(true);
    rightMotor.stop();


    // using the formula from the tutorial
    thetaY = linesCount[3] - linesCount[1];
    thetaX = linesCount[2] - linesCount[0];

    deltaX = -1 * SENSOR_OFFSET * Math.cos(Math.toRadians(thetaY / 2));
    deltaY = -1 * SENSOR_OFFSET * Math.cos(Math.toRadians(thetaX / 2));

    // move to the origin
    odometer.setXYT(deltaX, deltaY, odometer.getXYT()[2] - 6);
    navigation.travelTo(0.0, 0.0);

    leftMotor.setSpeed(ROTATION_SPEED / 3);
    rightMotor.setSpeed(ROTATION_SPEED / 3);

    // correct the angle so that the robot faces the 0 degree
    if (odometer.getXYT()[2] <= 350 && odometer.getXYT()[2] >= 10.0) {
      Sound.beep();
      leftMotor.rotate(convertAngle(-odometer.getXYT()[2]), true);
      rightMotor.rotate(-convertAngle(-odometer.getXYT()[2]), false);
    }
    // stop the robot
    leftMotor.stop(true);
    rightMotor.stop();

  }

  /**
   * This method moves the robot towards the origin.
   */
  public void toOrigin() {


    navigation.turnTo(45);

    leftMotor.setSpeed(ROTATION_SPEED);
    rightMotor.setSpeed(ROTATION_SPEED);

    sample = fetchSample();

    // detect lines
    while (sample > 0.3) {
      sample = fetchSample();
      leftMotor.forward();
      rightMotor.forward();
    }

    leftMotor.stop(true);
    rightMotor.stop();
    Sound.beep();

    // Move back to origin
    leftMotor.rotate(convertDistance(-12), true);
    rightMotor.rotate(convertDistance(-12), false);

  }

  /**
   * This method converts the total distance to the number of wheel rotations needed
   * 
   * @param distance
   * @return distance for wheel rotations.
   * 
   *         From lab 3.
   */
  private static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }

  /*
   * This method converts radians into degrees.
   * 
   * @param angle
   * 
   * @return wheel rotations
   * 
   * From lab 3.
   */
  private static int convertAngle(double angle) {
    return convertDistance(Math.PI * TRACK * angle / 360.0);
  }

  /**
   * This method fetches the distance measured by the light sensor.
   * 
   * @return data collected by the light sensor
   */
  private float fetchSample() {
    float[] ltData = new float[colorMode.sampleSize()];
    colorMode.fetchSample(ltData, 0);
    return ltData[0];
  }

}
