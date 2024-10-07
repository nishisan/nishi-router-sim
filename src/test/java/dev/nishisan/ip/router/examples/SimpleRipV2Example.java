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
        router1.addInterface("ge0/0/0/2", "192.168.1.1/24");
        router1.addInterface("ge0/0/0/3", "192.168.2.1/24");
        router1.addInterface("ge0/0/0/4", "192.168.3.1/24");
        router1.addInterface("ge0/0/0/5", "192.168.4.1/24");

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
        router2.addInterface("ge0/0/0/2", "10.0.2.1/24");
        router2.addInterface("ge0/0/0/3", "10.0.3.1/24");
        router2.addInterface("ge0/0/0/4", "10.0.4.1/24");

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
        vSwitch1.addInterface("eth-3", "LT:router-2");
        vSwitch1.addInterface("eth-4", "");

        vSwitch1.connect(router1.getInterfaceByName("ge0/0/0/1"), vSwitch1.getInterfaceByName("eth-1"));
        vSwitch1.connect(router2.getInterfaceByName("ge0/0/0/1"), vSwitch1.getInterfaceByName("eth-2"));

        /**
         * Start router-1
         */
        router1.start();

        /**
         * Start router-2
         */
        router2.start();

        Thread.sleep(5 * 1000);
        router1.shutDown();
        System.out.println("Done");
    }
}
