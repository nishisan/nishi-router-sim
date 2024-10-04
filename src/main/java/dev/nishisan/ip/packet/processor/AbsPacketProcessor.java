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
package dev.nishisan.ip.packet.processor;

import dev.nishisan.ip.packet.OnWireMsg;
import java.util.UUID;

/**
 *
 * @author lucas
 */
public abstract class AbsPacketProcessor<T extends OnWireMsg<T>> implements IPacketProcessor<T> {

    private final String uuid = UUID.randomUUID().toString();

    public String getUuid() {
        return uuid;
    }

}
