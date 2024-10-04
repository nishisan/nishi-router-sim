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
package dev.nishisan.ip.base;

import dev.nishisan.ip.router.ne.NRoutingEntry;
import inet.ipaddr.IPAddress;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 03.10.2024
 */
public class NPacket {

    private AtomicInteger ttl = new AtomicInteger(64);
    private String uuid = UUID.randomUUID().toString();
    private IPAddress src;
    private IPAddress dst;
    private Integer timeout = 1;
    private Boolean connected = false;
    private Long startTimestamp = 0L;
    private Long endTimestamp = 0L;

    public void startForwarding() {
        if (this.startTimestamp == 0L) {
            this.startTimestamp = System.currentTimeMillis();
        }
    }

    public void stopForwarding() {
        this.endTimestamp = System.currentTimeMillis();
    }

    public Long forwardTimeInMs() {
        return this.endTimestamp - this.startTimestamp;
    }

    public AtomicInteger getTtl() {
        return ttl;
    }

    public void setTtl(AtomicInteger ttl) {
        this.ttl = ttl;
    }

    public IPAddress getSrc() {
        return src;
    }

    public void setSrc(IPAddress src) {
        this.src = src;
    }

    public IPAddress getDst() {
        return dst;
    }

    public void setDst(IPAddress dst) {
        this.dst = dst;
    }

    public String getUuid() {
        return uuid;
    }

    public static NPacket build(String src, String dst, Integer ttl) {
        NPacket packet = new NPacket();
        packet.setSrc(NRoutingEntry.getIpAddress(src));
        packet.setDst(NRoutingEntry.getIpAddress(dst));
        packet.getTtl().set(ttl);
        return packet;
    }

    public static NPacket build(String src, String dst) {
        NPacket packet = new NPacket();
        packet.setSrc(NRoutingEntry.getIpAddress(src));
        packet.setDst(NRoutingEntry.getIpAddress(dst));
        return packet;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

}
