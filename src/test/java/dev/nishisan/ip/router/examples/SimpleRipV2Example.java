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
import dev.nishisan.ip.router.protocols.configuration.RipV2ProtocolConfiguration;

/**
 *
 * @author lucas
 */
public class SimpleRipV2Example {

    public static void main(String[] args) throws InterruptedException {
        NRouter router1 = new NRouter("router-1");
        router1.addInterface("ge0/0/0/1", "192.168.0.1/24", "UPLINK");


        RipV2ProtocolConfiguration router1RipV2Configuration = new RipV2ProtocolConfiguration();
        /**
         * Enabled Ripv2
         */
        router1RipV2Configuration.setEnabled(true);
        /**
         * Add All Networks
         */
        router1RipV2Configuration.addNetworks(router1.getRoutes());

        /**
         * Add the configuration Protocol to the router
         */
        router1.getRouterConfiguration().addRouterProtocolConfiguration(router1RipV2Configuration);

        NRouter router2 = new NRouter("router-2");
        router2.addInterface("ge0/0/0/1", "192.168.0.2/24", "UPLINK");
//        router2.addInterface("ge0/0/0/2", "10.0.2.1/24");
//        router2.addInterface("ge0/0/0/3", "10.0.3.1/24");
//        router2.addInterface("ge0/0/0/4", "10.0.4.1/24");

        RipV2ProtocolConfiguration router2RipV2Configuration = new RipV2ProtocolConfiguration();
        /**
         * Enabled Ripv2
         */
        router2RipV2Configuration.setEnabled(true);
        /**
         * Add All Networks
         */
        router2RipV2Configuration.addNetworks(router2.getRoutes());

        /**
         * Add the configuration Protocol to the router
         */
        router2.getRouterConfiguration().addRouterProtocolConfiguration(router2RipV2Configuration);

        /**
         * Add new Switch
         */
        NSwitch vSwitch1 = new NSwitch("switch-1");
        vSwitch1.addInterface("eth-1", "LT:router-1");
        vSwitch1.addInterface("eth-2", "LT:router-2");
//        vSwitch1.addInterface("eth-3", "LT:router-2");
//        vSwitch1.addInterface("eth-4", "");

        vSwitch1.connect(router1.getInterfaceByName("ge0/0/0/1"), vSwitch1.getInterfaceByName("eth-1"));
        vSwitch1.connect(router2.getInterfaceByName("ge0/0/0/1"), vSwitch1.getInterfaceByName("eth-2"));

        router1.setTickTime(10);
        
        router1.printInterfaces();
        router2.printInterfaces();
        vSwitch1.printInterfaces();
        System.out.println("------------------------------------------------------------------------------------");
        /**
         * Start router-1
         */
        router1.start();
        /**
         * Start router-2
         */
        router2.setTickTime(10);
        router2.start();
//        router1.getInterfaces().forEach((uid, iFace) -> {
//            /**
//             * Build Route Annouce RipV2 as Mcast Packet
//             */
//            MulticastGroup ripv2Group = iFace.joinMcastGroup("224.0.0.9"); // Join Ripv2 Group
//
//            RipV2Payload payLoad = new RipV2Payload(router1RipV2Configuration.getNetworksAsList(), iFace.getAddress(), iFace);
//            RipV2AnnoucePacket ripv2Announce = new RipV2AnnoucePacket(payLoad, ripv2Group);
//
//            router1.sendMcastPacket(ripv2Announce);
//        });

        Thread.sleep(10 * 1000);
        router1.shutDown();
        router2.shutDown();
        System.out.println("Done");
    }
}
