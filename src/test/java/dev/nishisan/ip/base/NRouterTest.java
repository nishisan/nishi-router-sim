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

import dev.nishisan.ip.router.ne.NRouter;
import dev.nishisan.ip.router.ne.NRouterInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author lucas
 */
public class NRouterTest {

    private static NRouter router1;
    private static NRouterInterface routerInterface;

    /**
     * Creates a basic router with 1 interface..check if the added interface is
     */
    @BeforeAll
    @DisplayName("Basic Router Test")
    public static void basicRouterTest() {
        router1 = new NRouter("router1");
        routerInterface = router1.addInterface("ge0/0/0/1", "10.0.0.1/24", "LT:switch-1 eth-1");
    }

    @Test
    public void interfaceAdminStatusTest() {
        Assertions.assertEquals(routerInterface.getAdminStatus(), BaseInterface.NIfaceAdminStatus.ADMIN_UP);
    }

    @Test
    public void interfaceOperStatusTest() {
        Assertions.assertEquals(routerInterface.getOperStatus(), BaseInterface.NIfaceOperStatus.OPER_DOWN);
    }

    @Test
    public void getByInterfaceNameTest() {
        Assertions.assertEquals(routerInterface, router1.getInterfaceByName("ge0/0/0/1"));
    }

    @Test
    public void getRoutingEntriesTest() {
        router1.printInterfaces();
        router1.printRoutingTable();
        Assertions.assertEquals(0, router1.getRoutes().size(), "Routing table must be empty");
    }
}
