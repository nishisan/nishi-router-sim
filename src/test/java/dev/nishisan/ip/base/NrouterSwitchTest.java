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
import dev.nishisan.ip.nswitch.ne.NSwitchInterface;
import dev.nishisan.ip.router.ne.NRouter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author lucas
 */
public class NrouterSwitchTest {

    private static NRouter router1;
    private static NRouter router2;
    private static NRouter router3;
    private static NSwitch switch1;
    private static NSwitch switch2;

    /**
     * Monta uma topologia
     */
    @BeforeAll
    public static void initRouterSwitch() {
        /**
         * Cria um router e adiciona algumas interfaces..
         */
        router1 = new NRouter("router-1");
        router1.addInterface("ge0/0/0/1", "10.0.0.1/24", "LT:switch-1 eth-1");
        router1.addInterface("ge0/0/0/2", "192.168.1.1/24");
        router1.addInterface("ge0/0/0/3", "192.168.2.1/24");
        router1.addInterface("ge0/0/0/4", "192.168.3.1/24");

        router1.printInterfaces();

        router2 = new NRouter("router-2");
        router2.addInterface("ge0/0/0/1", "10.0.0.254/24", "LT:switch-1 eth-1");
        router2.addInterface("ge0/0/0/2", "10.0.2.1/24");
        router2.addInterface("ge0/0/0/3", "10.0.3.1/24");
        router2.addInterface("ge0/0/0/4", "10.0.4.1/24");

        router2.printInterfaces();

        router3 = new NRouter("router-3");
        router3.addInterface("ge0/0/0/1", "10.0.0.2/24", "LT:switch-2 eth-2");
        router3.addInterface("ge0/0/0/2", "172.30.0.1/24");
        router3.addInterface("ge0/0/0/3", "172.30.1.1/24");
        router3.addInterface("ge0/0/0/4", "172.30.2.1/24");

        router3.printInterfaces();
        /**
         * Cria 2 switches, e conecta um router em cada switch, e uma interface
         * eth3 entre os switches.
         */
        switch1 = new NSwitch("switch-1");
        switch1.addInterface("eth-1", "LT:router-1");
        switch1.addInterface("eth-2", "");
        switch1.addInterface("eth-3", "LT:switch-2");

        switch2 = new NSwitch("switch-2");
        switch2.addInterface("eth-1", "LT:router-2");
        switch2.addInterface("eth-2", "LT:router-3");
        switch2.addInterface("eth-3", "LT:switch-1");

        switch1.connect(router1.getInterfaceByName("ge0/0/0/1"), switch1.getInterfaceByName("eth-1"));//.setLatency(20).setJitter(5);
        switch2.connect(router2.getInterfaceByName("ge0/0/0/1"), switch2.getInterfaceByName("eth-1"));//.setLatency(20).setJitter(5);
        switch2.connect(router3.getInterfaceByName("ge0/0/0/1"), switch2.getInterfaceByName("eth-2"));//.setLatency(20).setJitter(5);

        switch1.connect(switch1.getInterfaceByName("eth-3"), switch2.getInterfaceByName("eth-3"));

        switch1.printInterfaces();
        switch2.printInterfaces();
    }

    /**
     * Testa se as interfaces que ficaram UP dão match no que foi conectado
     */
    @Test
    public void testTopology() {
        router1.printInterfaces();
        router2.printInterfaces();
        router3.printInterfaces();

        switch1.printInterfaces();
        switch2.printInterfaces();

        /**
         * Router 1 should have 1 route, a 1 interface up
         */
        long router1Interfaces = router1.getInterfaces().values().stream()
                .filter(BaseInterface::isOperStatusUp)
                .count();

        Assertions.assertEquals(1L, router1Interfaces);

        /**
         * Router 2 should have 1 route, a 1 interface up
         */
        long router2Interfaces = router2.getInterfaces().values().stream()
                .filter(BaseInterface::isOperStatusUp)
                .count();

        Assertions.assertEquals(1L, router2Interfaces);

        /**
         * Router 3 should have 1 route, a 1 interface up
         */
        long router3Interfaces = router3.getInterfaces().values().stream()
                .filter(BaseInterface::isOperStatusUp)
                .count();

        Assertions.assertEquals(1L, router3Interfaces);

        /**
         * Switch 1 should have 1 route, a 1 interface up
         */
        long switch1Interfaces = switch1.getInterfaces().values().stream()
                .filter(NSwitchInterface::isOperStatusUp)
                .count();

        Assertions.assertEquals(2L, switch1Interfaces);

        /**
         * Switch 2 should have 1 route, a 1 interface up
         */
        long switch2Interfaces = switch2.getInterfaces().values().stream()
                .filter(NSwitchInterface::isOperStatusUp)
                .count();

        Assertions.assertEquals(3L, switch2Interfaces);
        
       
    }
}
