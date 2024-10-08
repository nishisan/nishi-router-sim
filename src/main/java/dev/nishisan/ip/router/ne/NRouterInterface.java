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

import dev.nishisan.ip.base.BaseInterface;
import dev.nishisan.ip.base.BroadCastDomain;
import inet.ipaddr.IPAddressString;

public class NRouterInterface extends BaseInterface {

    private NRouter router;

    public NRouterInterface(String name, NRouter router, BroadCastDomain broadCastDomain) {
        super(name, router, broadCastDomain);
        this.router = router;
    }

    public NRouterInterface(String name, String address, NRouter router, BroadCastDomain broadCastDomain) {
        super(name, router, broadCastDomain);
        this.setAddress(new IPAddressString(address).getAddress());
        this.router = router;
    }

    public NRouter getRouter() {
        return router;
    }

    public void setRouter(NRouter router) {
        this.router = router;
    }

}
