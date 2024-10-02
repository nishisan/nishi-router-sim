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

public class NRoutingEntry {

    private IPAddress dst;
    private IPAddress nextHop;
    private IPAddress src;
    private String uid = UUID.randomUUID().toString();
    private NRouterInterface dev;

    private NRouteEntryScope scope;

    public NRoutingEntry(String dst, String nextHop, String src, NRouterInterface dev) {
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
        }
        if (src != null) {
            this.src = NRoutingEntry.getIpAddress(src);
        }
        this.dev = dev;
    }

    public enum NRouteEntryScope {
        link;

    }

    public NRoutingEntry(IPAddress dst, IPAddress nextHop, IPAddress src, NRouterInterface dev) {
        this.dst = dst;
        this.nextHop = nextHop;
        this.src = src;
        this.dev = dev;
    }

    public NRoutingEntry(IPAddress dst, IPAddress nextHop, IPAddress src, NRouterInterface dev, NRouteEntryScope scope) {
        this.dst = dst;
        this.nextHop = nextHop;
        this.src = src;
        this.dev = dev;
        this.scope = scope;
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

    public void setScope(NRouteEntryScope scope) {
        this.scope = scope;
    }

    public void print() {
        NRoutingEntry.print(this);
    }

    public static void print(NRoutingEntry e) {
        StringBuilder b = new StringBuilder();
        b.append("   ");
        b.append(e.getDst().toPrefixBlock().toString());
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

        System.out.println(b.toString());
    }

    public static IPAddress getIpAddress(String ip) {
        IPAddressString s = new IPAddressString(ip);
        return s.getAddress();
    }
}
