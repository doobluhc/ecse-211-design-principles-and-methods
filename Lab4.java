package ca.mcgill.ecse211.lab4;

/**
 * This is the main class of Lab4
 */

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import static ca.mcgill.ecse211.lab4.Resources.*;


public class Lab4 {


  private static boolean isRisingEdge = true;

  public static void main(String[] args) {

    int buttonChoice;

    do {

      LCD.clear();

      // for user to choose which mode
      LCD.drawString("< Left | Right >", 0, 0);
      LCD.drawString("       |        ", 0, 1);
      LCD.drawString("Rising |Falling ", 0, 2);
      LCD.drawString(" Edge  |  Edge  ", 0, 3);
      LCD.drawString("       |        ", 0, 4);

      buttonChoice = Button.waitForAnyPress();
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

    if (buttonChoice == Button.ID_LEFT) {
      isRisingEdge = true;
    } else {
      isRisingEdge = false;
    }


    // start Odometer and display thread
    new Thread(odometer).start();
    new Thread(new Display()).start();
    // pass rising edge or falling edge to the localizers
    UltrasonicLocalizer ultrasonicLocalizer = new UltrasonicLocalizer(isRisingEdge, US_SENSOR.getDistanceMode());
    LightLocalizer lightLocatizer = new LightLocalizer();
    // call the localize method in
    ultrasonicLocalizer.localize();

    if (Button.waitForAnyPress() == Button.ID_DOWN) {
      System.exit(0);
    }
    while (Button.waitForAnyPress() != Button.ID_RIGHT);

    lightLocatizer.localize();

    while (Button.waitForAnyPress() != Button.ID_RIGHT);
    System.exit(0);
  }

}
