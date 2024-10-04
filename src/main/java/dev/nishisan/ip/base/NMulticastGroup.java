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
package dev.nishisan.ip.base;

import inet.ipaddr.IPAddress;
import java.util.List;

/**
 *
 * @author lucas
 */
public class NMulticastGroup {

    private IPAddress mcastGroup; // 239.1.1.1

    private List<NBaseInterface> subscribers;

    public IPAddress getMcastGroup() {
        return mcastGroup;
    }

    public void setMcastGroup(IPAddress mcastGroup) {
        this.mcastGroup = mcastGroup;
    }

    public List<NBaseInterface> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<NBaseInterface> subscribers) {
        this.subscribers = subscribers;
    }

}
