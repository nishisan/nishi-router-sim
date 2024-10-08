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
package dev.nishisan.ip.base;

import dev.nishisan.ip.packet.MultiCastPacket;
import dev.nishisan.ip.router.ne.NRoutingEntry;
import inet.ipaddr.IPAddress;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author lucas
 */
public class MulticastGroup {

    private IPAddress mcastGroup; // 239.1.1.1
    private final PublishSubject<MultiCastPacket> eventBus = PublishSubject.create();
    /**
     * List of subscribers in the group
     */
    private Map<String, BaseInterface> subscribers = new ConcurrentHashMap<>();

    public void sendMulticasPacket(MultiCastPacket packet) {
        System.out.println("Sending Mcast Message to:" + subscribers.size());
        this.eventBus.onNext(packet);
    }

    public MulticastGroup(IPAddress mcastGroup) {
        this.mcastGroup = mcastGroup;
    }

    public MulticastGroup(String ipAddress) {
        this.mcastGroup = NRoutingEntry.getIpAddress(ipAddress);
    }

    public IPAddress getMcastGroup() {
        return mcastGroup;
    }

    public void setMcastGroup(IPAddress mcastGroup) {
        this.mcastGroup = mcastGroup;
    }

    public Map<String, BaseInterface> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Map<String, BaseInterface> subscribers) {
        this.subscribers = subscribers;
    }

    public void addSubscriberInterface(BaseInterface iFace) {
        if (!this.subscribers.containsKey(iFace.getUid())) {
            this.subscribers.put(iFace.getUid(), iFace);
        }
    }

    public PublishSubject<MultiCastPacket> getEventBus() {
        return eventBus;
    }

}
