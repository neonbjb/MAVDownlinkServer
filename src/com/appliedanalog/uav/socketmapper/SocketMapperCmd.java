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

/**
 * Starts up a basic Server Socket to Server Socket IOEndpoint bridge from
 * ports 9999 to port 9998 when launched.
 * @author James
 */
public class SocketMapperCmd {
    IOEndpointServer externalServerEndpoint, mavServerEndpoint;
    EndpointConnector connector;    
    
    public static void main(String[] args){
        (new SocketMapperCmd()).run();
    }
    
    public void run(){
        try{
            externalServerEndpoint = new IOEndpointServer(9999);
            mavServerEndpoint = new IOEndpointServer(9998);
            connector = new EndpointConnector(externalServerEndpoint, mavServerEndpoint);
            
            externalServerEndpoint.setConnectionListener(new ServerEndpointConnectionListener(){
                @Override
                public void clientConnected() {
                    System.out.println("Android phone connected.");
                }

                @Override
                public void clientDisconnected() {
                    System.out.println("Android phone disconnected.");
                }
            });
            
            mavServerEndpoint.setConnectionListener(new ServerEndpointConnectionListener(){
                @Override
                public void clientConnected() {
                    System.out.println("MAV planner connected.");
                }

                @Override
                public void clientDisconnected() {
                    System.out.println("MAV planner disconnected.");
                }
            });
            
            mavServerEndpoint.start();
            externalServerEndpoint.start();
            while(true); //This application must be ctrl+c'ed
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
