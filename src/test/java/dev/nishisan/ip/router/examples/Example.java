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
 *
 * @author lucas
 */
public class Example {

    public static void main(String[] args) {

        NRouter router1 = new NRouter("router-1");
        router1.addInterface("ge0/0/0/1", "192.168.0.1/24", "UPLINK");
        router1.addInterface("ge0/0/0/2", "192.168.1.1/24");
        router1.addInterface("ge0/0/0/3", "192.168.2.1/24");
        router1.addInterface("ge0/0/0/4", "192.168.3.1/24");
  
        /**
         * Add a default Gateway
         */
        router1.addStaticRouteEntry("0.0.0.0", "192.168.0.254", "192.168.0.1", router1.getInterfaceByName("ge0/0/0/1"));     // Default GW
        router1.addStaticRouteEntry("192.168.8.1/32", "192.168.2.1").setMetric(10);                                                                // Rota mais especifica
        router1.addStaticRouteEntry("192.168.8.1/32", "192.168.3.1").setMetric(5);

        router1.printInterfaces();

        router1.printRoutingTable();

        Optional<NRoutingEntry> r = router1.getNextHop("192.168.8.1");
        if (r.isPresent()) {
            //
            // Print the route entry used
            //
            r.get().print();
        }

    }
}
