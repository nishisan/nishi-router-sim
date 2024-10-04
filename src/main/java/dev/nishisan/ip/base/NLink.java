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

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 02.10.2024
 */
public class NLink {

    private NBaseInterface src;
    private NBaseInterface dst;
    private Integer vlanId = 0;
    private Integer latency = 0;
    private Integer jitter = 0;

    public NLink(NBaseInterface src, NBaseInterface dst) {
        this.src = src;
        this.dst = dst;
        this.src.setLink(this);
        this.dst.setLink(this);

        if (this.src.getAdminStatus().equals(NBaseInterface.NIfaceAdminStatus.ADMIN_UP)) {
            if (this.dst.getAdminStatus().equals(NBaseInterface.NIfaceAdminStatus.ADMIN_UP)) {
                this.src.setOperStatus(NBaseInterface.NIfaceOperStatus.OPER_UP);
                this.dst.setOperStatus(NBaseInterface.NIfaceOperStatus.OPER_UP);
            }
        }

    }

    public NLink(NBaseInterface src, NBaseInterface dst, Integer latency, Integer jitter) {
        this.src = src;
        this.dst = dst;
        
        this.src.setLink(this);
        this.dst.setLink(this);

        if (this.src.getAdminStatus().equals(NBaseInterface.NIfaceAdminStatus.ADMIN_UP)) {
            if (this.dst.getAdminStatus().equals(NBaseInterface.NIfaceAdminStatus.ADMIN_UP)) {
                this.src.setOperStatus(NBaseInterface.NIfaceOperStatus.OPER_UP);
                this.dst.setOperStatus(NBaseInterface.NIfaceOperStatus.OPER_UP);
            }
        }

    }

    public NBaseInterface getSrc() {
        return src;
    }

    public void setSrc(NBaseInterface src) {
        this.src = src;
    }

    public NBaseInterface getDst() {
        return dst;
    }

    public void setDst(NBaseInterface dst) {
        this.dst = dst;
    }

    public NBaseInterface getOtherIface(NBaseInterface i) {
        if (this.src.equals(i)) {
            return this.dst;
        } else {
            return this.src;
        }
    }

    public Integer getVlanId() {
        return vlanId;
    }

    public void setVlanId(Integer vlanId) {
        this.vlanId = vlanId;
    }

    public Integer getLatency() {
        return latency;
    }

    public NLink setLatency(Integer latency) {
        this.latency = latency;
        return this;
    }

    public Integer getJitter() {
        return jitter;
    }

    public NLink setJitter(Integer jitter) {
        this.jitter = jitter;
        return this;
    }

}
