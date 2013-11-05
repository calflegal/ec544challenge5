/*
 * XbeeonSPOT.java
 *
 * Created on Apr 9, 2013 11:07:13 AM;
 */

package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.ILightSensor;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * This connects Xbee as a peripheral, extending radio coverage
 * @author Yuting Zhang <ytzhang@bu.edu>
 */
public class XbeeonSPOT extends MIDlet {

    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    private EDemoBoard eDemo = EDemoBoard.getInstance();
    private ILightSensor light = (ILightSensor)Resources.lookup(ILightSensor.class);

    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Hello, world");
        BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
        
        for (int i = 0; i < leds.size(); i++) {
                        leds.getLED(i).setRGB(0, 0, 100);
                        leds.getLED(i).setOn();
                    }
        Utils.sleep(100);
        for (int i = 0; i < leds.size(); i++) {
                        leds.getLED(i).setOff(); 
                    }
        
        long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));

        ISwitch sw2 = (ISwitch) Resources.lookup(ISwitch.class, "SW2");
        eDemo.initUART(9600, 8, 0, 1);
   
        while(true){
        if (sw2.isClosed()) {                  // done when switch is pressed
            uartSender();
            Utils.sleep(1000);                  // wait 1 second
        }
        
        Utils.sleep(1000);
        }
        
    }
    public void uartSender(){
        byte[] snd = new byte[26];
        snd[0] = (byte)0x7E;    //start of api 
        snd[1] = (byte)0x00;    //msb of length
        snd[2] = (byte)0x16;    //lsb of length
        snd[3] = (byte)0x10;    // api frame for transmit
        snd[4] = (byte)0x01;    // ack
        snd[5] = (byte)0x00;    // 64-bit addr coordinator 
        snd[6] = (byte)0x00;
        snd[7] = (byte)0x00;
        snd[8] = (byte)0x00;
        snd[9] = (byte)0x00;
        snd[10] = (byte)0x00;
        snd[11] = (byte)0xFF;
        snd[12] = (byte)0xFF;
        snd[13] = (byte)0xFF;   //16-bit
        snd[14] = (byte)0xFE;
        snd[15] = (byte)0x00;
        snd[16] = (byte)0x00;
        snd[17] = (byte)0x41; //ascii A
        snd[18] = (byte)0x41; //ascii A
        snd[19] = (byte)0x41; //ascii A
        snd[20] = (byte)0x41; //ascii A
        snd[21] = (byte)0x41; //ascii A
        snd[22] = (byte)0x41; //ascii A
        snd[23] = (byte)0x41; //ascii A
        snd[24] = (byte)0x41; //ascii A
        
        byte sum = 0;
        for (int ii=3; ii<25;ii++){
            sum +=snd[ii];
        }
 //       byte sumByte = (byte) (0xFF & sum);
        byte chexum = (byte)(0xff-sum);
        snd[25] = chexum;
            
        eDemo.writeUART(snd);
        
        for (int i = 4; i < 8; i++) {
            leds.getLED(i).setRGB(0, 100, 0);
            leds.getLED(i).setOn();
        }
        Utils.sleep(100);
        for (int i = 4; i < 8; i++) {
            leds.getLED(i).setOff(); 
        }
            
    }
    
    protected void readBytesFromUART(int numBytes) {
        byte[] buffer = new byte[numBytes];
        String returnString = "";
        try{
        if(eDemo.availableUART()>1){
            eDemo.readUART(buffer, 0, buffer.length);
            returnString = returnString + new String(buffer,"US-ASCII").trim();
       
            
        }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    protected void pauseApp() {
        // This is not currently called by the Squawk VM
    }

    /**
     * Called if the MIDlet is terminated by the system.
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true the MIDlet must cleanup and release all resources.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }
}
