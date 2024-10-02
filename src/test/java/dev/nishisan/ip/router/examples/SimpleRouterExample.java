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

import dev.nishisan.ip.router.ne.NRouter;
import dev.nishisan.ip.router.ne.NRoutingEntry;
import java.util.Optional;

/**
 * Creates a Router with 8 Interfaces
 *
 * @author lucas
 */
public class SimpleRouterExample {

    public static void main(String[] args) {
        /**
         * Create a Router with 8 Interfaces
         */
        NRouter router1 = new NRouter("router-1");
        router1.addInterface("ge0/0/0/1", "192.168.0.1/24", "UPLINK");
        router1.addInterface("ge0/0/0/2", "192.168.1.1/24");
        router1.addInterface("ge0/0/0/3", "192.168.2.1/24");
        router1.addInterface("ge0/0/0/4", "192.168.3.1/24");
        router1.addInterface("ge0/0/0/5", "192.168.4.1/24");
        router1.addInterface("ge0/0/0/6", "192.168.5.1/24");
        router1.addInterface("ge0/0/0/7", "192.168.6.1/24");
        router1.addInterface("ge0/0/0/8", "192.168.7.1/24");
        /**
         * Add a default Gateway
         */
        router1.addRouteEntry("0.0.0.0", "192.168.0.254", "192.168.0.1", router1.getInterfaceByName("ge0/0/0/1")); // Default GW
        router1.addRouteEntry("192.168.8.1/32", "192.168.7.1");                                                                // Rota mais especifica
        router1.printRoutingTable();
        router1.printInterfaces(); // <- show int desc
        /**
         * Check if we have a route
         */
        Optional<NRoutingEntry> route = router1.getNextHop("192.168.8.1");

        if (route.isPresent()) {
            /**
             * Prints the route used like: ip route get to 192.168.8.1
             */
            System.out.println("192.168.8.1 Reacheable:");
            route.get().print();
        }

    }
}
