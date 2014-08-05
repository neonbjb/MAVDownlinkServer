/*
    MavServerMapper - Links a MAV Mission Client with an online MAV autopilot data source.
    Copyright (C) 2014 James Betker, Applied Analog LLC

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.appliedanalog.uav.socketmapper;

import java.io.IOException;
import java.net.UnknownHostException;
import jssc.SerialPort;

/**
 * Implements an IORoutingInterface by routing data coming from a local serial
 * port through the LocalMappingServer.
 * @author James
 */
public class IOEndpointWindowsSerial implements IOEndpoint, Runnable {
    private SerialPort serialPort;
    EndpointDataListener listener = null;
    
    private static final String COM_PORT = "COM10";
    private static final int BAUD_RATE = SerialPort.BAUDRATE_115200;
    
    Thread thread = null;
    boolean running = false;
    
    IOEndpointWindowsSerial() {
    }
    
    public void openConnection() throws UnknownHostException, IOException {
        serialPort = new SerialPort(COM_PORT);
        try{
            if(!serialPort.openPort()){
                throw new IOException("Unable to open serial port!");
            }
            serialPort.setParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        }catch(Exception e){
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void start(){
        if(running) return;
        try{
            openConnection();
        }catch(Exception e){
            e.printStackTrace();
        }
        running = true;
        thread = new Thread(this);
        thread.start();
    }
    
    @Override
    public void stop(){
        if(thread == null) return;
        running = false;
        thread.interrupt();
        thread = null;
    }
    
    public void run(){
        while(running){
            try{
                //Read data from driver. This call will return upto readData.length bytes.
                //If no data is received it will timeout after 200ms (as set by parameter 2)
                byte[] buf = serialPort.readBytes();
                if(buf == null){
                    continue;
                }
                if(listener == null){
                    System.out.println("Received " + buf.length + " bytes in the serial port but no listener.");
                    continue;
                }
                listener.dataReceived(buf, buf.length);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() throws IOException {
        try{
            serialPort.closePort();
        }catch(Exception e){
            throw new IOException(e.getMessage());
        }
    }
    
    @Override
    public void writeData(byte[] data, int len) {
        try{
            byte[] resized = data;
            if(data.length != len){
                resized = new byte[len];
                System.arraycopy(data, 0, resized, 0, len);
            }
            serialPort.writeBytes(resized);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setDataListener(EndpointDataListener edl) {
        listener = edl;
    }
}