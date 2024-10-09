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
import dev.nishisan.ip.packet.MultiCastPacket;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author lucas
 */
public class BroadCastDomain {

    /**
     * Mimics the Broadcast Domain
     */
    private final PublishSubject<BroadCastPacket> eventBus = PublishSubject.create();
    private final Map<String, MulticastGroup> mCastGroups = new ConcurrentHashMap<>();
    private Long age = System.currentTimeMillis();
    private String name;
    private final BaseNe ne;
    private final String uuid = UUID.randomUUID().toString();

    public BroadCastDomain(String name, BaseNe ne) {
        this.name = name;
        this.ne = ne;
    }

    public void sendBroadcastPacket(BroadCastPacket packet) {
        this.eventBus.onNext(packet);
    }

    public PublishSubject<BroadCastPacket> getEventBus() {
        return eventBus;
    }

    public Map<String, MulticastGroup> getmCastGroups() {
        return mCastGroups;
    }

    public Boolean hasMCastGroup(MulticastGroup g) {
        return this.mCastGroups.containsKey(g.getMcastGroup().toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MulticastGroup addInterfaceToMcastGroup(String group, BaseInterface iFace) {
        MulticastGroup target = null;
        if (!mCastGroups.containsKey(group)) {
            target = this.createMcastGroup(group);
            mCastGroups.put(group, target);
        } else {
            target = this.getMcastGroupByIp(group);
        }

        target.addSubscriberInterface(iFace);
        return target;
    }

    public MulticastGroup addInterfaceToMcastGroup(MulticastGroup target, BaseInterface iFace) {

        if (!mCastGroups.containsKey(target.getMcastGroup().toString())) {
            mCastGroups.put(target.getMcastGroup().toString(), target);
            System.out.println(this.ne.getName() + " - Mcast Group:[" + target.getMcastGroup() + "] Created In:" + this.getName());
        }

        target.addSubscriberInterface(iFace);
        return target;
    }

    private MulticastGroup createMcastGroup(String group) {
        MulticastGroup mcastGroup = new MulticastGroup(group);
        this.mCastGroups.put(group, mcastGroup);
        System.out.println(this.ne.getName() + " - Mcast Group:[" + group + "] Created In:" + this.getName());
        return mcastGroup;
    }

    public void sendMulticasPacket(MultiCastPacket packet, BaseInterface iFace) {
        /**
         * Check if Switch interface is joined in the group...if not join.
         */

        MulticastGroup group = iFace.joinMcastGroup(packet.getGroup());
        group.sendMulticasPacket(packet);
    }

    public MulticastGroup getMcastGroupByIp(String group) {
        return this.mCastGroups.get(group);
    }

    public MulticastGroup getMcastGroup(MulticastGroup group) {
        return this.mCastGroups.get(group.getMcastGroup().toString());
    }

    public Long getAge() {
        return age;
    }

    public String getUuid() {
        return uuid;
    }

}
