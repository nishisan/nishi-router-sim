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
 * along with iFace program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package dev.nishisan.ip.base.iface.packet.processor;

import dev.nishisan.ip.base.BaseInterface;
import dev.nishisan.ip.packet.BroadCastPacket;
import java.util.Random;

/**
 *
 * @author lucas
 */
public class BroadCastPacketProcessor implements IFacePacketProcessor<BaseInterface, BroadCastPacket> {

    @Override
    public void processPacket(BaseInterface iFace, BroadCastPacket m) {
        /**
         * Check if oper status is up
         */
        if (iFace.getOperStatus().equals(BaseInterface.NIfaceOperStatus.OPER_UP)) {
            /**
             * Check if interface has link
             */
            if (iFace.getLink() != null) {
                if (!m.walked(iFace)) {
                    m.notifyWalk(iFace);
                    /**
                     * Apply latency if needed
                     */
                    if (iFace.getLink().getLatency() > 0) {
                        try {
                            //
                            // Mimics Latency
                            //

                            Random r = new Random();
                            Thread.sleep(iFace.getLink().getLatency());

                            //
                            // Jitter
                            //
                            if (iFace.getLink().getJitter() > 0) {
                                Thread.sleep(r.nextInt(iFace.getLink().getJitter()));
                            }
                        } catch (InterruptedException ex) {
                        }
                    }

                    StringBuilder msg = new StringBuilder();
                    msg.append("[").append(m.getClass().getSimpleName()).append("] - ");
                    msg.append("Msg Received:[" + m.getUid() + "] At:[" + iFace.getNe().getName() + "/" + iFace.getName() + "]");
                    msg.append(" Conected:[True]");
                    System.out.println(msg);
                    /**
                     * Processa o pacote na interface local.
                     */
                    iFace.getNe().processPacket(m, iFace);

                    //
                    // Obtem a ponta remota
                    //
                    BaseInterface o = iFace.getLink().getOtherIface(iFace);
                    //
                    // Como tem Link, esse método propaga para o proximo
                    //
                    o.getNe().sendBroadCastMessage(m);
                }
            }
        }
    }

}
