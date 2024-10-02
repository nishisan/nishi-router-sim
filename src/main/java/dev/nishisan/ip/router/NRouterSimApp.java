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
package dev.nishisan.ip.router;

import dev.nishisan.ip.nswitch.ne.NSwitch;
import dev.nishisan.ip.router.ne.NRouter;

public class NRouterSimApp {

    public static void main(String[] args) {
        /**
         * Cria um router e adiciona algumas interfaces..
         */
        NRouter router1 = new NRouter("router-1");
        router1.addInterface("ge0/0/0/1", "192.168.0.1/24");
        router1.addInterface("ge0/0/0/2", "192.168.1.1/24");
        router1.addInterface("ge0/0/0/3", "192.168.2.1/24");
        router1.addInterface("ge0/0/0/4", "192.168.3.1/24");
        router1.addInterface("ge0/0/0/5", "192.168.4.1/24");

        /**
         * Link scope
         */
        router1.addRouteEntry("10.10.10.10", null, null, "ge0/0/0/1");

        router1.addRouteEntry("0.0.0.0", "10.10.10.10", null, null);

        /**
         * Default Gateway do Router 1
         */
        router1.addRouteEntry("0.0.0.0/0", "192.168.0.2", "192.168.0.1", "ge0/0/0/1");
        
        /**
         * Rota Bem Especifica
         */
        router1.addRouteEntry("8.8.8.8/32", "192.168.4.254", "192.168.4.1", "ge0/0/0/5");

        NRouter router2 = new NRouter("router-2");
        router2.addInterface("ge0/0/0/1", "192.168.0.2/24");
        router2.addInterface("ge0/0/0/2", "192.168.1.2/24");
        router2.addInterface("ge0/0/0/3", "192.168.2.2/24");
        router2.addInterface("ge0/0/0/4", "192.168.3.2/24");

        /**
         * Exibe a tabela de roteamento dos roteadores
         */
        router1.printRoutingTable();
//        router2.printRoutingTable();

        /**
         * Cria um Switch-1
         */
        NSwitch vSwitch = new NSwitch("switch-1");
        /**
         * Adiciona 8 interfaces
         */
        vSwitch.addInterface("eth-1", "LT:router-1");
        vSwitch.addInterface("eth-2", "LT:router-2");
        vSwitch.addInterface("eth-3");
        vSwitch.addInterface("eth-4");
        vSwitch.addInterface("eth-5");
        vSwitch.addInterface("eth-6");
        vSwitch.addInterface("eth-7");
        vSwitch.addInterface("eth-8");

        /**
         * Conecta a porta ge0/0/0/1 do router a porta eth-1 do switch
         */
        vSwitch.connect(router1.getInterfaceByName("ge0/0/0/1"), vSwitch.getInterfaceByName("eth-1"));
        vSwitch.connect(router2.getInterfaceByName("ge0/0/0/1"), vSwitch.getInterfaceByName("eth-2"));

        /**
         * Teste de Ping !, para funcionar o router tem que ter aprendido a
         * tabela arp... o switch tb
         */
        router1.ping("8.8.8.8");
        router1.ping("1.1.1.1");
//        router1.ping("192.168.3.2");
//        router1.ping("10.10.10.10");
    }
}
