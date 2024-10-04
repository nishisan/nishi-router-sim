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

import dev.nishisan.ip.base.BaseInterface;
import dev.nishisan.ip.base.BaseNe;
import dev.nishisan.ip.base.NPacket;
import dev.nishisan.ip.router.ne.NRoutingEntry.NRouteEntryScope;
import inet.ipaddr.IPAddress;
import java.util.Optional;

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
        if (iFace.getLink() == null) {
            iFace.setOperStatus(BaseInterface.NIfaceOperStatus.OPER_DOWN);
        }
        this.getInterfaces().put(name, iFace);
        this.mainRouteTable.addRouteEntry(iFace.getAddress().toPrefixBlock(),
                null,
                iFace.getAddress(),
                iFace, NRouteEntryScope.link);
        return iFace;
    }

    public NRouterInterface addInterface(String name, String address, String description) {
        NRouterInterface iFace = new NRouterInterface(name, address, this);
        if (iFace.getLink() == null) {
            iFace.setOperStatus(BaseInterface.NIfaceOperStatus.OPER_DOWN);
        }
        iFace.setDescription(description);
        this.getInterfaces().put(name, iFace);
        this.mainRouteTable.addRouteEntry(iFace.getAddress().toPrefixBlock(), null, iFace.getAddress(), iFace, NRouteEntryScope.link);
        return iFace;
    }

    public NRoutingEntry addRouteEntry(String dst, String nextHop) {
        return this.addRouteEntry(dst, nextHop, null, null);
    }

    public NRoutingEntry addRouteEntry(String dst, String nextHop, String src, NRouterInterface dev) {
        /**
         * Ao adicionar uma rota devemos saber se o nextHop é alcançável
         */

        Optional<NRoutingEntry> n = this.mainRouteTable.getNextHop(nextHop);

        if (n.isPresent()) {
            if (dev == null) {
                if (n.get().getDev() != null) {
                    dev = n.get().getDev();
                }
            }

            if (src == null) {
                if (n.get().getSrc() != null) {
                    src = n.get().getSrc().toString();
                }
            }
        }
        /**
         * Se n for null o nexthop não é alcançável, aí não podemos adicionar
         */
        NRoutingEntry entry = new NRoutingEntry(dst, nextHop, src, dev);
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
        Optional<NRoutingEntry> r = this.mainRouteTable.getNextHop(target);
        if (r.isPresent()) {
            System.out.println("Using Route Entry:");
            r.get().print();
        } else {
            System.out.println("Next Hop Not Found...");
        }
    }

    public Optional<NRoutingEntry> getNextHop(String target) {
        Optional<NRoutingEntry> r = this.mainRouteTable.getNextHop(target);
        return r;
    }

    public Optional<NRoutingEntry> getNextHop(IPAddress target) {
        Optional<NRoutingEntry> r = this.mainRouteTable.getNextHop(target);
        return r;
    }

    @Override
    public String getType() {
        return "ROUTER";
    }

    @Override
    public void forwardPacket(NPacket p) {
        //
        // A packet has been received, what should I Do ?
        //

        /**
         * 1- Get TTL, check and decrement by 1
         */
        if (p.getTtl().get() > 0) {

            System.out.println("Processing Packet:[" + p.getUuid() + "] From:[" + p.getSrc() + "] -> [" + p.getDst() + "] TTL:[" + p.getTtl().get() + "]");

            p.getTtl().decrementAndGet();
            Optional<NRoutingEntry> routeEntry = this.getNextHop(p.getDst());

            if (routeEntry.isPresent()) {
                //
                // We have a destination
                //
                routeEntry.get().print();
                if (routeEntry.get().getDirectConneted()) {
                    //
                    // Destination in same broadcast domain as me
                    //
                } else {
                    //
                    // Destination is other
                    //

                    //
                    // We need to check if we can find the arp entry of the routing destination
                    //
                    this.sendArpRequest(routeEntry.get().getNextHop()).thenAccept(r -> {
                        System.out.println(" :: ARP Found on:[" + r.getUid() + "]");
                        r.packetReceive(p);
                    });

                }
            } else {
                System.out.println("Unreacheable...");
            }

        } else {
            System.out.println("Packet Discarted");
        }

    }

}
