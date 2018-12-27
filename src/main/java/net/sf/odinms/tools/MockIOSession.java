/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.odinms.tools;

import java.net.SocketAddress;

import net.sf.odinms.net.MaplePacket;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.write.WriteRequest;


/**
 * Represents a mock version of an IOSession to use a MapleClient instance
 * without an active connection (faekchar, etc).
 * 
 * Most methods return void, or when they return something, null. Therefore,
 * this class is mostly undocumented, due to the fact that each and every
 * function does squat.
 *
 * 这个类暂时没啥用
 *
 * @author Frz
 * @since Revision 518
 * @version 1.0
 */
public class MockIOSession extends DummySession {

    /**
     * Does nothing.
     */
    @Override
    public IoSessionConfig getConfig() {
        return null;
    }

    /**
     * Does nothing.
     */
    @Override
    public IoFilterChain getFilterChain() {
        return null;
    }

    /**
     * Does nothing.
     */
    @Override
    public IoHandler getHandler() {
        return null;
    }

    /**
     * Does nothing.
     */
    @Override
    public SocketAddress getLocalAddress() {
        return null;
    }

    /**
     * Does nothing.
     */
    @Override
    public SocketAddress getRemoteAddress() {
        return null;
    }

    /**
     * Does nothing.
     */
    @Override
    public IoService getService() {
        return null;
    }

    /**
     * Does nothing.
     */
    @Override
    public SocketAddress getServiceAddress() {
        return null;
    }

    /**
     * Does nothing.
     */
    @Override
    public WriteFuture write(Object message, SocketAddress remoteAddress) {
        return null;
    }

    /**
     * "Fake writes" a packet to the client, only running the OnSend event of
     * the packet.
     */
    @Override
    public WriteFuture write(Object message) {
        if (message instanceof MaplePacket) {
            MaplePacket mp = (MaplePacket) message;
            if (mp.getOnSend() != null) {
                mp.getOnSend().run();
            }
        }
        return null;
    }

}
