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

import dev.nishisan.ip.packet.ArpRequest;
import dev.nishisan.ip.packet.NPacket;
import dev.nishisan.ip.packet.OnWireMsg;
import dev.nishisan.ip.packet.processor.IPacketProcessor;
import dev.nishisan.ip.router.ne.NRouter;
import inet.ipaddr.IPAddress;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 01.10.2024
 */
public abstract class BaseNe<T extends NBaseInterface> {

    private final String name;
    private NBroadCastDomain defaultBroadcastDomain = new NBroadCastDomain();

    private Map<String, T> interfaces = Collections.synchronizedMap(new LinkedHashMap());
    private Map<String, IPacketProcessor> processors = Collections.synchronizedMap(new LinkedHashMap());

    public BaseNe(String name) {
        this.name = name;
        this.registerProcessors();
    }

    public Map<String, T> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Map<String, T> interfaces) {
        this.interfaces = interfaces;
    }

    public T getInterfaceByName(String name) {
        if (name == null) {
            return null;
        }
        return this.getInterfaces().get(name);
    }

    public String getName() {
        return name;
    }

    public PublishSubject<OnWireMsg> getEventBus() {
        return this.defaultBroadcastDomain.getEventBus();
    }

    public void pingBroadcast() {
        OnWireMsg m = new OnWireMsg();
        m.onReply(response -> {
            System.out.println("Resposta recebida para Msg [" + m.getUid() + "]: " + response);
        });
        this.defaultBroadcastDomain.getEventBus();
    }

    public CompletableFuture<NBaseInterface> sendArpRequest(String ip) {
        ArpRequest r = new ArpRequest(ip);
        CompletableFuture<NBaseInterface> future = new CompletableFuture<>();
        r.onReply(o -> {
            future.complete(o.getiFace());
        });
        this.sendOnWireMsg(r);
        return future;
    }

    public CompletableFuture<NBaseInterface> sendArpRequest(IPAddress ip) {
        ArpRequest r = new ArpRequest(ip);
        CompletableFuture<NBaseInterface> future = new CompletableFuture<>();

        r.onReply(o -> {
            future.complete(o.getiFace());
        });
        this.sendOnWireMsg(r);
        return future;
    }

    public void pingBroadcast(OnWireMsg m) {
        m.onReply(response -> {
            System.out.println("Resposta recebida para Msg [" + m.getUid() + "]: " + response);
        });

    }

    protected void sendOnWireMsg(OnWireMsg m) {
        this.defaultBroadcastDomain.getEventBus().onNext(m);
    }

    /**
     * Gets the NE Type
     *
     * @return
     */
    public abstract String getType();

    public abstract void printInterfaces();

    public abstract void forwardPacket(NPacket packet);

    public void processPacket(OnWireMsg m, NBaseInterface iFace) {
        /**
         * Chama a implementação dos processadores registrados
         */
        this.getProcessors().forEach((k, p) -> {
            p.processPacket(m, iFace);
        });

    }

    public abstract void registerProcessors();

    public void addProcessor(IPacketProcessor processor) {

        this.processors.put(name, processor);
    }

    public Map<String, IPacketProcessor> getProcessors() {
        return processors;
    }

    public NRouter asNrouter() {
        return (NRouter) this;
    }
}
