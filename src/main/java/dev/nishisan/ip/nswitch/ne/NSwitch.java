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
package dev.nishisan.ip.nswitch.ne;

import dev.nishisan.ip.base.BaseInterface;
import dev.nishisan.ip.base.BaseNe;
import dev.nishisan.ip.base.BroadCastDomain;
import dev.nishisan.ip.base.Link;
import dev.nishisan.ip.packet.NPacket;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 01.10.2024
 */
public class NSwitch extends BaseNe<NSwitchInterface> {

    private Map<String, Link> links = Collections.synchronizedMap(new LinkedHashMap());

    public NSwitch(String name) {
        super(name);
    }

    public NSwitchInterface addInterface(String name, String description) {
        NSwitchInterface iFace = new NSwitchInterface(name, description, this, this.getDefaultBroadcastDomain());
        this.getInterfaces().put(name, iFace);
        if (iFace.getLink() == null) {
            iFace.setOperStatus(BaseInterface.NIfaceOperStatus.OPER_DOWN);
        } else {
            iFace.setOperStatus(BaseInterface.NIfaceOperStatus.OPER_UP);
        }
        return iFace;
    }

    public NSwitchInterface addInterface(String name) {
        NSwitchInterface iFace = new NSwitchInterface(name, this, this.getDefaultBroadcastDomain());
        this.getInterfaces().put(name, iFace);
        return iFace;
    }

    public Link connect(BaseInterface src, BaseInterface dst) {
        Link link = new Link(src, dst);
        System.out.println("Connecting...");
        this.links.put(src.getMacAddress() + "." + dst.getMacAddress(), link);
        if (src.getAdminStatus().equals(BaseInterface.NIfaceAdminStatus.ADMIN_UP)) {
            if (dst.getAdminStatus().equals(BaseInterface.NIfaceAdminStatus.ADMIN_UP)) {
                src.setOperStatus(BaseInterface.NIfaceOperStatus.OPER_UP);
                dst.setOperStatus(BaseInterface.NIfaceOperStatus.OPER_UP);
            }
        }
        System.out.println("Link Created:" + link.getDst().getOperStatus() + " -> " + link.getSrc().getOperStatus());
        return link;
    }

    @Override
    public String getType() {
        return "SWITCH";
    }

    @Override
    public void forwardPacket(NPacket packet) {

    }

    @Override
    public void printInterfaces() {
        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.println("Device.........:[" + this.getName() + "]");
        System.out.println("Interfaces.....:[" + this.getInterfaces().size() + "]");
        System.out.println("-----------------------------------------------------------------------------------------------------");
        String header = String.format("%-15s %-15s %-15s %-18s %-30s", "Interface", "Admin Status", "Oper Status", "MAC Address", "Description");
        System.out.println(header);
        System.out.println("-----------------------------------------------------------------------------------------------------");
        this.getInterfaces().forEach((k, v) -> {
            String row = String.format("%-15s %-15s %-15s %-18s %-30s", v.getName(), v.getAdminStatus(), v.getOperStatus(), v.getMacAddress(), v.getDescription());
            System.out.println(row);
        });
        System.out.println("-----------------------------------------------------------------------------------------------------");
    }

    @Override
    public void registerProcessors() {

    }

    @Override
    public void tick() {

    }

}
