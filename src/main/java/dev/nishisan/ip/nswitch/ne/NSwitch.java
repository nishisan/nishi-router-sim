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
package dev.nishisan.ip.nswitch.ne;

import dev.nishisan.ip.base.BaseInterface;
import dev.nishisan.ip.base.BaseNe;
import dev.nishisan.ip.base.NLink;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 01.10.2024
 */
public class NSwitch extends BaseNe<NSwitchInterface> {

    private Map<String, NLink> links = Collections.synchronizedMap(new LinkedHashMap());

    public NSwitch(String name) {
        super(name);
    }

    public NSwitchInterface addInterface(String name, String description) {
        NSwitchInterface iFace = new NSwitchInterface(name, description, this);
        this.getInterfaces().put(name, iFace);
        if (iFace.getLink()==null){
            iFace.setOperStatus(BaseInterface.NIfaceOperStatus.OPER_DOWN);
        }
        return iFace;
    }

    public NSwitchInterface addInterface(String name) {
        NSwitchInterface iFace = new NSwitchInterface(name, this);
        this.getInterfaces().put(name, iFace);
        return iFace;
    }

    public NLink connect(BaseInterface src, BaseInterface dst) {
        NLink link = new NLink(src, dst);
        System.out.println("Connection from:[" + src.getNe().getType() + "] to: [" + dst.getNe().getType() + "] Created!");
        this.links.put(src.getMacAddress() + "." + dst.getMacAddress(), link);
        return link;
    }

    @Override
    public String getType() {
        return "SWITCH";
    }

}
