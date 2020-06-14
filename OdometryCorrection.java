package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection implements Runnable {
  private static Port port = LocalEV3.get().getPort("S1");
  private SensorModes myColor = new EV3ColorSensor(port);
  private SampleProvider myColorSampleProvider = myColor.getMode("RGB");
  private float[] colorSample = new float[myColorSampleProvider.sampleSize()];
  private static final long CORRECTION_PERIOD = 10;

  /*
   * Here is where the odometer correction code should be run.
   */
  public void run() {
    long correctionStart, correctionEnd;
    int numLines = 0;
    //position array for x, y and theta
    double[] position = new double[3];



    while (true) {
      correctionStart = System.currentTimeMillis();
      myColorSampleProvider.fetchSample(colorSample, 0);
      
      //adding offset of the length of the machine
      double offsetx = 6; 
      double offsety = 10;


      //after many tests, 5500 was the maximum sensing of black lines
      if (colorSample[0]*100000 < 6000) {
        Sound.beep();
        //increase number of lines
        numLines ++;

        //get robots current position
        position = odometer.getXYT();


        //goes up
        if(numLines == 1) {
          odometer.setY(TILE_SIZE);
        }
        else if(numLines == 2) {
          odometer.setY(2*TILE_SIZE);
        }
        else if(numLines == 3) {
          odometer.setY(3*TILE_SIZE);
        }


        //goes right
        else if (numLines == 4) {
          odometer.setX(TILE_SIZE);
        }
        else if (numLines == 5) {
          odometer.setX(2*TILE_SIZE);
        }
        else if (numLines == 6) {
          odometer.setX(3*TILE_SIZE);
        }


        //goes down
        else if(numLines == 7) {
          odometer.setY(3*TILE_SIZE);
        }
        else if(numLines == 8) {
          odometer.setY(2*TILE_SIZE);
        }
        else if(numLines == 9) {
          odometer.setY(TILE_SIZE + offsety);
        }


        //goes left
        else if(numLines == 10) {
          odometer.setX(3*TILE_SIZE);
        }
        else if(numLines == 11) {
          odometer.setX(2*TILE_SIZE);
        }
        else if(numLines == 12) {
          odometer.setX(TILE_SIZE + offsetx);
        }
      }


      // this ensures the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
      }
    }
  }

}



