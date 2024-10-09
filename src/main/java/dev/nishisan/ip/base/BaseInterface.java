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

import dev.nishisan.ip.base.iface.packet.processor.BroadCastPacketProcessor;
import dev.nishisan.ip.base.iface.packet.processor.MulticastPacketProcessor;
import dev.nishisan.ip.nswitch.ne.NSwitchInterface;
import dev.nishisan.ip.packet.NPacket;
import dev.nishisan.ip.packet.BroadCastPacket;
import dev.nishisan.ip.packet.MultiCastPacket;
import dev.nishisan.ip.router.ne.NRouterInterface;
import inet.ipaddr.IPAddress;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 01.10.2024
 */
public class BaseInterface {

    private String name;
    private String description = "";
    private String macAddress;
    private IPAddress address;
    private NIfaceOperStatus operStatus = NIfaceOperStatus.OPER_UP;
    private NIfaceAdminStatus adminStatus = NIfaceAdminStatus.ADMIN_UP;
    private final BaseNe ne;
    private Link link;
    private final PublishSubject<BroadCastPacket> eventBus;
    private String uid = UUID.randomUUID().toString();
    private BaseIfType ifType = BaseIfType.ETHERNET_CSMACD;
    private BroadCastDomain broadCastDomain;
    private final BroadCastPacketProcessor broadCastPacketProcessor = new BroadCastPacketProcessor();
    private final MulticastPacketProcessor multicastPacketProcessor = new MulticastPacketProcessor();
    private Disposable subscription;

    private Map<String, MultiCastSubscriptionEntry> joinedGroups = new ConcurrentHashMap<>();

    public enum NIfaceOperStatus {
        OPER_UP,
        OPER_DOWN
    }

    public enum NIfaceAdminStatus {
        ADMIN_UP,
        ADMIN_DOWN
    }

    public BaseInterface(String name, BaseNe ne, BroadCastDomain broadCastDomain) {
        this.name = name;
        this.eventBus = ne.getEventBus();
        this.macAddress = BaseInterface.generateMacAddress();
        this.ne = ne;
        this.broadCastDomain = broadCastDomain;
        this.broadCastDomain.addIfaceToDomain(this);
        /**
         * This is the Default Broadcast Domain
         */
        this.subscription = this.eventBus.subscribe(this::processBroadCast);
    }

    /**
     * Process a Broadcast packet
     *
     * @param m
     */
    private void processBroadCast(BroadCastPacket m) {
        this.broadCastPacketProcessor.processPacket(this, m);
    }

    /**
     * Process the multicast packet
     */
    private void processMcastPacket(MultiCastPacket mCastPacket) {
        this.multicastPacketProcessor.processPacket(this, mCastPacket);
    }

