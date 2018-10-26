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
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class NPCTalkHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
c.doneedlog(this, c.getPlayer());
    MapleCharacter player = c.getPlayer();
    player.setCurrenttime(System.currentTimeMillis());
    if (player.getCurrenttime() - player.getLasttime() < player.getDeadtime()) {
      player.dropMessage("找死啊~~~点这么快");
      c.getSession().write(MaplePacketCreator.enableActions());
      return;
    }
    player.setLasttime(System.currentTimeMillis());
        int oid = slea.readInt();
        slea.readInt();
        if (c.getPlayer().getMap().getMapObject(oid) == null || !c.getPlayer().getMap().getMapObject(oid).getType().equals(MapleMapObjectType.NPC)) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        MapleNPC npc = (MapleNPC) c.getPlayer().getMap().getMapObject(oid);
        if (npc.getId() == 9010009) {
            c.getSession().write(MaplePacketCreator.sendDuey((byte) 9, DueyActionHandler.loadItems(c.getPlayer())));
        }
        if (npc.hasShop()) {
            if (c.getPlayer().getShop() != null) {
                c.getPlayer().setShop(null);
                c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 20));
            }
            npc.sendShop(c);
        } else {
            if (c.getCM() != null || c.getQM() != null) {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            if (c.getCM() == null) {
                NPCScriptManager.getInstance().start(c, npc.getId());
            }
        // 0 = next button
        // 1 = yes no
        // 2 = accept decline
        // 5 = select a link
        }
    }
}