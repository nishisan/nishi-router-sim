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

import inet.ipaddr.IPAddress;
import io.reactivex.rxjava3.subjects.PublishSubject;
import java.util.Random;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 01.10.2024
 */
public class BaseInterface {

    private String name;
    private String description;
    private String macAddress;
    private IPAddress address;
    private NIfaceOperStatus operStatus = NIfaceOperStatus.OPER_UP;
    private NIfaceAdminStatus adminStatus = NIfaceAdminStatus.ADMIN_UP;
    private NLink link;
    private final PublishSubject<ZeroLayerMsg> eventBus;

    public enum NIfaceOperStatus {
        OPER_UP,
        OPER_DOWN
    }

    public enum NIfaceAdminStatus {
        ADMIN_UP,
        ADMIN_DOWN
    }

    public BaseInterface(String name, PublishSubject<ZeroLayerMsg> eventBus) {
        this.name = name;
        this.eventBus = eventBus;
        this.macAddress = BaseInterface.generateMacAddress();

        this.eventBus.subscribe(m -> {
            System.out.println("Msg Received:[" + m.getUid() + "]");

            m.reply(this.name + " respondeu à mensagem com UID: " + m.getUid());
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
}
