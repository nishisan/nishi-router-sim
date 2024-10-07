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
package dev.nishisan.ip.packet.payload;

import dev.nishisan.ip.base.NBaseInterface;
import dev.nishisan.ip.router.ne.NRoutingEntry;
import inet.ipaddr.IPAddress;
import java.util.List;

/**
 *
 * @author lucas
 */
public class RipV2Payload {

    private List<NRoutingEntry> networks;
    private String sourceSystemUid;
    private IPAddress srcAddress;
    private NBaseInterface srcIface;

    public RipV2Payload(List<NRoutingEntry> networks, IPAddress srcAddress, NBaseInterface srcIface) {
        this.networks = networks;
        this.srcAddress = srcAddress;
        this.srcIface = srcIface;
        this.sourceSystemUid = srcIface.getNe().getUuid();
    }

    public List<NRoutingEntry> getNetworks() {
        return networks;
    }

    public void setNetworks(List<NRoutingEntry> networks) {
        this.networks = networks;
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
