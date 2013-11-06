/*
 * XbeeonSPOT.java
 *
 * Created on Apr 9, 2013 11:07:13 AM;
 */

package org.sunspotworld;
import java.io.IOException;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
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
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * This connects Xbee as a peripheral, extending radio coverage
 * @author Yuting Zhang <ytzhang@bu.edu>
 */

public class XbeeonSPOT extends MIDlet {
    
    static final boolean debug = true;
    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    private EDemoBoard eDemo = EDemoBoard.getInstance();
    private ILightSensor light = (ILightSensor)Resources.lookup(ILightSensor.class);
    private ISwitch sw2 = (ISwitch) Resources.lookup(ISwitch.class, "SW2");
    private ISwitch sw1 = (ISwitch) Resources.lookup(ISwitch.class, "SW1");
    private static final int HOST_PORT = 65;

    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Hello, world");
        BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
        RadiogramConnection rCon = null;
        Datagram dg = null;
        try {
        rCon = (RadiogramConnection) Connector.open("radiogram://broadcast:" + HOST_PORT);
            dg = rCon.newDatagram(50);  // only sending 12 bytes of data
        } catch (Exception e) {
            e.printStackTrace();
        }
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

       
        eDemo.initUART(9600, 8, 0, 1);
        while(true){
           int state = 0;
           //state machine
           switch (state) {
               case 0:
                   dg.reset();
                   pingMoteOne();
                   byte response[] = readBytesFromUART(11);
                   break;
               case 1:
                   
               default:break;
           }
        if (sw2.isClosed()) {                  // done when switch is pressed
            uartSender();
            Utils.sleep(1000);                  // wait 1 second
            readBytesFromUART(11);
        }
        else if (sw1.isClosed()) {
            uartGetRSSI();
            Utils.sleep(1000);                  // wait 1 second
            readBytesFromUART(11);
        }
        
        Utils.sleep(1000);
        }
        
    }
    private void uartGetRSSI() {
        byte[] snd = new byte[8];
        snd[0] = (byte)0x7E;    //start of api 
        snd[1] = (byte)0x00;    //msb of length
        snd[2] = (byte)0x04;    //lsb of length
        snd[3] = (byte)0x08;    // api frame for transmit
        snd[4] = (byte)0x52;    // ack
        snd[5] = (byte)0x44;    // 64-bit addr 
        snd[6] = (byte)0x42;
        snd[7] = (byte)0x1F; //checksum
        
            
        eDemo.writeUART(snd);
        
    }
    public void uartSender(){
        byte[] snd = new byte[19];
        snd[0] = (byte)0x7E;    //start of api 
        snd[1] = (byte)0x00;    //msb of length
        snd[2] = (byte)0x0F;    //lsb of length
        snd[3] = (byte)0x10;    // api frame for transmit
        snd[4] = (byte)0x01;    // ack
        snd[5] = (byte)0x00;    // 64-bit addr 
        snd[6] = (byte)0x13;
        snd[7] = (byte)0xA2;
        snd[8] = (byte)0x00;
        snd[9] = (byte)0x40;
        snd[10] = (byte)0xA0;
        snd[11] = (byte)0x3C;
        snd[12] = (byte)0x9E;
        snd[13] = (byte)0x2D;   //16-bit
        snd[14] = (byte)0x0F;
        snd[15] = (byte)0x00;
        snd[16] = (byte)0x00;
        snd[17] = (byte)0x41; //ascii A
       snd[18] = (byte)0x02; //checksum
        
            
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
        
        try{
        if(eDemo.availableUART()>1){
            eDemo.readUART(buffer, 0, buffer.length);
            System.out.println(convertToHexString(buffer));
       
            
        }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    private static String convertToHexString(byte[] data) {
StringBuffer buf = new StringBuffer();
for (int i = 0; i < data.length; i++) {
    int halfbyte = (data[i] >>> 4) & 0x0F;
    int two_halfs = 0;
    do {
        if ((0 <= halfbyte) && (halfbyte <= 9))
            buf.append((char) ('0' + halfbyte));
        else
            buf.append((char) ('a' + (halfbyte - 10)));
            halfbyte = data[i] & 0x0F;
        } while(two_halfs++ < 1);
    }
return buf.toString();
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
