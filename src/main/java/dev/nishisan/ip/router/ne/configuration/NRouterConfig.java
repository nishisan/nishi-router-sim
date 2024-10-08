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
package dev.nishisan.ip.router.ne.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import dev.nishisan.ip.router.protocols.configuration.IRoutingProtocolConfiguration;

/**
 *
 * @author lucas
 */
public class NRouterConfig {

    private Map<String, IRoutingProtocolConfiguration> routerProtocolsConfiguration = new ConcurrentHashMap<>();

    private String sysName;

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public Map<String, IRoutingProtocolConfiguration> getRouterProtocolsConfiguration() {
        return routerProtocolsConfiguration;
    }

    public void setRouterProtocolsConfiguration(Map<String, IRoutingProtocolConfiguration> routerProtocolsConfiguration) {
        this.routerProtocolsConfiguration = routerProtocolsConfiguration;
    }

    public void addRouterProtocolConfiguration(IRoutingProtocolConfiguration configuration) {
        this.routerProtocolsConfiguration.put(configuration.getConfigurationType(), configuration);
    }

}
