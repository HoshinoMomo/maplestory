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
import net.sf.odinms.client.inventory.MapleInventory;
import net.sf.odinms.client.inventory.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Matze
 */
public class ItemMoveHandler extends AbstractMaplePacketHandler {

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int actionId = slea.readInt();
        if (actionId <= c.getLastActionId()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        c.setLastActionId(actionId);
        MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
        MapleInventory inventory = c.getPlayer().getInventory(type);
        byte src = (byte) slea.readShort();
        byte dst = (byte) slea.readShort();
        short quantity = slea.readShort();
        if (src < 0 && dst > 0) {
  MapleCharacter player = c.getPlayer();
      player.setCurrenttime(System.currentTimeMillis());
      if (player.getCurrenttime() - player.getLasttime() < player.getDeadtime()) {
        player.dropMessage("找死啊~~~点这么快");
        c.getSession().write(MaplePacketCreator.enableActions());
        return;
      }
            MapleInventoryManipulator.unequip(c, src, dst);
        } else if (dst < 0) {
            MapleInventoryManipulator.equip(c, src, dst);
        } else if (inventory.getItem(src).getItemId() == 5110000) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        } else if (dst == 0) {
            if (quantity < 0) {
                c.getPlayer().dropMessage(1, "Either you're Tryst, or you're a hacker. So GT*O here.\r\n(P.S. If you're Tryst, I ask you to **** OFF. Either way, you're a hacker.)");
                try {
                    c.getChannelServer().getWorldInterface().broadcastGMMessage(c.getPlayer().getName(), MaplePacketCreator.serverNotice(0, "Duper alert: " + c.getPlayer().getName() + " is dropping negative amount of items.").getBytes());
                } catch (Throwable u) {
                }
                return;
            }
            if (c.getPlayer().getInventory(type).getItem(src) == null) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            MapleInventoryManipulator.drop(c, type, src, quantity);
        } else {
            MapleInventoryManipulator.move(c, type, src, dst);
        }
    }
}