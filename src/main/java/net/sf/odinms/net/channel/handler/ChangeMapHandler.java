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

import java.net.InetAddress;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.inventory.MapleInventoryType;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class ChangeMapHandler extends AbstractMaplePacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChangeMapHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (slea.available() == 0) {
            if (c.getPlayer().getParty() != null) {
                c.getPlayer().setParty(c.getPlayer().getParty());
            }
            String ip = ChannelServer.getInstance(c.getChannel()).getIP(c.getChannel());
            String[] socket = ip.split(":");
            c.getPlayer().saveToDB(true);
            c.getPlayer().setInCS(false);
            c.getPlayer().setInMTS(false);
            c.getPlayer().cancelSavedBuffs();
            ChannelServer.getInstance(c.getChannel()).removePlayer(c.getPlayer());
            c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
            try {
                c.getSession().write(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
                c.getSession().close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            slea.readByte(); // 1 = from dying 2 = regular portals
            int targetid = slea.readInt(); // FF FF FF FF

            String startwp = slea.readMapleAsciiString();
            MaplePortal portal = c.getPlayer().getMap().getPortal(startwp);

            MapleCharacter player = c.getPlayer();
            if (targetid != -1 && !c.getPlayer().isAlive()) {
                boolean executeStandardPath = true;
                if (player.getEventInstance() != null) {
                    executeStandardPath = player.getEventInstance().revivePlayer(player);
                }
                if (executeStandardPath) {
                    if (c.getPlayer().haveItem(5510000, 1, false, true)) {
                        c.getPlayer().setHp(50);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5510000, 1, true, false);
                        c.getPlayer().changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().getPortal(0));
                        c.getPlayer().updateSingleStat(MapleStat.HP, 50);
                        c.getSession().write(MaplePacketCreator.serverNotice(5, "使用了原地复活术。死亡后您在当前地图复活。"));
                    } else {
                        player.setHp(50);
                        if (c.getPlayer().getMap().getForcedReturnId() != 999999999) {
                            MapleMap to = c.getPlayer().getMap().getForcedReturnMap();
                            MaplePortal pto = to.getPortal(0);
                            player.setStance(0);
                            player.changeMap(to, pto);
                        } else {
                            MapleMap to = c.getPlayer().getMap().getReturnMap();
                            MaplePortal pto = to.getPortal(0);
                            player.setStance(0);
                            player.changeMap(to, pto);
                        }
                    }
                }
           } else if (targetid != -1 && !c.getPlayer().isGM()) {
AutobanManager.getInstance().broadcastMessage(player.getClient(),"公告："+ player.getName() + " 恶意使用BUG，先做出处罚。请其他玩家自重");
log.warn("玩家 {} 因非法传送地图被处罚！", c.getPlayer().getName());
c.getPlayer().changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().getPortal(0)); //使传送无效。
c.getPlayer().updateSingleStat(MapleStat.HP, 1); //降低玩家HP 到 10点
c.getPlayer().updateSingleStat(MapleStat.MP, 0); //降低玩家MP 到 0点
c.getPlayer().updateSingleStat(MapleStat.MESO, 0);//降低玩家金钱 到 0点
c.getPlayer().updateSingleStat(MapleStat.EXP, -210000000);//降低玩家经验 到 -210000000点
            } else {
                if (portal != null) {
                    portal.enterPortal(c);
                } else {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    log.warn("Portal {} not found on map {}", startwp, c.getPlayer().getMap().getId());
                }
            }
        }
    }
}