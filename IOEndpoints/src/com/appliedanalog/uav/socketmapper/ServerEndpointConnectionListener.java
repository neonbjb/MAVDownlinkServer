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

/**
 * An interface for a client interested in knowing when special endpoints 
 * are disconnected / reconnected.
 * @author James
 */
public interface ServerEndpointConnectionListener {
    public void clientConnected();
    public void clientDisconnected();
}
