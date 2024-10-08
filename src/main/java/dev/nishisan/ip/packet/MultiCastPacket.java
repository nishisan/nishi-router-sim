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

import dev.nishisan.ip.base.BaseInterface;
import dev.nishisan.ip.base.MulticastGroup;
import inet.ipaddr.IPAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author lucas
 */
public abstract class MultiCastPacket<O> {

    private O payLoad;
    private MulticastGroup group;
    private IPAddress srcAddress;
    private BaseInterface srcIface;
    private Map<String, BaseInterface> walked = new ConcurrentHashMap<>();

    public MultiCastPacket(O payLoad, MulticastGroup group, BaseInterface srcIface) {
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

    public MulticastGroup getGroup() {
        return group;
    }

    public void setGroup(MulticastGroup group) {
        this.group = group;
    }

    public IPAddress getSrcAddress() {
        return srcAddress;
    }

    public void setSrcAddress(IPAddress srcAddress) {
        this.srcAddress = srcAddress;
    }

    public BaseInterface getSrcIface() {
        return srcIface;
    }

    public void setSrcIface(BaseInterface srcIface) {
        this.srcIface = srcIface;
    }

    /**
     * @return the walked
     */
    public Map<String, BaseInterface> getWalked() {
        return walked;
    }

    /**
     * @param walked the walked to set
     */
    public void setWalked(Map<String, BaseInterface> walked) {
        this.walked = walked;
    }

    public void notifyWalk(BaseInterface i) {
        this.walked.put(i.getUid(), i);
    }

    public Boolean walked(BaseInterface iFace) {
        return this.walked.containsKey(iFace.getUid());
    }
}
