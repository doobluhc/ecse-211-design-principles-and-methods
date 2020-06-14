package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class PController extends UltrasonicController {



  public PController() {
    this.filterControl = 0;
    LEFT_MOTOR.setSpeed(MOTOR_SPEED); // Initialize motor rolling forward
    RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();

  }

  /**
   * This method processes the data from the sensor and adjusts the wheel 
   * speed based on the distance between the robot and the walls.
   */
  @Override
  public void processUSData(int distance) {
    filter(distance);
    //distanceDiff = reference control value - measured distance from the wall 
    int distanceDiff = BAND_CENTER - this.distance;//the difference between the actual distance and bandcenter
    int deltaSpeed = 3*Math.abs(distanceDiff);//change of speed that is proportional to the distance difference

    //when the distanceDiff is small , no need to adjust the speeds
    if (Math.abs(distanceDiff) <= BAND_WIDTH ) {
      LEFT_MOTOR.setSpeed(MOTOR_SPEED); 
      RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
    }
    // if it is too close, back away so that the robot does not 
    // touch the wall(for quick turn at the corner)
    else if (this.distance < 30) {
      LEFT_MOTOR.setSpeed(MOTOR_SPEED); 
      RIGHT_MOTOR.setSpeed(MOTOR_SPEED + 40);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.backward();
    }

    //when the difference is within the accepted range
    else if (Math.abs(distanceDiff) <= MAX_DISTANCE_DIFF ){
      //when the actual distance is larger than the bandcenter
      //turn to the left slightly
      if(distanceDiff < 0) {
        LEFT_MOTOR.setSpeed(MOTOR_SPEED - deltaSpeed); 
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED + deltaSpeed);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
      }
      //when the actual distance is smaller than the bandcenter
      //turn to the right slightly
      else if (distanceDiff > 0) {
        LEFT_MOTOR.setSpeed(MOTOR_SPEED + deltaSpeed); 
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED - deltaSpeed);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
      }
    }

    //when the difference is not in the accepted range
    else if (Math.abs(distanceDiff) > MAX_DISTANCE_DIFF) {
      //when the actual distance is way smaller than the bandcenter
      //make a turn to the right so that the robot is not very far from the wall
      if (distanceDiff > 0) {
        LEFT_MOTOR.setSpeed(200); 
        RIGHT_MOTOR.setSpeed(100);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
      }
      //when the actual distance is way larger than the bandcenter
      //make a wide turn to the left
      else if (distanceDiff < 0) {
        LEFT_MOTOR.setSpeed(100); 
        RIGHT_MOTOR.setSpeed(200);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
      }
    }
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
