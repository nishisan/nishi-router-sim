/*
 * Copyright (C) 2024 Lucas Nishimura <lucas.nishimura at gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package dev.nishisan.ip.packet;

import dev.nishisan.ip.base.BaseInterface;
import dev.nishisan.ip.packet.BroadCastPacket;
import dev.nishisan.ip.router.ne.NRoutingEntry;
import inet.ipaddr.IPAddress;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 02.10.2024
 */
public class ArpPacket extends BroadCastPacket<ArpPacket> {

    private final IPAddress requestAddress;

    private BaseInterface iFace;

    public ArpPacket(String requestAddress) {
        this.requestAddress = NRoutingEntry.getIpAddress(requestAddress);
    }

    public ArpPacket(IPAddress requestAddress) {
        this.requestAddress = requestAddress;
    }

    public IPAddress getRequestAddress() {
        return requestAddress;
    }

    public BaseInterface getiFace() {
        return iFace;
    }

    public void setiFace(BaseInterface iFace) {
        this.iFace = iFace;
    }

}
