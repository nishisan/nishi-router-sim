package dev.nishisan.ip.router;

import dev.nishisan.ip.router.ne.NRouter;

public class NRouterSimApp {

    public static void main(String[] args) {
        NRouter router = new NRouter("router");
        router.addInterface("ge0/0/0/1","192.168.0.1/24");
        router.addInterface("ge0/0/0/2","192.168.1.1/24");
        router.addInterface("ge0/0/0/3","8.8.8.8/24");
        router.printRoutingTable();
    }
}
