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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NRoutingEntry {

    private IPAddress dst;
    private IPAddress nextHop;
    private IPAddress src;
    private String uid = UUID.randomUUID().toString();
    private NRouterInterface dev;
    private Boolean directConneted = false;
    private Integer metric = 0;
    private Integer adminDistance = 0;
    private Integer distance = 0;
    private Long upSince = 0L;

    private NRouteEntryScope scope;

    private NRouteType type;

    public enum NRouteEntryScope {
        link;
    }

    public enum NRouteType {
        STATIC,
        CONNECTED,
        RIP
    }

    public NRoutingEntry(String dst, String nextHop, String src, NRouterInterface dev, NRouteType type) {
        /**
         * Sanitização
         */

        if (dst != null) {
            if (dst.equals("0.0.0.0")) {
                dst = "0.0.0.0/0";
            } else {
                if (!dst.contains("/")) {
                    //
                    // Se não tem / assume /32
                    //
                    dst = dst + "/32";
                }
            }
        } else {
            //
            // Assume quando o DST for null que é 0.0.0.0/0
            //
            dst = "0.0.0.0/0";
        }

        if (dst != null) {
            this.dst = NRoutingEntry.getIpAddress(dst);
        }
        if (nextHop != null) {
            this.nextHop = NRoutingEntry.getIpAddress(nextHop);
        } else {
            this.setDirectConneted(true);
        }
        if (src != null) {
            this.src = NRoutingEntry.getIpAddress(src);
        }
        this.type = type;
        this.dev = dev;
        this.upSince = System.currentTimeMillis();
    }

    public NRoutingEntry(IPAddress dst, IPAddress nextHop, IPAddress src, NRouterInterface dev, NRouteType type) {
        this.dst = dst;
        if (nextHop == null) {
            this.setDirectConneted(true);
        }
        this.nextHop = nextHop;
        this.src = src;
        this.dev = dev;
        this.type = type;
        this.upSince = System.currentTimeMillis();
    }

    public NRoutingEntry(IPAddress dst, IPAddress nextHop, IPAddress src, NRouterInterface dev, NRouteEntryScope scope, NRouteType type) {
        this.dst = dst;
        this.nextHop = nextHop;
        if (nextHop == null) {
            this.setDirectConneted(true);
        }
        this.src = src;
        this.dev = dev;
        this.scope = scope;
        this.type = type;
        this.upSince = System.currentTimeMillis();
    }

    public IPAddress getDst() {
        return dst;
    }

    public void setDst(IPAddress dst) {
        this.dst = dst;
    }

    public IPAddress getNextHop() {
        return nextHop;
    }

    public void setNextHop(IPAddress nextHop) {
        this.nextHop = nextHop;
    }

    public IPAddress getSrc() {
        return src;
    }

    public void setSrc(IPAddress src) {
        this.src = src;
    }

    public NRouterInterface getDev() {
        return dev;
    }

    public void setDev(NRouterInterface dev) {
        this.dev = dev;
    }

    public String getUid() {
        return this.uid;
    }

    public NRouteEntryScope getScope() {
        return scope;
    }

    public NRoutingEntry setScope(NRouteEntryScope scope) {
        this.scope = scope;
        return this;
    }

    public void print() {
        NRoutingEntry.print(this);
    }

    public static void print(NRoutingEntry e) {
        StringBuilder b = new StringBuilder();
        b.append("   ");
        b.append(e.getDst().toPrefixBlock().toString());

        b.append(" [")
                .append(e.getAdminDistance())
                .append("/").append(e.getMetric()).append("]");

        if (e.getNextHop() != null) {
            b.append(" via ").append(e.getNextHop().toString());
        }
        if (e.getDev() != null) {
            b.append(" dev ").append(e.getDev().getName());
        }

        if (e.getScope() != null) {
            b.append(" scope ").append(e.getScope());
        }

        if (e.getSrc() != null) {
            b.append(" src ").append(e.getSrc().toInetAddress().getHostAddress());
        }

        if (e.getMetric() != null) {
            b.append(" metric ").append(e.getMetric());
        }

        if (e.getDirectConneted()) {
            b.append(" direct connected ");
        }

        System.out.println(b.toString());
    }

    public Boolean getDirectConneted() {
        return directConneted;
    }

    public NRoutingEntry setDirectConneted(Boolean directConneted) {
        this.directConneted = directConneted;
        return this;
    }

    public static IPAddress getIpAddress(String ip) {
        IPAddressString s = new IPAddressString(ip);
        return s.getAddress();
    }

    public Integer getMetric() {
        return metric;
    }

    public NRoutingEntry setMetric(Integer metric) {
        this.metric = metric;
        return this;
    }

    public Integer getAdminDistance() {
        return adminDistance;
    }

    public NRoutingEntry setAdminDistance(Integer adminDistance) {
        this.adminDistance = adminDistance;
        return this;
    }

    public Integer getDistance() {
        return distance;
    }

    public NRoutingEntry setDistance(Integer distance) {
        this.distance = distance;
        return this;
    }

    public NRouteType getType() {
        return type;
    }

    public NRoutingEntry setType(NRouteType type) {
        this.type = type;
        return this;
    }

    public Long getUpSince() {
        return upSince;
    }

    public void setUpSince(Long upSince) {
        this.upSince = upSince;
    }

    // Método para formatar o tempo de uptime em d00h00m
    public String formatUptime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.HOURS.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return String.format("%dd%02dh%02dm%02ds", days, hours, minutes, seconds);
    }

    public String getStringUptime() {
        long uptimeMillis = System.currentTimeMillis() - this.getUpSince();
        return this.formatUptime(uptimeMillis);
    }
}
