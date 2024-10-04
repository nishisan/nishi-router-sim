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

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 02.10.2024
 */
public class OnWireMsg<T extends OnWireMsg<T>> implements Serializable {

    private String uid = UUID.randomUUID().toString();

    private Integer vlanId = 0;
    private Consumer<T> replyCallback;
    private Map<String, BaseInterface> walked = new ConcurrentHashMap<>();

    public String getUid() {
        return uid;
    }

    public void onReply(Consumer<T> callback) {
        this.replyCallback = callback;
    }

    public void reply(T response) {
        if (this.replyCallback != null) {
            this.replyCallback.accept(response);
        }
    }

    @Override
    public String toString() {
        return "ZeroLayerMsg{" + "uid=" + uid + '}';
    }

    public Integer getVlanId() {
        return vlanId;
    }

    public void setVlanId(Integer vlanId) {
        this.vlanId = vlanId;
    }

    public Consumer<T> getReplyCallback() {
        return replyCallback;
    }

    public void setReplyCallback(Consumer<T> replyCallback) {
        this.replyCallback = replyCallback;
    }

    public Boolean walked(BaseInterface i) {
        return this.walked.containsKey(i.getUid());
    }

    public void notifyWalk(BaseInterface i) {
        this.walked.put(i.getUid(), i);
    }

}
