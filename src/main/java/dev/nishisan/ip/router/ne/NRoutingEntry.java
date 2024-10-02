package dev.nishisan.ip.router.ne;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddress;
import java.util.UUID;

public class NRoutingEntry {

    private IPAddress dst;
    private IPAddress nextHop;
    private IPAddress src;
    private NInterface dev;

    private NRouteEntryScope scope;

    public enum NRouteEntryScope {
        link;

    }

    public NRoutingEntry(IPAddress dst, IPAddress nextHop, IPAddress src, NInterface dev) {
        this.dst = dst;
        this.nextHop = nextHop;
        this.src = src;
        this.dev = dev;
    }

    public NRoutingEntry(IPAddress dst, IPAddress nextHop, IPAddress src, NInterface dev,NRouteEntryScope scope) {
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

    public NInterface getDev() {
        return dev;
    }

    public void setDev(NInterface dev) {
        this.dev = dev;
    }

    public String getUid() {
        return UUID.randomUUID().toString();
    }

    public NRouteEntryScope getScope() {
        return scope;
    }

    public void setScope(NRouteEntryScope scope) {
        this.scope = scope;
    }
}
