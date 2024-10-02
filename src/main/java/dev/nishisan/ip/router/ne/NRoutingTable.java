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

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import java.util.Comparator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class NRoutingTable {

    private String name;
    private NRouter router;
    private Map<String, NRoutingEntry> entries = new ConcurrentHashMap<>();

    public NRoutingTable(String name, NRouter router) {
        this.name = name;
        this.router = router;
    }

    public NRoutingEntry addRouteEntry(IPAddress dst, IPAddress nextHope, IPAddress src, NRouterInterface dev) {
        NRoutingEntry entry = new NRoutingEntry(dst.toPrefixBlock(), nextHope, src, dev);
        this.entries.put(entry.getUid(), entry);
        return entry;
    }

    public NRoutingEntry addRouteEntry(NRoutingEntry entry) {
        this.entries.put(entry.getUid(), entry);
        return entry;
    }

    public NRoutingEntry addRouteEntry(IPAddress dst, IPAddress nextHope, IPAddress src, NRouterInterface dev, NRoutingEntry.NRouteEntryScope scope) {
        NRoutingEntry entry = new NRoutingEntry(dst.toPrefixBlock(), nextHope, src, dev, scope);
        this.entries.put(entry.getUid(), entry);
        return entry;
    }

    public void printRoutingTable() {
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Router.........:[" + this.router.getName() + "]");
        System.out.println("Routing Table..:[" + this.name + "]");
        System.out.println("Prefixes v4....:[" + this.entries.size() + "]");

        System.out.println("------------------------------------------------------------------------------------");
        AtomicLong idx = new AtomicLong(1L);
        this.entries.forEach((k, v) -> {

            StringBuilder b = new StringBuilder();
            b.append(idx.get()).append(" - ");
            b.append(v.getDst().toPrefixBlock().toString());
            if (v.getNextHop() != null) {
                b.append(" via ").append(v.getNextHop().toString());
            }
            if (v.getDev() != null) {
                b.append(" dev ").append(v.getDev().getName());
            }

            if (v.getScope() != null) {
                b.append(" scope ").append(v.getScope());
            }

            if (v.getSrc() != null) {
                b.append(" src ").append(v.getSrc().toInetAddress().getHostAddress());
            }
            idx.incrementAndGet();
            System.out.println(b.toString());

        });
        System.out.println("------------------------------------------------------------------------------------");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NRouter getRouter() {
        return router;
    }

    public void setRouter(NRouter router) {
        this.router = router;
    }

    public Map<String, NRoutingEntry> getEntries() {
        return entries;
    }

    public void setEntries(Map<String, NRoutingEntry> entries) {
        this.entries = entries;
    }

    private NRoutingEntry getNextHop_(String destinationIp) {
        NRoutingEntry bestMatch = null;
        int bestMatchPrefixlen = -1;

        IPAddress destinationAddress = new IPAddressString(destinationIp).getAddress();

        for (Map.Entry<String, NRoutingEntry> entry : entries.entrySet()) {
            NRoutingEntry route = entry.getValue();
            IPAddress networkAddress = route.getDst();
            int prefixLength = networkAddress.getPrefixLength();

            if (networkAddress.contains(destinationAddress)) {
                if (prefixLength > bestMatchPrefixlen) {
                    bestMatch = route;
                    bestMatchPrefixlen = prefixLength;
                }
            }
        }

        return bestMatch;
    }

    public NRoutingEntry getNextHop__(String destinationIp) {
        IPAddress destinationAddress = new IPAddressString(destinationIp).getAddress();

        return entries.entrySet().parallelStream()
                .filter(entry -> entry.getValue().getDst().contains(destinationAddress))
                .max(Comparator.comparingInt(entry -> entry.getValue().getDst().getPrefixLength()))
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public NRoutingEntry getNextHop(String destinationIp) {
        Long s = System.currentTimeMillis();
        try {
            
            IPAddress destinationAddress = new IPAddressString(destinationIp).getAddress();

            return entries.entrySet().parallelStream()
                    .filter(entry -> entry.getValue().getDst().contains(destinationAddress))
                    .max(Comparator.comparingInt(entry -> {
                        // Priorizar rotas mais específicas, exceto a rota padrão
                        int prefixLength = entry.getValue().getDst().getPrefixLength();
                        return entry.getKey().equals("0.0.0.0/0") ? prefixLength - 1 : prefixLength;
                    }))
                    .map(Map.Entry::getValue)
                    .orElse(null);
        } finally {
            Long e = System.currentTimeMillis();
            Long t = e-s;
            System.out.println("Took :["+t+"] (ms) To Search in:["+this.entries.size()+"] Entries");
        }
    }

}
