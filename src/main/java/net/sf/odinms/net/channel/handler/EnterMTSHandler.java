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

import java.util.*;

import net.sf.odinms.client.inventory.Equip;
import net.sf.odinms.client.inventory.IItem;
import net.sf.odinms.client.inventory.Item;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MTSItemInfo;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnterMTSHandler extends AbstractMaplePacketHandler {

    private static Logger log = LoggerFactory.getLogger(DistributeSPHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getChannelServer().allowMTS()) {
            if (c.getPlayer().getLevel() >= 50) {
                try {
                    WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
                    wci.addBuffsToStorage(c.getPlayer().getId(), c.getPlayer().getAllBuffs());
                } catch (RemoteException e) {
                    c.getChannelServer().reconnectWorld();
                }

                c.getPlayer().getMap().removePlayer(c.getPlayer());
                c.getSession().write(MaplePacketCreator.warpMTS(c));
                c.getPlayer().setInMTS(true);
                c.getSession().write(MaplePacketCreator.MTSWantedListingOver(0, 0));
                c.getSession().write(MaplePacketCreator.showMTSCash(c.getPlayer()));

                List<MTSItemInfo> items = new ArrayList<MTSItemInfo>();
                int pages = 0;
                try {
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM mts_items WHERE tab = 1 AND transfer = 0 ORDER BY id DESC LIMIT ?, 16");
                    ps.setInt(1, 0);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (rs.getInt("type") != 1) {
                            Item i = new Item(rs.getInt("itemid"), (byte) 0, (short) rs.getInt("quantity"));
                            i.setOwner(rs.getString("owner"));
                            items.add(new MTSItemInfo(i, rs.getInt("price") + 100 + (int) (rs.getInt("price") * 0.1), rs.getInt("id"), rs.getInt("seller"), rs.getString("sellername"), rs.getString("sell_ends")));
                        } else {
                            Equip equip = new Equip(rs.getInt("itemid"), (byte) rs.getInt("position"), false);
                            equip.setOwner(rs.getString("owner"));
                            equip.setQuantity((short) 1);
                            equip.setAcc((short) rs.getInt("acc"));
                            equip.setAvoid((short) rs.getInt("avoid"));
                            equip.setDex((short) rs.getInt("dex"));
                            equip.setHands((short) rs.getInt("hands"));
                            equip.setHp((short) rs.getInt("hp"));
                            equip.setInt((short) rs.getInt("int"));
                            equip.setJump((short) rs.getInt("jump"));
                            equip.setLuk((short) rs.getInt("luk"));
                            equip.setMatk((short) rs.getInt("matk"));
                            equip.setMdef((short) rs.getInt("mdef"));
                            equip.setMp((short) rs.getInt("mp"));
                            equip.setSpeed((short) rs.getInt("speed"));
                            equip.setStr((short) rs.getInt("str"));
                            equip.setWatk((short) rs.getInt("watk"));
                            equip.setWdef((short) rs.getInt("wdef"));
                            equip.setUpgradeSlots((byte) rs.getInt("upgradeslots"));
                            equip.setLocked((byte) rs.getInt("locked"));
                            equip.setLevel((byte) rs.getInt("level"));
                            items.add(new MTSItemInfo((IItem) equip, rs.getInt("price") + 100 + (int) (rs.getInt("price") * 0.1), rs.getInt("id"), rs.getInt("seller"), rs.getString("sellername"), rs.getString("sell_ends")));
                        }
                    }
                    rs.close();
                    ps.close();

                    ps = con.prepareStatement("SELECT COUNT(*) FROM mts_items");
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        pages = (int) Math.ceil(rs.getInt(1) / 16);
                    }
                    rs.close();
                    ps.close();
                } catch (SQLException e) {
                    log.error("Err1: " + e);
                }

                c.getSession().write(MaplePacketCreator.sendMTS(items, 1, 0, 0, pages));
                c.getSession().write(MaplePacketCreator.TransferInventory(getTransfer(c.getPlayer().getId())));
                c.getSession().write(MaplePacketCreator.NotYetSoldInv(getNotYetSold(c.getPlayer().getId())));
                c.getPlayer().saveToDB(true);
            } else {
                c.getSession().write(MaplePacketCreator.serverNotice(5, "目前无法进入，请玩家稍后再试.(电击象服务器目前不开放拍卖平台)"));
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        } else {
            NPCScriptManager.getInstance().start(c, 22000);
            c.getSession().write(MaplePacketCreator.serverNotice(6, "欢迎来到自由市场 目前无法进入拍卖平台，请玩家稍后再试.(电击象服务器目前不开放拍卖平台)"));
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    public List<MTSItemInfo> getNotYetSold(int cid) {
        List<MTSItemInfo> items = new ArrayList<MTSItemInfo>();
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = con.prepareStatement("SELECT * FROM mts_items WHERE seller = ? AND transfer = 0 ORDER BY id DESC");
            ps.setInt(1, cid);

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("type") != 1) {
                    Item i = new Item(rs.getInt("itemid"), (byte) 0, (short) rs.getInt("quantity"));
                    i.setOwner(rs.getString("owner"));
                    items.add(new MTSItemInfo((IItem) i, rs.getInt("price"), rs.getInt("id"), rs.getInt("seller"), rs.getString("sellername"), rs.getString("sell_ends")));
                } else {
                    Equip equip = new Equip(rs.getInt("itemid"), (byte) rs.getInt("position"), false);
                    equip.setOwner(rs.getString("owner"));
                    equip.setQuantity((short) 1);
                    equip.setAcc((short) rs.getInt("acc"));
                    equip.setAvoid((short) rs.getInt("avoid"));
                    equip.setDex((short) rs.getInt("dex"));
                    equip.setHands((short) rs.getInt("hands"));
                    equip.setHp((short) rs.getInt("hp"));
                    equip.setInt((short) rs.getInt("int"));
                    equip.setJump((short) rs.getInt("jump"));
                    equip.setLuk((short) rs.getInt("luk"));
                    equip.setMatk((short) rs.getInt("matk"));
                    equip.setMdef((short) rs.getInt("mdef"));
                    equip.setMp((short) rs.getInt("mp"));
                    equip.setSpeed((short) rs.getInt("speed"));
                    equip.setStr((short) rs.getInt("str"));
                    equip.setWatk((short) rs.getInt("watk"));
                    equip.setWdef((short) rs.getInt("wdef"));
                    equip.setUpgradeSlots((byte) rs.getInt("upgradeslots"));
                    equip.setLocked((byte) rs.getInt("locked"));
                    equip.setLevel((byte) rs.getInt("level"));
                    items.add(new MTSItemInfo((IItem) equip, rs.getInt("price"), rs.getInt("id"), rs.getInt("seller"), rs.getString("sellername"), rs.getString("sell_ends")));
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            log.error("Err8: " + e);
        }
        return items;
    }

    public List<MTSItemInfo> getTransfer(int cid) {
        List<MTSItemInfo> items = new ArrayList<MTSItemInfo>();
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = con.prepareStatement("SELECT * FROM mts_items WHERE transfer = 1 AND seller = ? ORDER BY id DESC");
            ps.setInt(1, cid);

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("type") != 1) {
                    Item i = new Item(rs.getInt("itemid"), (byte) 0, (short) rs.getInt("quantity"));
                    i.setOwner(rs.getString("owner"));
                    items.add(new MTSItemInfo((IItem) i, rs.getInt("price"), rs.getInt("id"), rs.getInt("seller"), rs.getString("sellername"), rs.getString("sell_ends")));
                } else {
                    Equip equip = new Equip(rs.getInt("itemid"), (byte) rs.getInt("position"), false);
                    equip.setOwner(rs.getString("owner"));
                    equip.setQuantity((short) 1);
                    equip.setAcc((short) rs.getInt("acc"));
                    equip.setAvoid((short) rs.getInt("avoid"));
                    equip.setDex((short) rs.getInt("dex"));
                    equip.setHands((short) rs.getInt("hands"));
                    equip.setHp((short) rs.getInt("hp"));
                    equip.setInt((short) rs.getInt("int"));
                    equip.setJump((short) rs.getInt("jump"));
                    equip.setLuk((short) rs.getInt("luk"));
                    equip.setMatk((short) rs.getInt("matk"));
                    equip.setMdef((short) rs.getInt("mdef"));
                    equip.setMp((short) rs.getInt("mp"));
                    equip.setSpeed((short) rs.getInt("speed"));
                    equip.setStr((short) rs.getInt("str"));
                    equip.setWatk((short) rs.getInt("watk"));
                    equip.setWdef((short) rs.getInt("wdef"));
                    equip.setUpgradeSlots((byte) rs.getInt("upgradeslots"));
                    equip.setLocked((byte) rs.getInt("locked"));
                    equip.setLevel((byte) rs.getInt("level"));
                    items.add(new MTSItemInfo((IItem) equip, rs.getInt("price"), rs.getInt("id"), rs.getInt("seller"), rs.getString("sellername"), rs.getString("sell_ends")));
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            log.error("Err7: " + e);
        }
        return items;
    }
}