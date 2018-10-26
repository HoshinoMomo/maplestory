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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;

public class SaveAllCommand implements Command {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        if (splitted[0].equals("!存档")) {
            Collection<ChannelServer> ccs = ChannelServer.getAllInstances();
            for (ChannelServer chan : ccs) {
                mc.dropMessage("Saving characters on channel " + chan.getChannel());
                if (chan != null) {
                    Collection<MapleCharacter> chars = new LinkedHashSet<MapleCharacter>(Collections.synchronizedCollection(chan.getPlayerStorage().getAllCharacters()));
                    synchronized (chars) {
                        for (MapleCharacter chr : chars) {
                            try {
                                if (chr != null) {
                                    chr.saveToDB(true);
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }
                }
            }
            mc.dropMessage("所有人物数据已经存档.");
        }
    }

    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
                    new CommandDefinition("存档", "", "Saves all characters", 3)
                };
    }
}