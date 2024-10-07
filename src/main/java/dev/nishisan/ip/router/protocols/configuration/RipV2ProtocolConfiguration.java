/*
 * Copyright (C) 2024 lucas
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
package dev.nishisan.ip.router.protocols.configuration;

import dev.nishisan.ip.base.NBaseInterface;
import dev.nishisan.ip.router.ne.NRoutingEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author lucas
 */
public class RipV2ProtocolConfiguration extends RoutingProtocolConfiguration {

    private Boolean enabled = false;
    private Map<String, NBaseInterface> passiveInterface = new ConcurrentHashMap<>();
    private Map<String, NRoutingEntry> networks = new ConcurrentHashMap<>();
    public static String type = "RIPV2";

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, NBaseInterface> getPassiveInterface() {
        return passiveInterface;
    }

    public void setPassiveInterface(Map<String, NBaseInterface> passiveInterface) {
        this.passiveInterface = passiveInterface;
    }

    @Override
    public String getConfigurationType() {
        return RipV2ProtocolConfiguration.type;
    }

    public void addPassiveInterface(NBaseInterface iFace) {
        if (!this.passiveInterface.containsKey(iFace.getUid())) {
            this.passiveInterface.put(iFace.getUid(), iFace);
        }
    }

    public void addNetwork(NRoutingEntry routeEntry) {
        this.networks.put(routeEntry.getUid(), routeEntry);
    }

    public void addNetworks(List<NRoutingEntry> entries) {
        entries.forEach(e -> {
            this.networks.put(e.getUid(), e);
        });
    }

    public Map<String, NRoutingEntry> getNetworks() {
        return networks;
    }

    public List<NRoutingEntry> getNetworksAsList() {
        return new ArrayList<>(networks.values());
    }

}
