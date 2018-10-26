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

package net.sf.odinms.client.messages.commands;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;

public class OnlineCommands implements Command {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, RemoteException {
        if (splitted[0].toLowerCase().equals("!人数")) {
            mc.dropMessage("在线人物: ");
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                mc.dropMessage("[频道 " + cs.getChannel() + "]");
                StringBuilder sb = new StringBuilder();
                Collection<MapleCharacter> cmc = cs.getPlayerStorage().getAllCharacters();
                for (MapleCharacter chr : cmc) {
                    if (sb.length() > 150) {
                        sb.setLength(sb.length() - 2);
                        mc.dropMessage(sb.toString());
                        sb = new StringBuilder();
                    }
                    if (!chr.isGM()) {
                        sb.append(MapleCharacterUtil.makeMapleReadable("ID:" + chr.getId() + "Name:" + chr.getName()));
                        sb.append(", ");
                    }
                }
                if (sb.length() >= 2) {
                    sb.setLength(sb.length() - 2);
                    mc.dropMessage(sb.toString());
                }
            }
        } else if (splitted[0].equalsIgnoreCase("!gm人数")) {
            try {
                mc.dropMessage("在线管理员: " + c.getChannelServer().getWorldInterface().listGMs());
            } catch (RemoteException re) {
            }
        } else if (splitted[0].equalsIgnoreCase("!连接数量")) {
            try {
                Map<Integer, Integer> connected = c.getChannelServer().getWorldInterface().getConnected();
                StringBuilder conStr = new StringBuilder("连接数量: ");
                boolean first = true;
                for (int i : connected.keySet()) {
                    if (!first) {
                        conStr.append(", ");
                    } else {
                        first = false;
                    }
                    if (i == 0) {
                        conStr.append("总计: ");
                        conStr.append(connected.get(i));
                    } else {
                        conStr.append("频道 ");
                        conStr.append(i);
                        conStr.append(": ");
                        conStr.append(connected.get(i));
                    }
                }
                mc.dropMessage(conStr.toString());
            } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
        }
    }

    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
                    new CommandDefinition("人数", "", "List all of the users on the server, organized by channel.", 5),
                    new CommandDefinition("channel", "", "List all characters online on a channel.", 5),
                    new CommandDefinition("gm人数", "", "Shows the name of every GM that is online", 5),
                    new CommandDefinition("连接数量", "", "Shows how many players are connected on each channel", 5)
                };
    }
}