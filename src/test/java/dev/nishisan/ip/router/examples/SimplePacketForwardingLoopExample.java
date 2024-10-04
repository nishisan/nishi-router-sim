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
package dev.nishisan.ip.router.examples;

import dev.nishisan.ip.base.NPacket;
import dev.nishisan.ip.nswitch.ne.NSwitch;
import dev.nishisan.ip.router.ne.NRouter;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 03.10.2024
 */
public class SimplePacketForwardingLoopExample {

    public static void main(String[] args) {

        /**
         * Cria um router e adiciona algumas interfaces..
         */
        NRouter router1 = new NRouter("router-1");
        router1.addInterface("ge0/0/0/1", "10.0.0.1/24", "LT:switch-1 eth-1");
        router1.addInterface("ge0/0/0/2", "192.168.1.1/24");
        router1.addInterface("ge0/0/0/3", "192.168.2.1/24");
        router1.addInterface("ge0/0/0/4", "192.168.3.1/24");

        /**
         * Router 1 - Default GW is router-2
         */
        router1.addRouteEntry("0.0.0.0", "10.0.0.254");

        NRouter router2 = new NRouter("router-2");
        router2.addInterface("ge0/0/0/1", "10.0.0.254/24", "LT:switch-1 eth-1");
        router2.addInterface("ge0/0/0/2", "10.0.2.1/24");
        router2.addInterface("ge0/0/0/3", "10.0.3.1/24");
        router2.addInterface("ge0/0/0/4", "10.0.4.1/24");

        /**
         * Router 2 Default Gw is router-1
         */
        router2.addRouteEntry("0.0.0.0", "10.0.0.1");

        /**
         * Exibe a tabela de roteamento dos roteadores
         */
        router1.printRoutingTable();
        router2.printRoutingTable();

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
        vSwitch2.addInterface("eth-2", "");
        vSwitch2.addInterface("eth-3", "LT:switch-1");

        vSwitch1.connect(router1.getInterfaceByName("ge0/0/0/1"), vSwitch1.getInterfaceByName("eth-1"));

        vSwitch2.connect(router2.getInterfaceByName("ge0/0/0/1"), vSwitch2.getInterfaceByName("eth-1"));

        vSwitch1.connect(vSwitch1.getInterfaceByName("eth-3"), vSwitch2.getInterfaceByName("eth-3"));

        NPacket samplePacket = NPacket.buildRequest("200.1.1.1", "8.8.8.8");

        /**
         * Injects a packet on the router interface directly In this topology
         * will generate a Loop Cause router1 gw is router2 and router2 gw is
         * router1
         */
        router1.getInterfaceByName("ge0/0/0/4").sendPacket(samplePacket);

    }
}
