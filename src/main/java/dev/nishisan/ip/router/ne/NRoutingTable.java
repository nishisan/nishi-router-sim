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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class NRoutingTable {

    private String name;
    private NRouter router;

    private Map<String, NRoutingEntry> entries = Collections.synchronizedMap(new LinkedHashMap());

    public NRoutingTable(String name, NRouter router) {
        this.name = name;
        this.router = router;
    }

    public NRoutingEntry addStaticRouteEntry(IPAddress dst, IPAddress nextHope, IPAddress src, NRouterInterface dev) {
        NRoutingEntry entry = new NRoutingEntry(dst.toPrefixBlock(), nextHope, src, dev, NRoutingEntry.NRouteType.STATIC);
        return this.addRoute(entry);
    }

    public NRoutingEntry addRoute(NRoutingEntry entry) {
        if (!this.entries.values().contains(entry)) {
            this.entries.put(entry.getUid(), entry);
        }
        return entry;
    }

    public NRoutingEntry addStaticRouteEntry(IPAddress dst, IPAddress nextHope, IPAddress src, NRouterInterface dev, NRoutingEntry.NRouteEntryScope scope) {
        NRoutingEntry entry = new NRoutingEntry(dst.toPrefixBlock(), nextHope, src, dev, scope, NRoutingEntry.NRouteType.STATIC);
        return this.addRoute(entry);
    }

    public void printRoutingTable() {
        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.println("Router.........:[" + this.router.getName() + "]");
        System.out.println("Routing Table..:[" + this.name + "]");
        System.out.println("Prefixes v4....:[" + this.entries.size() + "]");
        System.out.println("-----------------------------------------------------------------------------------------------------");

        // Legenda do comando show ip route
        System.out.println("Codes: C - connected, S - static, R - RIP");

        AtomicLong idx = new AtomicLong(1L);
        this.entries.forEach((k, v) -> {

            StringBuilder b = new StringBuilder();

            // Identificando o tipo de rota com base no enum NRouteType
            switch (v.getType()) {
                case CONNECTED:
                    b.append("C    "); // Rota Conectada
                    break;
                case STATIC:
                    b.append("S    "); // Rota Estática
                    break;
                case RIP:
                    b.append("R    "); // Rota RIP
                    break;
                default:
                    b.append("?    "); // Desconhecido
                    break;
            }

            // Prefixo e Métricas (ID - AD/Métrica)
            b.append(v.getDst().toPrefixBlock().toString())
                    .append(" [")
                    .append(v.getAdminDistance())
                    .append("/")
                    .append(v.getMetric())
                    .append("] ");

            // Próximo salto (Next hop)
            if (v.getNextHop() != null) {
                b.append("via ").append(v.getNextHop().toString()).append(", ");
            }

            // Interface e Tempo fictício
            if (v.getDev() != null) {
                b.append(v.getDev().getName()).append(", ");
            }

            b.append(v.getStringUptime()).append(" "); // Adiciona o tempo formatado

            // Escopo e Origem
            if (v.getScope() != null) {
                b.append("scope ").append(v.getScope());
            }
            if (v.getSrc() != null) {
                b.append(" src ").append(v.getSrc().toInetAddress().getHostAddress());
            }

            System.out.println(b.toString());
        });

        System.out.println("-----------------------------------------------------------------------------------------------------");
    }

    public void printRoutingTable2() {
        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.println("Router.........:[" + this.router.getName() + "]");
        System.out.println("Routing Table..:[" + this.name + "]");
        System.out.println("Prefixes v4....:[" + this.entries.size() + "]");

        System.out.println("-----------------------------------------------------------------------------------------------------");
        AtomicLong idx = new AtomicLong(1L);
        this.entries.forEach((k, v) -> {

            StringBuilder b = new StringBuilder();
            b.append(idx.get()).append(" - ");
            b.append(v.getDst().toPrefixBlock().toString());
            b.append(" [")
                    .append(v.getAdminDistance())
                    .append("/").append(v.getMetric()).append("]");
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

            if (v.getMetric() != null) {
                b.append(" metric ").append(v.getMetric());
            }
            if (v.getDirectConneted()) {
                b.append(" direct connected ");
            }
            idx.incrementAndGet();
            System.out.println(b.toString());

        });
        System.out.println("-----------------------------------------------------------------------------------------------------");
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

    /**
     * Obtem o next hop da tabela de roteamento pela representação em ip
     *
     * @param destinationIp
     * @return
     */
    public Optional<NRoutingEntry> getNextHop(String destinationIp) {
        IPAddress destinationAddress = NRoutingEntry.getIpAddress(destinationIp);
        return this.getNextHop(destinationAddress);
    }

    /**
     * Computes the best route based on métric and admin distance.é
     *
     * @param destinationAddress
     * @return
     */
    public Optional<NRoutingEntry> getNextHop(IPAddress destinationAddress) {
        Long s = System.currentTimeMillis();
        try {
            NRoutingEntry r = entries.entrySet().parallelStream()
                    .filter(entry -> entry.getValue().getDst().contains(destinationAddress))
                    .max(Comparator.<Map.Entry<String, NRoutingEntry>>comparingInt(entry -> {
                        return entry.getValue().getDst().getPrefixLength();
                    }).thenComparingInt(entry -> {
                        return entry.getValue().getMetric() * -1;
                    }))
                    .map(Map.Entry::getValue)
                    .orElse(null);

            return Optional.ofNullable(r);
        } catch (Exception ex) {
            //
            // Em caso de ex, retorna null
            //
            return Optional.ofNullable(null);
        } finally {
            Long e = System.currentTimeMillis();
            Long t = e - s;
//            System.out.println("Took :[" + t + "] (ms) To Search in:[" + this.entries.size() + "] Entries");
        }
    }
}
