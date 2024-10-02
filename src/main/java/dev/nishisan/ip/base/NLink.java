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

    private BaseInterface src;
    private BaseInterface dst;
    private Integer vlanId = 0;

    public NLink(BaseInterface src, BaseInterface dst) {
        this.src = src;
        this.dst = dst;
        this.src.setLink(this);
        this.dst.setLink(this);
        
        if (this.src.getAdminStatus().equals(BaseInterface.NIfaceAdminStatus.ADMIN_UP)){
            if (this.dst.getAdminStatus().equals(BaseInterface.NIfaceAdminStatus.ADMIN_UP)){
                this.src.setOperStatus(BaseInterface.NIfaceOperStatus.OPER_UP);
                this.dst.setOperStatus(BaseInterface.NIfaceOperStatus.OPER_UP);
            }
        }
        
    }

    public BaseInterface getSrc() {
        return src;
    }

    public void setSrc(BaseInterface src) {
        this.src = src;
    }

    public BaseInterface getDst() {
        return dst;
    }

    public void setDst(BaseInterface dst) {
        this.dst = dst;
    }

    public BaseInterface getOtherIface(BaseInterface i) {
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

}
