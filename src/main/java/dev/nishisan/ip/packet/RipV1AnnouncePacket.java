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

import dev.nishisan.ip.router.ne.NRoutingEntry;
import inet.ipaddr.IPAddress;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lucas
 */
public class RipV1AnnouncePacket extends BroadCastPacket<RipV1AnnouncePacket> {

    private IPAddress source;
    private List<NRoutingEntry> networks = new ArrayList<>();
    private Integer hopCount = 0;

    public Integer getHopCount() {
        return hopCount;
    }

    public void setHopCount(Integer hopCount) {
        this.hopCount = hopCount;
    }

    public IPAddress getSource() {
        return source;
    }

    public void setSource(IPAddress source) {
        this.source = source;
    }

    public List<NRoutingEntry> getNetworks() {
        return networks;
    }

    public void setNetworks(List<NRoutingEntry> networks) {
        this.networks = networks;
    }

}
