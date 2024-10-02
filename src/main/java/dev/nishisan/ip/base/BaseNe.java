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

import io.reactivex.rxjava3.subjects.PublishSubject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 01.10.2024
 */
public abstract class BaseNe<T extends BaseInterface> {

    private final String name;
    /**
     * Mimics the Broadcast
     */
    private final PublishSubject<ZeroLayerMsg> eventBus = PublishSubject.create();

    private Map<String, T> interfaces = Collections.synchronizedMap(new LinkedHashMap());

    public BaseNe(String name) {
        this.name = name;
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

    public PublishSubject<ZeroLayerMsg> getEventBus() {
        return eventBus;
    }

    public void pingBroadcast() {
        ZeroLayerMsg m = new ZeroLayerMsg();
        m.onReply(response -> {
            System.out.println("Resposta recebida para Msg [" + m.getUid() + "]: " + response);
        });
        this.eventBus.onNext(m);
    }

    public void pingBroadcast(ZeroLayerMsg m) {
        m.onReply(response -> {
            System.out.println("Resposta recebida para Msg [" + m.getUid() + "]: " + response);
        });
        this.eventBus.onNext(m);
    }

    /**
     * Gets the NE Type
     *
     * @return
     */
    public abstract String getType();

    public void printInterfaces() {
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Device.........:[" + this.getName() + "]");
        System.out.println("Interfaces.....:[" + this.interfaces.size() + "]");
        System.out.println("------------------------------------------------------------------------------------");
        String header = String.format("%-15s %-15s %-15s %-18s %-30s", "Interface", "Admin Status", "Oper Status", "MAC Address", "Description");
        System.out.println(header);
        System.out.println("------------------------------------------------------------------------------------");
        this.interfaces.forEach((k, v) -> {
            String row = String.format("%-15s %-15s %-15s %-18s %-30s", v.getName(), v.getAdminStatus(), v.getOperStatus(), v.getMacAddress(), v.getDescription());
            System.out.println(row);
        });
        System.out.println("------------------------------------------------------------------------------------");
    }
}
