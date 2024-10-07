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

import dev.nishisan.ip.base.NBaseInterface;
import dev.nishisan.ip.packet.RipV1AnnouncePacket;
import dev.nishisan.ip.packet.BroadCastPacket;
import dev.nishisan.ip.router.ne.NRouter;

/**
 *
 * @author lucas
 */
public class RipV1PacketProcessor extends AbsPacketProcessor<RipV1AnnouncePacket> {
    
    @Override
    public void processPacket(BroadCastPacket m, NBaseInterface iFace) {
        if (m instanceof RipV1AnnouncePacket ripAnnouce) {
            //
            // Responde ele mesmo para testar..
            //
            ripAnnouce.reply(ripAnnouce);
            /**
             * Recebi um anuncio RIP, sou um router ?
             */
            if (iFace.getNe().getType().equals("ROUTER")) {
                /**
                 * Sou um router :)
                 */
                
                if (iFace.getAddress() != null) {
                    /**
                     * Vou verificar se minha interface está na mesma subnet do
                     * anuncio
                     */
                    
                    if (iFace.getAddress().prefixEquals(ripAnnouce.getSource())) {
                        /**
                         * Mesma rede, podemos adicionar as rotas :)
                         */
                        if (!iFace.getAddress().equals(ripAnnouce.getSource())) {
                            /**
                             * Não sou eu mesmo então vou me adicionar aqui
                             */
                            NRouter router = iFace.getNe().asNrouter();
                            System.out.println("Adding:" + ripAnnouce.getNetworks().size() + " Networks TO:[" + router.getName() + "]");
                            
                            ripAnnouce.getNetworks().forEach(net -> {
                                router.addRipRouteEntry(net.getDst(), ripAnnouce.getSource(), null, null);
                            });
                        }
                    }
                }
                
            }
            
        }
    }
    
}
