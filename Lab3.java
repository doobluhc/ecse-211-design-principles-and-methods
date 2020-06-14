// Lab2.java
package ca.mcgill.ecse211.lab3;

import lejos.hardware.Button;
import static ca.mcgill.ecse211.lab3.Resources.*;


/**
 * The main driver class for the odometry lab.
 */
public class Lab3 {
  static boolean isDetected = false;
  //map1
 public static int[] x = {0,1,2,2,1};
 public static int[] y = {2,1,2,1,0};
  //map2
//  public static int[] x = {1,0,2,2,1};
//  public static int[] y = {1,2,2,1,0};
//  //map3
//  public static int[] x = {1,2,2,0,1};
//  public static int[] y = {0,1,2,2,1};
  //map4
//  public static int[] x = {0,1,1,2,2};
//  public static int[] y = {1,2,0,1,2};


  static int index = 0;
  /**
   * The main class.
   *
   * @param args
   */
  public static void main(String[] args) {
    int buttonChoice;
    new Thread(odometer).start();
    new Thread(new Display()).start();

    //display the options
    do{Display.showText("< Left     | Right >",
        "           |        ",
        " Simple    | Navigation"    ,
        "Navigation |    "                  );

    buttonChoice = Button.waitForAnyPress();
    }while(buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT );


    //start the navigation without obstacle avoidance
    if (buttonChoice == Button.ID_LEFT) {
      (new Thread(){
        public void run() {
          SimpleNavigation simpleNavigation = new SimpleNavigation();

          for (int i = 0; i < x.length; i++) {
            simpleNavigation.travelTo(x[i] * TILE_SIZE, y[i] * TILE_SIZE);
          }
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }).start();


    }
    else{
      //start the polling thread to get data from the poller
      Thread pollingThread = (new Thread() {
        public void run() {
          float[] usData = new float[US_SENSOR.sampleSize()];
          US_SENSOR.getDistanceMode().fetchSample(usData, 0);

          int distance;

            US_SENSOR.fetchSample(usData, 0);
            distance = (int) (usData[0] * 100.0);
            if (distance < BAND_CENTER && distance != 0) { // If object detected ignoring zero
              PController pController = new PController();
              pController.processUSData(distance);                                            // readings
              setDetectionStatus(true); // set object detected to true
            }

            try {
              Thread.sleep(40);
            } catch (Exception e) {
            }
          }

      });
      pollingThread.start();//start the thread


      try {
        Thread.sleep(5000);
      } catch (Exception e) {
        // TODO: handle exception
      }

    }

    while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
    }

    System.exit(0);


  }


/**
 * set the detection status.
 * @param status
 */
  public synchronized static void setDetectionStatus(boolean status) {
    isDetected = status;
  }
  /**
   * return detection status.
   * @return
   */
  public synchronized static boolean getDetectionStatus() {
    return isDetected;
  }

}
