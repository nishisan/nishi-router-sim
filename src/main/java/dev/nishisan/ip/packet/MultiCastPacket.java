/*
 * Copyright (C) 2024 lucas
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

import dev.nishisan.ip.base.NBaseInterface;
import dev.nishisan.ip.base.NMulticastGroup;
import inet.ipaddr.IPAddress;

/**
 *
 * @author lucas
 */
public abstract class MultiCastPacket<O> {

    private O payLoad;
    private NMulticastGroup group;
    private IPAddress srcAddress;
    private NBaseInterface srcIface;

    public MultiCastPacket(O payLoad, NMulticastGroup group, NBaseInterface srcIface) {
        this.payLoad = payLoad;
        this.group = group;
        this.srcAddress = srcIface.getAddress();
        this.srcIface = srcIface;
    }

    public O getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(O payLoad) {
        this.payLoad = payLoad;
    }

    public NMulticastGroup getGroup() {
        return group;
    }

    public void setGroup(NMulticastGroup group) {
        this.group = group;
    }

    public IPAddress getSrcAddress() {
        return srcAddress;
    }

    public void setSrcAddress(IPAddress srcAddress) {
        this.srcAddress = srcAddress;
    }

    public NBaseInterface getSrcIface() {
        return srcIface;
    }

    public void setSrcIface(NBaseInterface srcIface) {
        this.srcIface = srcIface;
    }

    
    
}