    public static String generateMacAddress() {
        Random random = new Random();
        byte[] macAddr = new byte[6];
        random.nextBytes(macAddr);
        macAddr[0] = (byte) (macAddr[0] & (byte) 254);
        StringBuilder macAddressBuilder = new StringBuilder();
        for (int i = 0; i < macAddr.length; i++) {
            macAddressBuilder.append(String.format("%02X", macAddr[i]));
            if (i < macAddr.length - 1) {
                macAddressBuilder.append(":");
            }
        }
        return macAddressBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NPacket sendPacket(NPacket p) {
        /**
         * @todo, check if source address is direct connected to the
         * interface...
         */
        p.startForwarding();
        if (this.ne.getType().equals("ROUTER")) {
            //
            // If its a router packet should be forwaded
            //
        } else if (this.ne.getType().equals("SWITCH")) {
            //
            // It its a switch we need to send it to the broadcast or destination interface
            //
        }

        /**
         * Only Forwards if OperStatus is UP
         */
        if (this.isOperStatusUp()) {
            p.startForwarding();
            this.ne.forwardPacket(p);
        }
        return p;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public IPAddress getAddress() {
        return address;
    }

    public void setAddress(IPAddress address) {
        this.address = address;
    }

    public NIfaceOperStatus getOperStatus() {
        return operStatus;
    }

    public Boolean isOperStatusUp() {
        return NIfaceOperStatus.OPER_UP.equals(this.getOperStatus());
    }

    public BaseInterface setOperStatus(NIfaceOperStatus operStatus) {

        if (this.operStatus != operStatus) {
            //
            // Status Changing
            //

            this.operStatus = operStatus;
            System.out.println(" " + this.ne.getName() + "." + this.getName() + " Changed Status To:" + this.operStatus);
            this.ne.onIFaceOperStatusChanged(this);
        }

        return this;
    }

    public NIfaceAdminStatus getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(NIfaceAdminStatus adminStatus) {
        this.adminStatus = adminStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return this.link;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.uid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseInterface other = (BaseInterface) obj;
        return Objects.equals(this.uid, other.uid);
    }

    public BaseNe getNe() {
        return ne;
    }

    public MulticastGroup joinMcastGroup(String mcastGroupAddress) {
        /**
         * it will look for the mcast group in the current broadcast domain
         */

        if (!this.getBroadCastDomain().getmCastGroups().containsKey(mcastGroupAddress)) {
            MulticastGroup result = this.getBroadCastDomain().addInterfaceToMcastGroup(mcastGroupAddress, this);
            Disposable subscription = result.getEventBus().subscribe(this::processMcastPacket);
            this.joinedGroups.put(result.getMcastGroup().toString(), new MultiCastSubscriptionEntry(subscription, result));
            System.out.println(this.fullName() + " Joined Mcast Group[S]:" + mcastGroupAddress + " In " + this.getBroadCastDomain().getUuid() + " NE: " + this.getNe().getName());
            return result;
        } else {
            return this.getBroadCastDomain().getMcastGroupByIp(mcastGroupAddress);
        }

    }

    public MulticastGroup joinMcastGroup(MulticastGroup group) {
        /**
         * it will look for the mcast group in the current broadcast domain
         */
        if (!this.getBroadCastDomain().getmCastGroups().containsKey(group.getMcastGroup().toString())) {
            MulticastGroup result = this.getBroadCastDomain().addInterfaceToMcastGroup(group, this);
            Disposable subscription = result.getEventBus().subscribe(this::processMcastPacket);
            this.joinedGroups.put(result.getMcastGroup().toString(), new MultiCastSubscriptionEntry(subscription, result));
            System.out.println(this.fullName() + " Joined Mcast Group[G]:" + group.getMcastGroup() + " In " + this.getBroadCastDomain().getUuid() + " NE: " + this.getNe().getName());
            this.getBroadCastDomain().printConnectedIfaces();
            System.out.println("-----------");
            this.link.getOtherIface(this).getBroadCastDomain().printConnectedIfaces();
            return result;
        } else {
            return this.getBroadCastDomain().getmCastGroups().get(group.getMcastGroup().toString());
        }
    }

    public BroadCastDomain getBroadCastDomain() {
        return broadCastDomain;
    }

    public BaseIfType getIfType() {
        return ifType;
    }

    public void setIfType(BaseIfType ifType) {
        this.ifType = ifType;
    }

    public String fullName() {
        return this.getNe().getName() + "." + this.getName();
    }

    public enum BaseIfType {
        ETHERNET_CSMACD(6, "Ethernet (IEEE 802.3)"),
        FAST_ETHERNET(62, "Fast Ethernet (100 Mbps)"),
        GIGABIT_ETHERNET(117, "Gigabit Ethernet (1000 Mbps)"),
        FRAME_RELAY(32, "Frame Relay"),
        ATM(37, "Asynchronous Transfer Mode (ATM)"),
        PPP(23, "Point-to-Point Protocol (PPP)"),
        HDLC(118, "High-Level Data Link Control (HDLC)"),
        SOFTWARE_LOOPBACK(24, "Software Loopback");

        private final int code;
        private final String description;

        BaseIfType(int code,
                String description
        ) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return name() + " (" + code + "): " + description;
        }
    }

    public Boolean isNRouterInterface() {
        if (this instanceof NRouterInterface) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isNSwitchInterface() {
        if (this instanceof NSwitchInterface) {
            return true;
        } else {
            return false;
        }
    }

    public NRouterInterface asNRouterInterface() {
        return (NRouterInterface) this;
    }

    public void setBroadCastDomain(BroadCastDomain broadCastDomain) {
        this.broadCastDomain = broadCastDomain;
    }

}
