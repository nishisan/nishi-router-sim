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
         * Default Gateway do Router 1
         */
        router1.addRouteEntry("0.0.0.0/0", "192.168.0.2", "192.168.0.1", "ge0/0/0/1");
        router1.addRouteEntry("8.8.8.8/32", "192.168.4.254", "192.168.4.1", "ge0/0/0/5");

        router1.addRouteEntry("169.187.202.106/29", "133.147.163.8", "189.121.246.215", "ge0/0/0/8");
        router1.addRouteEntry("136.61.192.166/27", "5.16.236.21", "95.130.92.90", "ge0/0/0/2");
        router1.addRouteEntry("9.211.214.90/18", "184.221.136.40", "140.50.204.74", "ge0/0/0/2");
        router1.addRouteEntry("27.133.83.115/16", "251.224.131.230", "48.247.102.125", "ge0/0/0/0");
        router1.addRouteEntry("81.29.143.48/2", "199.221.71.64", "31.2.91.9", "ge0/0/0/1");
        router1.addRouteEntry("96.120.153.255/9", "157.125.125.187", "112.16.63.244", "ge0/0/0/7");
        router1.addRouteEntry("248.193.149.116/10", "73.209.2.11", "211.31.46.99", "ge0/0/0/8");
        router1.addRouteEntry("218.41.68.215/17", "130.151.98.252", "174.20.90.51", "ge0/0/0/5");
        router1.addRouteEntry("114.243.147.17/6", "143.109.87.63", "227.227.4.238", "ge0/0/0/9");
        router1.addRouteEntry("159.230.194.140/27", "254.110.38.199", "16.149.171.85", "ge0/0/0/2");
        router1.addRouteEntry("212.62.123.18/1", "52.218.246.44", "106.230.224.248", "ge0/0/0/8");
        router1.addRouteEntry("124.85.118.204/14", "188.42.224.251", "252.230.37.199", "ge0/0/0/8");
        router1.addRouteEntry("169.8.34.207/6", "40.228.85.126", "94.150.36.46", "ge0/0/0/6");
        router1.addRouteEntry("158.66.108.26/12", "102.3.60.241", "131.162.226.185", "ge0/0/0/6");
        router1.addRouteEntry("221.21.151.76/27", "42.203.249.138", "132.144.153.185", "ge0/0/0/3");
        router1.addRouteEntry("100.79.41.152/4", "79.251.75.85", "90.249.37.76", "ge0/0/0/4");
        router1.addRouteEntry("162.101.56.111/16", "148.123.210.158", "43.122.219.235", "ge0/0/0/4");
        router1.addRouteEntry("146.182.137.78/25", "155.85.187.159", "94.189.100.50", "ge0/0/0/3");
        router1.addRouteEntry("210.215.122.206/14", "253.112.106.248", "208.2.74.225", "ge0/0/0/5");
        router1.addRouteEntry("112.255.117.55/22", "195.132.31.30", "69.116.228.199", "ge0/0/0/2");

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
        router1.ping("192.168.0.2");
    }
}
