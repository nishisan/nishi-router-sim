package dev.nishisan.ip.router.ne;

import dev.nishisan.ip.router.ne.NRoutingEntry.NRouteEntryScope;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NRouter {

    private final String name;
    private Map<String, NInterface> interfaces = new ConcurrentHashMap<>();
    private final NRoutingTable mainRouteTable = new NRoutingTable("main", this);

    public NRouter(String name) {
        this.name = name;
    }

    /**
     * Adds a new interface without ip
     *
     * @param name
     * @return
     */
    public NInterface addInterface(String name) {
        NInterface iFace = new NInterface(name, this);
        this.interfaces.put(name, iFace);
        return iFace;
    }

    /**
     * Adds new Network Interface with an IP Address
     *
     * @param name
     * @param address
     * @return
     */
    public NInterface addInterface(String name, String address) {
        NInterface iFace = new NInterface(name, address, this);
        this.interfaces.put(name, iFace);
        this.mainRouteTable.addRouteEntry(iFace.getAddress().toPrefixBlock(), null, iFace.getAddress(), iFace,NRouteEntryScope.link);
        return iFace;
    }
    
    
    public void printRoutingTable(){
        this.mainRouteTable.printRoutingTable();
    }

    public Map<String, NInterface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Map<String, NInterface> interfaces) {
        this.interfaces = interfaces;
    }

    
}
