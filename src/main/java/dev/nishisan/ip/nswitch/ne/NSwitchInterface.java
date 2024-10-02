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

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 01.10.2024
 */
public class NSwitchInterface extends BaseInterface {

    private NSwitch nSwitch;

    public NSwitchInterface(String name, NSwitch nSwitch) {
        super(name);
        this.nSwitch = nSwitch;
    }

    public NSwitchInterface(String name, String description, NSwitch nSwitch) {
        super(name);
        this.setDescription(description);
        this.nSwitch = nSwitch;

    }

    public NSwitch getnSwitch() {
        return nSwitch;
    }

    public void setnSwitch(NSwitch nSwitch) {
        this.nSwitch = nSwitch;
    }

}
