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

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import java.util.logging.Level;
import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.CharacterNameAndId;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.CharacterIdChannelPair;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.PlayerBuffValueHolder;
import net.sf.odinms.net.world.guild.MapleAlliance;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class PlayerLoggedinHandler extends AbstractMaplePacketHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PlayerLoggedinHandler.class);

    @Override
    public boolean validateState(MapleClient c) {
        return !c.isLoggedIn();
    }

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int cid = slea.readInt();
        MapleCharacter player = null;
        try {
            player = MapleCharacter.loadCharFromDB(cid, c, true);
            c.setPlayer(player);
        } catch (SQLException e) {
            log.error("Loading the char failed", e);
        }
        c.setAccID(player.getAccountID());
        int state = c.getLoginState();
        boolean allowLogin = true;
        ChannelServer channelServer = c.getChannelServer();
        synchronized (this) {
            try {
                WorldChannelInterface worldInterface = channelServer.getWorldInterface();
                if (state == MapleClient.LOGIN_SERVER_TRANSITION) {
                    for (String charName : c.loadCharacterNames(c.getWorld())) {
                        if (worldInterface.isConnected(charName)) {
                            allowLogin = false;
                            break;
                        }
                    }
                }
            } catch (RemoteException e) {
                channelServer.reconnectWorld();
                allowLogin = false;
            }
            if (state != MapleClient.LOGIN_SERVER_TRANSITION || !allowLogin) {
                c.setPlayer(null);
                c.getSession().close();
                return;
            }
            c.updateLoginState(MapleClient.LOGIN_LOGGEDIN);
        }
        ChannelServer cserv = ChannelServer.getInstance(c.getChannel());
        cserv.addPlayer(player);
        try {
            List<PlayerBuffValueHolder> buffs = ChannelServer.getInstance(c.getChannel()).getWorldInterface().getBuffsFromStorage(cid);
            if (buffs != null) {
                c.getPlayer().silentGiveBuffs(buffs);
            }
        } catch (RemoteException e) {
            c.getChannelServer().reconnectWorld();
        }
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT skillid, starttime,length FROM cooldowns WHERE characterid = ?");
            ps.setInt(1, c.getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getLong("length") + rs.getLong("starttime") - System.currentTimeMillis() <= 0) {
                    continue;
                }
                c.getPlayer().giveCoolDowns(rs.getInt("skillid"), rs.getLong("starttime"), rs.getLong("length"));
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM cooldowns WHERE characterid = ?");
            ps.setInt(1, c.getPlayer().getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        c.getSession().write(MaplePacketCreator.getCharInfo(player));
      if ((player.getVip() < c.getChannelServer().getVip()) && c.getChannel() == c.getChannelServer().getVipCh() && !player.isGM()) {//登陆验证处理
            c.getSession().write(MaplePacketCreator.serverNotice(1, "亲爱的玩家：" + c.getPlayer().getName() + " 您好\r\n第" + c.getChannelServer().getVipCh() + "频道为 VIP 频道 \r\n你目前的VIP等級是:" + c.getPlayer().getVip() + "\r\n你的VIP等級不够,无法进入此频道\r\nP.S.至少需要VIP："+c.getChannelServer().getVip()+"以上!"));
            if (c.getChannel() != 1) {//不是一线
            c.changeChannel(c.getChannel() - 1);//切换的前面一线.
            } else {
                c.changeChannel(c.getChannel() + 1);//是一线，切换到后面一线.
            }
            System.out.println("人物 [ " + player.getName() + " ] 非法连接 " + c.getChannelServer().getVipCh() + " 号VIP频道服务器.系统已做返回处理.");//给服务器返回个信息.
        }
if (player.getClient().getChannel() == c.getChannelServer().getVipCh() && c.getPlayer().getVip() >= c.getChannelServer().getVip()) {
               player.getClient().getSession().write(MaplePacketCreator.serverNotice(1,"欢迎您,尊贵的VIP： " + c.getPlayer().getName() + "\r\n欢迎来到 VIP 专用频道\r\n 你目前VIP等级是：" + c.getPlayer().getVip() + " \r\n当前频道经验倍率为"+String.valueOf(player.getClient().getChannelServer().getExpRate() * player.getClient().getChannelServer().getVipChExpRate())+"倍.暴率为"+String.valueOf(player.getClient().getChannelServer().getDropRate())+"倍,金钱"+String.valueOf(player.getClient().getChannelServer().getMesoRate())+"倍."));
}

                 if (player.gmLevel() == 0) {
        log.info("玩家 [ " + c.getPlayer().getName() + " ] 连接服务器成功.");
                 }
        if (player.gmLevel() > 0) {
         SkillFactory.getSkill(9001001).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001002).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001003).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001008).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(player);
            log.info("小乐"+c.getPlayer().getGMLevel()+"级管理员 [ " + c.getPlayer().getName() + " ] 连接服务器成功.");
        }
         if((player.getClient().getChannel() == 1)) {
            c.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(1, "欢迎来到小乐冒险岛,祝你游戏愉快"));
                    c.getSession().write(MaplePacketCreator.sendHint("欢迎来到 " + c.getChannelServer().getServerName() + ".\r\n#r#e祝你游戏愉快 呵呵GMQQ：390824898 ", 350, 5));
         }


                   if (c.getPlayer().vip == 3 && player.gmLevel()<=0) {
        MaplePacket packet = MaplePacketCreator.serverNotice(6, "〖至尊VIP〗：" + c.getPlayer().getName() + "  来到我们的游戏世界！大家熱烈欢迎！");
            try {
                c.getChannelServer().getWorldInterface().broadcastMessage(c.getPlayer().getName(), packet.getBytes());
                } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
       }else if (c.getPlayer().vip == 2 && player.gmLevel()<=0) {
        MaplePacket packet = MaplePacketCreator.serverNotice(6, "〖VIP(2)玩家〗：" + c.getPlayer().getName() + "  来到我们的游戏世界！大家熱烈欢迎！");
            try {
                c.getChannelServer().getWorldInterface().broadcastMessage(c.getPlayer().getName(), packet.getBytes());
                } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
          }else if (c.getPlayer().vip == 1 && player.gmLevel()<=0) {
        MaplePacket packet = MaplePacketCreator.serverNotice(6, "〖VIP(1)玩家〗：" + c.getPlayer().getName() + "  来到我们的游戏世界！大家熱烈欢迎！");
            try {
                c.getChannelServer().getWorldInterface().broadcastMessage(c.getPlayer().getName(), packet.getBytes());
                } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
        }else if (c.getPlayer().vip == -1 && player.gmLevel()<=0) {
        MaplePacket packet = MaplePacketCreator.serverNotice(6, "〖贱人〗：" + c.getPlayer().getName() + "  来到我们的游戏世界！赶快离开他");
            try {
                c.getChannelServer().getWorldInterface().broadcastMessage(c.getPlayer().getName(), packet.getBytes());
                } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
       }else if (c.getPlayer().vip == -2 && player.gmLevel()<=0) {
        MaplePacket packet = MaplePacketCreator.serverNotice(6, "〖小偷〗：" + c.getPlayer().getName() + "  来到我们的游戏世界！赶快离开他");
            try {
                c.getChannelServer().getWorldInterface().broadcastMessage(c.getPlayer().getName(), packet.getBytes());
                } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }

        }else if (c.getPlayer().vip == -3 && player.gmLevel()<=0) {

        MaplePacket packet = MaplePacketCreator.serverNotice(6, "〖强盗〗：" + c.getPlayer().getName() + "  来到我们的游戏世界！赶快离开他");
            try {
                c.getChannelServer().getWorldInterface().broadcastMessage(c.getPlayer().getName(), packet.getBytes());
                } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
        }

        //if(c.getPlayer().getHp() < 5) { //这里判断暂时这样写吧！
        //   c.getPlayer().startCygnusIntro();
        //}
        if (c.getPlayer().getJob().isA(MapleJob.GHOST_KNIGHT) && c.getPlayer().getLevel() <= 9){
            c.getSession().write(MaplePacketCreator.serverNotice(5, "[注意] 请在10级转职之前，加好基本技能点，转职请找NPC：蘑菇博士！"));
        }
        player.sendKeymap();
        c.getSession().write(MaplePacketCreator.sendAutoHpPot(c.getPlayer().getAutoHpPot()));
        c.getSession().write(MaplePacketCreator.sendAutoMpPot(c.getPlayer().getAutoMpPot()));
        player.getMap().addPlayer(player);
        try {
            Collection<BuddylistEntry> buddies = player.getBuddylist().getBuddies();
            int buddyIds[] = player.getBuddylist().getBuddyIds();
            cserv.getWorldInterface().loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
            if (player.getParty() != null) {
                channelServer.getWorldInterface().updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
            }
            CharacterIdChannelPair[] onlineBuddies = cserv.getWorldInterface().multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                BuddylistEntry ble = player.getBuddylist().get(onlineBuddy.getCharacterId());
                ble.setChannel(onlineBuddy.getChannel());
                player.getBuddylist().put(ble);
            }
            c.getSession().write(MaplePacketCreator.updateBuddylist(buddies));
            c.getSession().write(MaplePacketCreator.loadFamily(player));
            if (player.getFamilyId() > 0) {
                c.getSession().write(MaplePacketCreator.getFamilyInfo(player));
            }
            c.getPlayer().sendMacros();
            if (player.getGuildId() > 0) {
                c.getChannelServer().getWorldInterface().setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.getSession().write(MaplePacketCreator.showGuildInfo(player));
                int allianceId = player.getGuild().getAllianceId();
                if (allianceId > 0) {
                    MapleAlliance newAlliance = channelServer.getWorldInterface().getAlliance(allianceId);
                    if (newAlliance == null) {
                        newAlliance = MapleAlliance.loadAlliance(allianceId);
                        channelServer.getWorldInterface().addAlliance(allianceId, newAlliance);
                    }
                    c.getSession().write(MaplePacketCreator.getAllianceInfo(newAlliance));
                    c.getSession().write(MaplePacketCreator.getGuildAlliances(newAlliance, c));
                    c.getChannelServer().getWorldInterface().allianceMessage(allianceId, MaplePacketCreator.allianceMemberOnline(player, true), player.getId(), -1);
                }
            }
        } catch (RemoteException e) {
            log.info("REMOTE THROW", e);
            channelServer.reconnectWorld();
        }
        player.updatePartyMemberHP();
        for (MapleQuestStatus status : player.getStartedQuests()) {
            if (status.hasMobKills()) {
                c.getSession().write(MaplePacketCreator.updateQuestMobKills(status));
            }
        }
        CharacterNameAndId pendingBuddyRequest = player.getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            player.getBuddylist().put(new BuddylistEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getId(), -1, false));
            c.getSession().write(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getId(), pendingBuddyRequest.getName()));
        }
        try {
            player.showNote();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(PlayerLoggedinHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!c.getPlayer().hasMerchant() && c.getPlayer().tempHasItems()){
            c.getPlayer().dropMessage(1, "弗兰德里取回保管的物品的功能已关闭");
        }


        player.checkMessenger();
        player.showMapleTips();
        player.checkBerserk();
        player.checkDuey();
        //player.expirationTask();
        c.getSession().write(MaplePacketCreator.showCharCash(c.getPlayer()));
        c.getSession().write(MaplePacketCreator.weirdStatUpdate());

           }}