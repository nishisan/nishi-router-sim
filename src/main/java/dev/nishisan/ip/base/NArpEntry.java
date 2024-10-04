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
package dev.nishisan.ip.base;

import inet.ipaddr.IPAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 01.10.2024
 */
public class NArpEntry {

    private String macAddress;
    private NBaseInterface iFace;
    private IPAddress ipAddress;
    private AtomicLong expiresIn = new AtomicLong(60L);

    public NArpEntry(String macAddress, NBaseInterface iFace, IPAddress ipAddress) {
        this.macAddress = macAddress;
        this.iFace = iFace;
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public NBaseInterface getiFace() {
        return iFace;
    }

    public void setiFace(NBaseInterface iFace) {
        this.iFace = iFace;
    }

    public IPAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(IPAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public AtomicLong getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(AtomicLong expiresIn) {
        this.expiresIn = expiresIn;
    }

}
