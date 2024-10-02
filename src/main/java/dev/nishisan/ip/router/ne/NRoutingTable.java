package dev.nishisan.ip.router.ne;

import inet.ipaddr.IPAddress;

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

    public NRoutingEntry addRouteEntry(IPAddress dst, IPAddress nextHope, IPAddress src, NInterface dev) {
        NRoutingEntry entry = new NRoutingEntry(dst.toPrefixBlock(), nextHope, src, dev);
        this.entries.put(entry.getUid(), entry);
        return entry;
    }

    public NRoutingEntry addRouteEntry(IPAddress dst, IPAddress nextHope, IPAddress src, NInterface dev, NRoutingEntry.NRouteEntryScope scope) {
        NRoutingEntry entry = new NRoutingEntry(dst.toPrefixBlock(), nextHope, src, dev,scope);
        this.entries.put(entry.getUid(), entry);
        return entry;
    }
    
    public void printRoutingTable() {
        System.out.println("-------------------------------------------------------");
        AtomicLong idx = new AtomicLong(1L);
        this.entries.forEach((k, v) -> {

            StringBuilder b = new StringBuilder();
            b.append(idx.get()).append(" - ");
            b.append(v.getDst().toPrefixBlock().toString());
            if (v.getDst() != null) {
                b.append(" via ").append(v.getDst().toString());
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
        System.out.println("-------------------------------------------------------");
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
    
    
}
