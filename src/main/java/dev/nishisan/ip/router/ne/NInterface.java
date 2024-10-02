package dev.nishisan.ip.router.ne;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

import java.util.Random;

public class NInterface {

    private String name;
    private String macAddress;
    private IPAddress address;
    private NRouter router;
    private NIfaceOperStatus operStatus = NIfaceOperStatus.OPER_UP;
    private NIfaceAdminStatus adminStatus = NIfaceAdminStatus.ADMIN_UP;

    public enum NIfaceOperStatus {
        OPER_UP,
        OPER_DOWN
    }

    public enum NIfaceAdminStatus {
        ADMIN_UP,
        ADMIN_DOWN
    }

    public NInterface(String name, String address) {
        this.name = name;

        this.address = new IPAddressString(address).getAddress();
        this.macAddress = NInterface.generateMacAddress();
    }

    public NInterface(String name, String address, NRouter router) {
        this.name = name;
        this.address = new IPAddressString(address).getAddress();
        this.router = router;
        this.macAddress = NInterface.generateMacAddress();
    }

    public NInterface(String name, NRouter router) {
        this.name = name;
        this.router = router;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public IPAddress getAddress() {
        return address;
    }

    public void setAddress(IPAddress address) {
        this.address = address;
    }

    public NRouter getRouter() {
        return router;
    }

    public void setRouter(NRouter router) {
        this.router = router;
    }

    public static String generateMacAddress() {
        Random random = new Random();
        byte[] macAddr = new byte[6];
        random.nextBytes(macAddr);
        macAddr[0] = (byte) (macAddr[0] & (byte) 254);
        StringBuilder macAddressBuilder = new StringBuilder();
        for (int i = 0; i < macAddr.length; i++) {
            macAddressBuilder.append(String.format("%02X", macAddr[i]));
            if (i < macAddr.length - 1) {
                macAddressBuilder.append(":");
            }
        }
        return macAddressBuilder.toString();
    }

    public NIfaceOperStatus getOperStatus() {
        return operStatus;
    }

    public void setOperStatus(NIfaceOperStatus operStatus) {
        this.operStatus = operStatus;
    }

    public NIfaceAdminStatus getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(NIfaceAdminStatus adminStatus) {
        this.adminStatus = adminStatus;
    }

}
