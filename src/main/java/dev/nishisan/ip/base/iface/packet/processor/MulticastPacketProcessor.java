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
 * along with scfIface program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package dev.nishisan.ip.base.iface.packet.processor;

import dev.nishisan.ip.base.BaseInterface;
import dev.nishisan.ip.base.MulticastGroup;
import dev.nishisan.ip.packet.MultiCastPacket;

/**
 *
 * @author lucas
 */
public class MulticastPacketProcessor implements IFacePacketProcessor<BaseInterface, MultiCastPacket> {

    @Override
    public void processPacket(BaseInterface scfIface, MultiCastPacket mCastPacket) {
        if (scfIface.getLink() != null) {
            /**
             * We have a link..
             */

            BaseInterface iFace = scfIface.getLink().getOtherIface(scfIface);
            if (!mCastPacket.walked(iFace)) {
                System.out.println("Mcast Packet Received on:" + scfIface.fullName());
                System.out.println("Found Link from:[" + scfIface.getLink().getSrc().fullName() + "] To:[" + scfIface.getLink().getDst().fullName() + "]");

                mCastPacket.notifyWalk(iFace);
                if (!iFace.isNRouterInterface()) {
                    /**
                     * Its is not a router!
                     */
                    if (mCastPacket.getGroup() != null) {

                        System.out.println(scfIface.getNe().getName() + "." + scfIface.getName() + " Packet Found a Switch: " + iFace.fullName());

                        //
                        // check if remote side has the mcast group
                        //
                        if (iFace.getBroadCastDomain().hasMCastGroup(mCastPacket.getGroup())) {
                            iFace.joinMcastGroup(mCastPacket.getGroup()).sendMcastPacket(mCastPacket);
                        } else {
                            System.out.println("Remote Not Joined: " + iFace.fullName() + " BD:" + iFace.getBroadCastDomain().getUuid());
                            
                        }

                    }

                } else {
                    /**
                     * Other Router :)
                     */

                    iFace.joinMcastGroup(mCastPacket.getGroup()).sendMcastPacket(mCastPacket);
                }
            } else {

                /**
                 * Check if another BroadCast
                 */
                if (iFace.getBroadCastDomain().hasMCastGroup(mCastPacket.getGroup())) {

                    /**
                     * Acho que concordamos que o multicast deve ser produzido
                     * no dominio de broadcast
                     */
                    MulticastGroup a = iFace.getBroadCastDomain().getMcastGroup(mCastPacket.getGroup());
                    if (!mCastPacket.walked(iFace.getBroadCastDomain())) {
                        mCastPacket.notifyWalk(iFace.getBroadCastDomain());
                        if (!a.equals(mCastPacket.getGroup())) {
                            //
                            // Propagates to the broadcast domain
                            //

                            a.sendMcastPacket(mCastPacket);
                        }
                    } else {
                    }
                }
            }
        }
    }

}
