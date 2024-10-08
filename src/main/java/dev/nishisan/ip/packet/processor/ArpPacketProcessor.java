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
package dev.nishisan.ip.packet.processor;

import dev.nishisan.ip.base.BaseInterface;
import dev.nishisan.ip.packet.ArpPacket;
import dev.nishisan.ip.packet.BroadCastPacket;

/**
 *
 * @author lucas
 */
public class ArpPacketProcessor extends AbsPacketProcessor<ArpPacket> {

    public ArpPacketProcessor() {
        this.setName("ARP_PROCESSOR");
    }

    @Override
    public void processPacket(BroadCastPacket m, BaseInterface iFace) {
        if (m instanceof ArpPacket arp) {
//            System.out.println("Arp Packet");
            if (iFace.getAddress() != null) {
                if (iFace.getAddress().equals(arp.getRequestAddress())) {
                    arp.setiFace(iFace);
                    /**
                     * Encontrei a interface que eu queria
                     */
                    arp.reply(arp);
                    System.out.println("Pong::" + iFace.getAddress() + " " + arp.getRequestAddress());
                } else {
//                    System.out.println("Not Pong");
                }
            }
        } else {
//            System.out.println("Something Else");
        }
    }

}
