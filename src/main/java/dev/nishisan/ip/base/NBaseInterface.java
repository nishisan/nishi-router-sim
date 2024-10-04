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

import dev.nishisan.ip.packet.NPacket;
import dev.nishisan.ip.packet.OnWireMsg;
import inet.ipaddr.IPAddress;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 01.10.2024
 */
public class NBaseInterface {

    private String name;
    private String description = "";
    private String macAddress;
    private IPAddress address;
    private NIfaceOperStatus operStatus = NIfaceOperStatus.OPER_UP;
    private NIfaceAdminStatus adminStatus = NIfaceAdminStatus.ADMIN_UP;
    private final BaseNe ne;
    private NLink link;
    private final PublishSubject<OnWireMsg> eventBus;
    private String uid = UUID.randomUUID().toString();

    public enum NIfaceOperStatus {
        OPER_UP,
        OPER_DOWN
    }

    public enum NIfaceAdminStatus {
        ADMIN_UP,
        ADMIN_DOWN
    }

    public NBaseInterface(String name, BaseNe ne) {
        this.name = name;
        this.eventBus = ne.getEventBus();
        this.macAddress = NBaseInterface.generateMacAddress();
        this.ne = ne;
        /**
         *
         */
        this.eventBus.subscribe(m -> {
            /**
             * Check if oper status is up
             */
            if (this.operStatus.equals(NIfaceOperStatus.OPER_UP)) {
                /**
                 * Check if interface has link
                 */
                if (this.link != null) {
                    if (!m.walked(this)) {
//                        System.out.println("B Packet Start");
                        m.notifyWalk(this);
                        /**
                         * Apply latency if needed
                         */
                        if (this.link.getLatency() > 0) {
                            try {
                                //
                                // Mimics Latency
                                //

                                Random r = new Random();
                                Thread.sleep(this.link.getLatency());

                                //
                                // Jitter
                                //
                                if (this.link.getJitter() > 0) {
                                    Thread.sleep(r.nextInt(this.link.getJitter()));
                                }
                            } catch (InterruptedException ex) {
                            }
                        }

                        StringBuilder msg = new StringBuilder();
                        msg.append("[").append(m.getClass().getSimpleName()).append("] - ");
                        msg.append("Msg Received:[" + m.getUid() + "] At:[" + this.ne.getName() + "/" + this.getName() + "]");
                        msg.append(" Conected:[True]");
                        System.out.println(msg);
                        /**
                         * Processa o pacote na interface local.
                         */
                        this.ne.processPacket(m, this);

                        //
                        // Obtem a ponta remota
                        //
                        NBaseInterface o = this.link.getOtherIface(this);
                        //
                        // Como tem Link, esse método propaga para o proximo
                        //
                        o.getNe().sendOnWireMsg(m);

                    }
                }
            }

        });
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

    public void sendPacket(NPacket p) {
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
         * Encaminha o pacote :)
         */
        this.ne.forwardPacket(p);
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

    public void setOperStatus(NIfaceOperStatus operStatus) {
        this.operStatus = operStatus;
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

    public void setLink(NLink link) {
        this.link = link;
    }

    public NLink getLink() {
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
        final NBaseInterface other = (NBaseInterface) obj;
        return Objects.equals(this.uid, other.uid);
    }

    public BaseNe getNe() {
        return ne;
    }
    
    

}
