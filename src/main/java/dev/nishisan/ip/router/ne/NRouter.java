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
package dev.nishisan.ip.router.ne;

import dev.nishisan.ip.base.BaseNe;
import dev.nishisan.ip.router.ne.NRoutingEntry.NRouteEntryScope;

public class NRouter extends BaseNe<NRouterInterface> {

    private final NRoutingTable mainRouteTable = new NRoutingTable("main", this);

    public NRouter(String name) {
        super(name);
    }

    /**
     * Adds a new interface without ip
     *
     * @param name
     * @return
     */
    public NRouterInterface addInterface(String name) {
        NRouterInterface iFace = new NRouterInterface(name, this);
        this.getInterfaces().put(name, iFace);
        return iFace;
    }

    /**
     * Adds new Network Interface with an IP Address
     *
     * @param name
     * @param address
     * @return
     */
    public NRouterInterface addInterface(String name, String address) {
        NRouterInterface iFace = new NRouterInterface(name, address, this);
        this.getInterfaces().put(name, iFace);
        this.mainRouteTable.addRouteEntry(iFace.getAddress().toPrefixBlock(), null, iFace.getAddress(), iFace, NRouteEntryScope.link);
        return iFace;
    }

    public NRoutingEntry addRouteEntry(String dst, String nextHop, String src, String dev) {
        /**
         * Ao adicionar uma rota devemos saber se o nextHop é alcançável
         */

        NRoutingEntry n = this.mainRouteTable.getNextHop(nextHop);

        if (n != null) {
            if (dev == null) {
                if (n.getDev() != null) {
                    dev = n.getDev().getName();
                }
            }

            if (src == null) {
                if (n.getSrc() != null) {
                    src = n.getSrc().toString();
                }
            }
        }
        /**
         * Se n for null o nexthop não é alcançável, aí não podemos adicionar
         */
        NRoutingEntry entry = new NRoutingEntry(dst, nextHop, src, this.getInterfaceByName(dev));
        return this.mainRouteTable.addRouteEntry(entry);
    }

    public void printRoutingTable() {
        this.mainRouteTable.printRoutingTable();
    }

    /**
     * Simula um ping respeitando a tabela de roteamento
     *
     * @param target
     */
    public void ping(String target) {
        System.out.println("Searching Routing Table for Target:[" + target + "] ");
        NRoutingEntry r = this.mainRouteTable.getNextHop(target);
        if (r != null) {
            System.out.println("Using Route Entry:");
            r.print();
        } else {
            System.out.println("Next Hop Not Found...");
        }
    }

}
