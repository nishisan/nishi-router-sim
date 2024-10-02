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
package dev.nishisan.ip.router.examples;

import dev.nishisan.ip.nswitch.ne.NSwitch;
import dev.nishisan.ip.router.ne.NRouter;
import dev.nishisan.ip.router.ne.NRoutingEntry;
import java.util.Optional;

/**
 *
 * @author lucas
 */
public class SimpleSwitchExample {

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
         * Default GW
         */
        router1.addRouteEntry("10.0.2.0/24", "10.0.0.254");
        router1.addRouteEntry("0.0.0.0", "10.0.0.254");

        NRouter router2 = new NRouter("router-2");
        router2.addInterface("ge0/0/0/1", "10.0.0.254/24","LT:switch-1 eth-1");
        router2.addInterface("ge0/0/0/2", "10.0.2.1/24");
        router2.addInterface("ge0/0/0/3", "10.0.3.1/24");
        router2.addInterface("ge0/0/0/4", "10.0.4.1/24");

        /**
         * Exibe a tabela de roteamento dos roteadores
         */
        router1.printRoutingTable();
        router2.printRoutingTable();

        /**
         * Creates a VSwitch with 2 Interfaces
         */
        NSwitch vSwitch = new NSwitch("switch-1");
        /**
         * Adiciona 2 interfaces
         */
        vSwitch.addInterface("eth-1", "LT:router-1");
        vSwitch.addInterface("eth-2", "LT:router-2");
        vSwitch.addInterface("eth-3", "");
        
        vSwitch.sendMsg();
        
        /**
         * Creates a connection from router-1.ge0/0/0/1 to switch-1.eth-1
         */
        vSwitch.connect(router1.getInterfaceByName("ge0/0/0/1"), vSwitch.getInterfaceByName("eth-1"));
        /**
         * Creates a connection from router-2.ge0/0/0/1 to switch-1.eth-2
         */
        vSwitch.connect(router2.getInterfaceByName("ge0/0/0/1"), vSwitch.getInterfaceByName("eth-2"));

        
        vSwitch.printInterfaces();
        Optional<NRoutingEntry> route = router1.getNextHop("10.0.2.1");

        if (route.isPresent()) {
            /**
             * Prints the route used like: ip route get to 192.168.8.1
             */
            System.out.println("192.168.8.1 Reacheable:");
            route.get().print();
        }
    }
}
