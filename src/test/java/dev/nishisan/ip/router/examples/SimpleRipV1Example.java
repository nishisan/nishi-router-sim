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
import dev.nishisan.ip.router.ne.NRouterInterface;

/**
 *
 * @author lucas
 */
public class SimpleRipV1Example {

    public static void main(String[] args) {
        NRouter router1 = new NRouter("router-1");
        NRouterInterface s = router1.addInterface("ge0/0/0/1", "192.168.0.1/24", "UPLINK");
        router1.addInterface("ge0/0/0/2", "192.168.1.1/24");
        router1.addInterface("ge0/0/0/3", "192.168.2.1/24");
        router1.addInterface("ge0/0/0/4", "192.168.3.1/24");

        NRouter router2 = new NRouter("router-2");
        router2.addInterface("ge0/0/0/1", "192.168.0.2/24", "LT:switch-1 eth-2");
        router2.addInterface("ge0/0/0/2", "10.0.2.1/24");
        router2.addInterface("ge0/0/0/3", "10.0.3.1/24");
        router2.addInterface("ge0/0/0/4", "10.0.4.1/24");

        NRouter router3 = new NRouter("router-3");
        router3.addInterface("ge0/0/0/1", "192.168.0.3/24", "LT:switch-1 eth-3");
        router3.addInterface("ge0/0/0/2", "172.30.1.1/24");
        router3.addInterface("ge0/0/0/3", "172.30.2.1/24");
        router3.addInterface("ge0/0/0/4", "172.30.3.1/24");

        NSwitch vSwitch1 = new NSwitch("switch-1");
        vSwitch1.addInterface("eth-1", "LT:router-1");
        vSwitch1.addInterface("eth-2", "LT:router-2");
        vSwitch1.addInterface("eth-3", "LT:router-2");
        vSwitch1.addInterface("eth-4", "");

        vSwitch1.connect(router1.getInterfaceByName("ge0/0/0/1"), vSwitch1.getInterfaceByName("eth-1"));
        vSwitch1.connect(router2.getInterfaceByName("ge0/0/0/1"), vSwitch1.getInterfaceByName("eth-2"));
        vSwitch1.connect(router3.getInterfaceByName("ge0/0/0/1"), vSwitch1.getInterfaceByName("eth-3"));

        vSwitch1.printInterfaces();

        /**
         * Send Announce on all interfaces on default Vlan
         */
        router1.sendRipV1Annouce(s).thenAccept(r -> {
            System.out.println("Rip Sent");
        });

        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimpleRipV1Example.class.getName()).log(Level.SEVERE, null, ex);
        }

        router1.printRoutingTable();
        router2.printRoutingTable();

        router3.printRoutingTable();
    }
}
