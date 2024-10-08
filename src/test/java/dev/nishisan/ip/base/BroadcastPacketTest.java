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

import dev.nishisan.ip.nswitch.ne.NSwitch;
import dev.nishisan.ip.packet.NPacket;

import dev.nishisan.ip.router.ne.NRouter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author lucas
 */
public class BroadcastPacketTest {

    /**
     * Monta uma topologia
     */
    @Test
    public void initRouterSwitch() {

        /**
         * Create Router-1.
         */
        NRouter router1 = new NRouter("router-1");
        router1.addInterface("ge0/0/0/1", "10.0.0.1/24", "LT:switch-1 eth-1");
        router1.addInterface("ge0/0/0/2", "192.168.1.1/24");
        router1.addInterface("ge0/0/0/3", "192.168.2.1/24");
        router1.addInterface("ge0/0/0/4", "192.168.3.1/24").setOperStatus(BaseInterface.NIfaceOperStatus.OPER_UP);

        /**
         * Router 1 - Default GW is router-2
         */
        router1.addStaticRouteEntry("0.0.0.0", "10.0.0.254");

        NRouter router2 = new NRouter("router-2");
        router2.addInterface("ge0/0/0/1", "10.0.0.254/24", "LT:switch-1 eth-1");
        router2.addInterface("ge0/0/0/2", "10.0.2.1/24");
        router2.addInterface("ge0/0/0/3", "10.0.3.1/24");
        router2.addInterface("ge0/0/0/4", "10.0.4.1/24");

        /**
         * Add multiple route to see if routing metric working
         */
        router2.addStaticRouteEntry("172.30.0.0/16", "10.0.0.2").setMetric(10);
        router2.addStaticRouteEntry("172.30.0.0/16", "10.0.0.1").setMetric(100);

        router2.addStaticRouteEntry("192.168.3.0/24", "10.0.0.1").setMetric(100);

        NRouter router3 = new NRouter("router-3");
        router3.addInterface("ge0/0/0/1", "10.0.0.2/24", "LT:switch-2 eth-2");
        router3.addInterface("ge0/0/0/2", "172.30.0.1/24");
        router3.addInterface("ge0/0/0/3", "172.30.1.1/24");
        router3.addInterface("ge0/0/0/4", "172.30.2.1/24").setOperStatus(BaseInterface.NIfaceOperStatus.OPER_UP);

        router3.addStaticRouteEntry("0.0.0.0", "10.0.0.254");

        /**
         * Cria 2 switches, e conecta um router em cada switch, e uma interface
         * eth3 entre os switches.
         */
        NSwitch vSwitch1 = new NSwitch("switch-1");
        vSwitch1.addInterface("eth-1", "LT:router-1");
        vSwitch1.addInterface("eth-2", "");
        vSwitch1.addInterface("eth-3", "LT:switch-2");

        NSwitch vSwitch2 = new NSwitch("switch-2");
        vSwitch2.addInterface("eth-1", "LT:router-2");
        vSwitch2.addInterface("eth-2", "LT:router-3");
        vSwitch2.addInterface("eth-3", "LT:switch-1");

        vSwitch1.connect(router1.getInterfaceByName("ge0/0/0/1"), vSwitch1.getInterfaceByName("eth-1")).setLatency(4).setJitter(5);
        vSwitch2.connect(router2.getInterfaceByName("ge0/0/0/1"), vSwitch2.getInterfaceByName("eth-1")).setLatency(4).setJitter(5);
        vSwitch2.connect(router3.getInterfaceByName("ge0/0/0/1"), vSwitch2.getInterfaceByName("eth-2")).setLatency(4).setJitter(5);

        vSwitch1.connect(vSwitch1.getInterfaceByName("eth-3"), vSwitch2.getInterfaceByName("eth-3"));

        vSwitch1.printInterfaces();
        vSwitch2.printInterfaces();

        /**
         * Exibe a tabela de roteamento dos roteadores
         */
        router1.printInterfaces();
        router1.printRoutingTable();
//        router2.printInterfaces();
//        router2.printRoutingTable();
        router3.printInterfaces();
        router3.printRoutingTable();

        System.out.println("------------------------------------------------------------------------------------------------------------------");
        /**
         * Build an IP Packet, source is 200.1.1.1, destination is 172.30.2.1
         */
        NPacket samplePacket = NPacket.buildRequest("192.168.3.1", "172.30.2.1");

        System.out.println("Source Packet is:" + samplePacket.getUuid());
        /**
         * This is the call back that will be called after the send it will be
         * like de reply of the ping..
         */
        samplePacket.onReply(5, (req, repl) -> {

            try {
                // Exemplo de validação com RTT
                Assertions.assertNotNull(req.getRtt(), "RTT não deve ser nulo");
                Assertions.assertTrue(req.getRtt() > 0, "O RTT deve ser maior que zero");

                System.out.println("RTT: " + req.getRtt());
            } catch (AssertionError e) {
                // Captura as falhas e exibe no console, pois você está dentro de uma chamada assíncrona
                System.err.println("Falha no teste: " + e.getMessage());
            }
        });
        /**
         * Force operstatus even with nothing connected..
         */
        router1.getInterfaceByName("ge0/0/0/4").setOperStatus(BaseInterface.NIfaceOperStatus.OPER_UP);

        /**
         * The packet must be inject on the router interface...
         */
        router1.getInterfaceByName("ge0/0/0/4").sendPacket(samplePacket);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(BroadcastPacketTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
