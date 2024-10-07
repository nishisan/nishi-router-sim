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

import dev.nishisan.ip.packet.BroadCastPacket;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author lucas
 */
public class NBroadCastDomain {

    /**
     * Mimics the Broadcast Domain
     */
    private final PublishSubject<BroadCastPacket> eventBus = PublishSubject.create();
    private final Map<String, NMulticastGroup> mCastGroups = new ConcurrentHashMap<>();
    private Long age = System.currentTimeMillis();
    private String name;

    public NBroadCastDomain(String name) {
        this.name = name;
    }

    public void sendBroadcastPacket(BroadCastPacket packet) {
        this.eventBus.onNext(packet);
    }

    public PublishSubject<BroadCastPacket> getEventBus() {
        return eventBus;
    }

    public Map<String, NMulticastGroup> getmCastGroups() {
        return mCastGroups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NMulticastGroup addInterfaceToMcastGroup(String group, NBaseInterface iFace) {
        NMulticastGroup target = null;
        if (!mCastGroups.containsKey(group)) {
            target = this.createMcastGroup(group);
            mCastGroups.put(group, target);
        } else {
            target = this.getMcastGroupByIp(group);
        }

        target.addSubscriberInterface(iFace);
        return target;
    }

    public NMulticastGroup addInterfaceToMcastGroup(NMulticastGroup target, NBaseInterface iFace) {

        if (!mCastGroups.containsKey(target.getMcastGroup().toString())) {
            target = mCastGroups.put(target.getMcastGroup().toString(), target);
        }

        target.addSubscriberInterface(iFace);
        return target;
    }

    private NMulticastGroup createMcastGroup(String group) {
        NMulticastGroup mcastGroup = new NMulticastGroup(group);
        this.mCastGroups.put(group, mcastGroup);
        System.out.println("Mcast Group:[" + group + "] Created In:" + this.getName());
        return mcastGroup;
    }

    public NMulticastGroup getMcastGroupByIp(String group) {
        return this.mCastGroups.get(group);
    }

    public Long getAge() {
        return age;
    }

}
