/*
    IOEndpoints - Library that provides a generic interface for interlinking I/O across different interfaces.
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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Implements an IO endpoint using a network socket.
 */
public class IOEndpointSocket implements IOEndpoint, Runnable{
    Socket socket;
    boolean running;
    Thread myThread;
    EndpointDataListener listener;
    
    /**
     * Initializes an IOEndpoint using a Socket.
     * @param sock An initialized networking socket.
     */
    public IOEndpointSocket(Socket sock){
        socket = sock;
        running = false;
    }
    
    /**
     * Late initialization offered to deriving classes so a server interface can be implemented.
     */
    protected IOEndpointSocket(){
        socket = null;
        running = false;
    }
    
    /**
     * Called by superclasses to bind this framework to a newly established socket
     * link.
     * @param sock TCP socket link.
     */
    protected void bind(Socket sock){
        if(running) return;
        socket = sock;
        socketEndpointStart();
    }
    
    /**
     * Returns whether or not the endpoint's socket connection is alive.
     * @return 
     */
    public boolean connected(){
        return socket != null && socket.isConnected();
    }
    
    @Override
    public void writeData(byte[] data, int len) {
        if(socket == null) return;
        try{
            OutputStream os = socket.getOutputStream();
            os.write(data, 0, len);
            os.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void run(){
        try{
            while(running){
                InputStream sockIn = socket.getInputStream();
                byte[] data = new byte[255];
                int len = sockIn.read(data);
                if(len < 0){
                    break;
                }
                if(listener != null){
                    listener.dataReceived(data, len);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        running = false;
        socket = null;
    }

    @Override
    public void setDataListener(EndpointDataListener edl) {
        listener = edl;
    }

    @Override
    public void start() {
        socketEndpointStart();
    }

    void socketEndpointStart(){
        if(running) return;
        running = true;
        myThread = new Thread(this);
        myThread.start();
    }

    @Override
    public void stop() {
        if(!running || socket == null) return;
        running = false;
        myThread.interrupt();
        try{
            myThread.join();
            socket.close();
            socket = null;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
