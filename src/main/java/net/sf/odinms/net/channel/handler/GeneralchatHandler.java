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

package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class GeneralchatHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        String text = slea.readMapleAsciiString();
        int show = slea.readByte();
//MapleCharacter player = null;
        if (c.getPlayer().getCheatTracker().textSpam(text) && !c.getPlayer().isGM()) {
            c.getSession().write(MaplePacketCreator.serverNotice(5, "说话太快了吧！"));
            return;
        }
        if (text.length() > 70 && !c.getPlayer().isGM()) {
            return;
        }
        if (!CommandProcessor.getInstance().processCommand(c, text)) {
            if (c.getPlayer().isMuted() || (c.getPlayer().getMap().getMuted() && !c.getPlayer().isGM())) {
                c.getPlayer().dropMessage(5, c.getPlayer().isMuted() ? "You are " : "The map is " + "muted, therefore you are unable to talk.");
                return;
            }
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, c.getPlayer().hasGMLevel(3) && c.getChannelServer().allowGmWhiteText(), show));
        
        if (c.getPlayer().getVip() == 2) {
        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(5, "┈━═☆[VIP2]" + c.getPlayer().getName() + " : " + text));

      }

      else if (c.getPlayer().getVip() == 3) {
        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(6,"┈━═☆[VIP3]" + c.getPlayer().getName() + " : " + text));

      }
      else if (c.getPlayer().getVip() == 1 )
       c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getWhisper(" ┈━═☆[VIP1] :"+c.getPlayer().getName(),c.getChannel(),text));

      else
       c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, true, show));
        }
    }}
