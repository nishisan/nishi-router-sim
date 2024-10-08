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

import dev.nishisan.ip.router.ne.NRoutingEntry;
import inet.ipaddr.IPAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    private Integer originalTtl = 64;
    private NPacketType type = NPacketType.REQUEST;
    private NPacket source;
    private NPacket reply;
    private CompletableFuture<NPacket> replyFuture = new CompletableFuture<>();

    public enum NPacketType {
        REQUEST,
        REPLY
    }

    public void reply(NPacket replyPacket) {
        replyFuture.complete(replyPacket);
    }

    public void reply() {

        replyFuture.complete(this.source);
    }

    public void onReply(int timeoutInSeconds, Consumer<NPacket> replyHandler) {
        replyFuture.orTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .thenAccept(replyHandler)
                .exceptionally(ex -> {
                    System.out.println("Timeout ou erro: " + ex.getMessage());
                    return null;
                });
    }

    public void onReply(int timeoutInSeconds, BiConsumer<NPacket, NPacket> replyHandler) {
        replyFuture.orTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .thenAccept(reply -> {
                    replyHandler.accept(this, this.getReply());
                })
                .exceptionally(ex -> {
                    System.out.println("Timeout ou erro: " + ex.getMessage());
                    return null;
                });
    }

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

    public static NPacket buildRequest(String src, String dst, Integer ttl) {
        NPacket packet = new NPacket();
        packet.setSrc(NRoutingEntry.getIpAddress(src));
        packet.setDst(NRoutingEntry.getIpAddress(dst));
        packet.getTtl().set(ttl);
        packet.setOriginalTtl(ttl);
        packet.setType(NPacketType.REQUEST);
        return packet;
    }

    public static NPacket buildRequest(String src, String dst) {
        NPacket packet = new NPacket();
        packet.setSrc(NRoutingEntry.getIpAddress(src));
        packet.setDst(NRoutingEntry.getIpAddress(dst));
        packet.setType(NPacketType.REQUEST);
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
        /**
         * True when packet arrives on destionation but is confunsing...
         * Connections is stablished when round trips is valid... so this is not
         * real connected.. Must check in future
         */
        this.connected = connected;
    }

    public NPacket createReply() {
        NPacket n = new NPacket();
        n.src = this.dst;
        n.dst = this.src;
        n.ttl.set(this.originalTtl);
        n.setType(NPacketType.REPLY);
        n.source = this;
        return n;
    }

    public Integer getOriginalTtl() {
        return originalTtl;
    }

    public void setOriginalTtl(Integer originalTtl) {
        this.originalTtl = originalTtl;
    }

    public NPacketType getType() {
        return type;
    }

    public void setType(NPacketType type) {
        this.type = type;
    }

    public NPacket getSource() {
        return source;
    }

    public void setSource(NPacket s) {
        this.source = s;
    }

    public void setReply(NPacket s) {
        this.reply = s;
    }

    public NPacket getReply() {
        return this.reply;
    }

    public Long getRtt() {
        if (this.reply != null) {
            Long took = this.reply.endTimestamp - this.startTimestamp;
            return took;
        } else {
            return -1L;
        }
    }

}
