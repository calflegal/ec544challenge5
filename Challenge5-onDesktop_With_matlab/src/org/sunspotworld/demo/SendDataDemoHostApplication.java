/*
 * SendDataDemoHostApplication.java
 *
 * Copyright (c) 2008-2009 Sun Microsystems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.sunspotworld.demo;


import com.sun.spot.io.j2me.radiogram.*;

import com.sun.spot.peripheral.ota.OTACommandServer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import javax.microedition.io.*;

/**
 * This application is the 'on Desktop' portion of the SendDataDemo. 
 * This host application collects sensor samples sent by the 'on SPOT'
 * portion running on neighboring SPOTs and just prints them out. 
 *   
 * @author: Vipul Gupta
 * modified: Ron Goldman
 */
public class SendDataDemoHostApplication {
    // Broadcast port on which we listen for sensor samples
    private static final int HOST_PORT = 65;
        
    private void run() throws Exception {
        RadiogramConnection rCon;
        Datagram dg;
        DateFormat fmt = DateFormat.getTimeInstance();
        //sideIdentifier of 1 is the LEFT sensor (car relative)
        //2 is the RIGHT
         
        try {
            // Open up a server-side broadcast radiogram connection
            // to listen for sensor readings being sent by different SPOTs
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + HOST_PORT);
            dg = rCon.newDatagram(rCon.getMaximumLength());
        } catch (Exception e) {
             System.err.println("setUp caught " + e.getMessage());
             throw e;
        }

        File myFile = new File("C:\\Users\\wei\\Documents\\MATLAB\\Matlab Code\\xbeedata.txt");

        // Main data collection loop
        while (true) {
            try {
                // Read sensor sample received over the radio
                rCon.receive(dg);
                String addr = dg.getAddress();  // read sender's Id
            //    long time = dg.readLong();      // read time of the reading
                

                int spot1signal = dg.readInt();        
                int spot2signal = dg.readInt(); 
                int spot3signal = dg.readInt();
                
                double converted1 = convertDBMS(spot1signal);
                double converted2 = convertDBMS(spot2signal);
                double converted3 = convertDBMS(spot3signal);
                writeXbeeToFile(converted1, converted2, converted3, myFile);
            
        
                System.out.println("router one dbm: " + spot1signal);
                System.out.println("router two dbm: " + spot2signal);
                System.out.println("router three dbm: " + spot3signal);
            } catch (Exception e) {
                System.err.println("Caught " + e +  " while reading sensor samples.");
                throw e;
            }
        }
    }
    
    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) throws Exception {
        // register the application's name with the OTA Command server & start OTA running
        OTACommandServer.start("SendDataDemo");

        SendDataDemoHostApplication app = new SendDataDemoHostApplication();
        app.run();
    }
    
        /* dummy function to convert dBms to feet
     * will write real conversion function when we have RSSI sample data
     * args: takes a dBm value 
     * return: the measurement in feet
     */
    public static double convertDBMS(double DBMSval)
    {
        double returnVal;
        
        returnVal = (DBMSval * (-1.84)) - 56.68;
        
        return returnVal;
    }
    
    public static void writeXbeeToFile(double one, double two, double three, File file)
    {
        DecimalFormat df = new DecimalFormat("00.000");
        try {
            
          BufferedWriter output = new BufferedWriter(new FileWriter(file));
          output.write(df.format(one) + ";" + df.format(two) + ";" + df.format(three));
          output.close();
          
        } catch ( IOException e ) {
           e.printStackTrace();
        }
        
    }

}
