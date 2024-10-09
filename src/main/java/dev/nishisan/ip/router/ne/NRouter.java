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

import dev.nishisan.ip.router.ne.configuration.NRouterConfig;
import dev.nishisan.ip.base.BaseNe;
import dev.nishisan.ip.base.MulticastGroup;
import dev.nishisan.ip.packet.MultiCastPacket;
import dev.nishisan.ip.packet.NPacket;
import dev.nishisan.ip.packet.RipV1AnnouncePacket;
import dev.nishisan.ip.packet.RipV2AnnoucePacket;
import dev.nishisan.ip.packet.payload.RipV2Payload;
import dev.nishisan.ip.packet.processor.ArpPacketProcessor;
import dev.nishisan.ip.packet.processor.RipV1PacketProcessor;
import dev.nishisan.ip.router.exception.InvalidConfigurationCastException;
import dev.nishisan.ip.router.ne.NRoutingEntry.NRouteEntryScope;
import dev.nishisan.ip.router.protocols.configuration.IRoutingProtocolConfiguration;
import dev.nishisan.ip.router.protocols.configuration.RipV2ProtocolConfiguration;
import inet.ipaddr.IPAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NRouter extends BaseNe<NRouterInterface> {

    private final NRoutingTable mainRouteTable = new NRoutingTable("main", this);
    private final NRouterConfig routerConfiguration = new NRouterConfig();

    public NRouter(String name) {
        super(name);
        this.routerConfiguration.setSysName(name);
    }

    /**
     * Adds a new interface without ip
     *
     * @param name
     * @return
     */
    public NRouterInterface addInterface(String name) {
        NRouterInterface iFace = new NRouterInterface(name, this, this.getDefaultBroadcastDomain());
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
        NRouterInterface iFace = new NRouterInterface(name, address, this, this.getDefaultBroadcastDomain());
        if (iFace.getLink() == null) {
            iFace.setOperStatus(NRouterInterface.NIfaceOperStatus.OPER_DOWN);
        }
        this.getInterfaces().put(name, iFace);

        return iFace;
    }

    /**
     * Add a new interface to the router
     *
     * @param name
     * @param address
     * @param description
     * @return
     */
    public NRouterInterface addInterface(String name, String address, String description) {
        NRouterInterface iFace = new NRouterInterface(name, address, this, this.getDefaultBroadcastDomain());
        if (iFace.getLink() == null) {
            iFace.setOperStatus(NRouterInterface.NIfaceOperStatus.OPER_DOWN);
        }
        iFace.setDescription(description);
        this.getInterfaces().put(name, iFace);
        return iFace;
    }

    public NRoutingEntry addStaticRouteEntry(String dst, String nextHop) {
        return this.addStaticRouteEntry(dst, nextHop, null, null);
    }

    public NRoutingEntry addRipRouteEntry(IPAddress dst, IPAddress nextHop, IPAddress src, NRouterInterface dev) {

        Optional<NRoutingEntry> n = this.mainRouteTable.getNextHop(nextHop);

        if (n.isPresent()) {
            if (dev == null) {
                if (n.get().getDev() != null) {
                    dev = n.get().getDev();
                }
            }

            if (src == null) {
                if (n.get().getSrc() != null) {
                    src = n.get().getSrc();
                }
            }
        }

        NRoutingEntry entry = new NRoutingEntry(dst, nextHop, src, dev, NRoutingEntry.NRouteType.RIP);
        entry.setAdminDistance(120);
        return this.mainRouteTable.addRoute(entry);
    }

    public NRoutingEntry addStaticRouteEntry(String dst, String nextHop, String src, NRouterInterface dev) {
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
        NRoutingEntry entry = new NRoutingEntry(dst, nextHop, src, dev, NRoutingEntry.NRouteType.STATIC);
        entry.setAdminDistance(1);
        return this.mainRouteTable.addRoute(entry);
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

            System.out.println("[" + this.getName() + "] - Processing Packet:[" + p.getUuid() + "] Type:[" + p.getType() + "] From:[" + p.getSrc() + "] -> [" + p.getDst() + "] TTL:[" + p.getTtl().get() + "]");

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

                    //routeEntry.get().print();
                    if (routeEntry.get().getSrc().equals(p.getDst())) {

                        //
                        // Should we generate a pong ?
                        //
                        p.setConnected(true);
                        p.stopForwarding();

                        //
                        //Raise Back round
                        //
                        if (p.getType().equals(NPacket.NPacketType.REQUEST)) {
//                            System.out.println("Ping! 1");
                            //
                            // Answer only for reply
                            //
                            NPacket r = routeEntry.get().getDev().sendPacket(p.createReply());
                        } else {
//                            System.out.println("Ping! 2");
                            if (p.getSource() != null) {
                                /**
                                 * Link request and reply
                                 */
                                p.getSource().setReply(p);
                                p.getSource().reply();
                            }
                        }
                    }

                } else {
                    //
                    // Destination is other
                    //

                    //
                    // We need to check if we can find the arp entry of the routing destination
                    //
                    this.sendArpRequest(routeEntry.get().getNextHop()).thenAccept(r -> {
//                        System.out.println(" :: ARP Found on:[" + r.getUid() + "]"
//                                + " IP:[" + routeEntry.get().getNextHop() + "] Mac:[" + r.getMacAddress()
//                                + "]");
//                        System.out.println("------------------------------------------------------------------------------------------------------------------");
                        r.sendPacket(p);
                    }).orTimeout(p.getTimeout(), TimeUnit.SECONDS).exceptionally(ex -> {
                        // Tratamento em caso de timeout ou exceção                      
                        System.out.println("Arp Timeout:" + ex.getMessage());
                        return null;
                    }).join();

                }
            } else {
                System.out.println("Unreacheable...");
            }

        } else {
            System.out.println("Packet Discarted");
        }

    }

    @Override
    public void printInterfaces() {
        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.println("Device.........:[" + this.getName() + "]");
        System.out.println("Interfaces.....:[" + this.getInterfaces().size() + "]");
        System.out.println("-----------------------------------------------------------------------------------------------------");
        String header = String.format("%-15s %-15s %-15s %-15s %-18s %-30s", "Interface", "Admin Status", "Oper Status", "IP", "MAC Address", "Description");
        System.out.println(header);
        System.out.println("-----------------------------------------------------------------------------------------------------");
        this.getInterfaces().forEach((k, v) -> {
            String row = String.format("%-15s %-15s %-15s %-15s %-18s %-30s", v.getName(), v.getAdminStatus(), v.getOperStatus(), v.getAddress().toNormalizedString(), v.getMacAddress(), v.getDescription());
            System.out.println(row);
        });
        System.out.println("-----------------------------------------------------------------------------------------------------");
    }

    @Override
    public void registerProcessors() {
        this.addProcessor(new ArpPacketProcessor());
        this.addProcessor(new RipV1PacketProcessor());
    }

    /**
     * Monta um anuncio RIP :)
     *
     * @return
     */
    public CompletableFuture<RipV1AnnouncePacket> sendRipV1Annouce(NRouterInterface i) {
        RipV1AnnouncePacket r = new RipV1AnnouncePacket();
        r.setSource(i.getAddress());
        r.getNetworks().addAll(this.mainRouteTable.getEntries().values());
        CompletableFuture<RipV1AnnouncePacket> future = new CompletableFuture<>();
        r.onReply(o -> {
            future.complete(o);
        });
        this.sendBroadCastMessage(r);
        return future;
    }

    @Override
    public void tick() {
        this.processRoutingProtocols();
    }

    public NRouterConfig getRouterConfiguration() {
        return routerConfiguration;
    }

    public List<NRoutingEntry> getRoutes() {
        return new ArrayList<>(this.mainRouteTable.getEntries().values());
    }

    /**
     * Process routing protocols Logic
     */
    private void processRoutingProtocols() {
        if (this.routerConfiguration.getRouterProtocolsConfiguration().containsKey(RipV2ProtocolConfiguration.type)) {
            this.annouceRipV2RoutingProcotol();
        }

    }

    private void annouceRipV2RoutingProcotol() {
        IRoutingProtocolConfiguration protocol = this.routerConfiguration.getRouterProtocolsConfiguration().get(RipV2ProtocolConfiguration.type);
        try {
            RipV2ProtocolConfiguration ripv2Configuration = protocol.getRipV2Configuration();

            if (ripv2Configuration.getEnabled()) {
                /**
                 * In Case we Can Announce on All interfaces and Join All
                 * Interfaces
                 */
                if (ripv2Configuration.getPassiveInterface().isEmpty()) {
                    //
                    // We can announce on all networks
                    //
                    this.getInterfaces().forEach((uid, iFace) -> {
                        /**
                         * Build Route Annouce RipV2 as Mcast Packet
                         */
                        MulticastGroup ripv2Group = iFace.joinMcastGroup("224.0.0.9"); // Join Ripv2 Group

                        RipV2Payload payLoad = new RipV2Payload(ripv2Configuration.getNetworksAsList(), iFace.getAddress(), iFace);
                        RipV2AnnoucePacket ripv2Announce = new RipV2AnnoucePacket(payLoad, ripv2Group);

                        this.sendMcastPacket(ripv2Announce);
                    });
                }
            }

        } catch (InvalidConfigurationCastException ex) {
            Logger.getLogger(NRouter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void sendMcastPacket(MultiCastPacket mcastPacket) {
        /**
         * 1 Join Interface to the Group
         */
        MulticastGroup group = mcastPacket.getSrcIface().joinMcastGroup(mcastPacket.getGroup());
        
        /**
         * Sent the packet to the joined group
         */
        group.sendMulticasPacket(mcastPacket);
//        System.out.println("Sent Mcast Packet to:" + group.getMcastGroup().toString() + " From:[" + mcastPacket.getSrcIface().getAddress() + "/" + mcastPacket.getSrcIface().getMacAddress() + "]");

    }

    public NRoutingTable getMainRouteTable() {
        return this.mainRouteTable;
    }
}
