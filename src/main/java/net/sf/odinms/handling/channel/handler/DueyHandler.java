/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
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
package net.sf.odinms.handling.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.inventory.IItem;
import net.sf.odinms.client.inventory.ItemFlag;
import net.sf.odinms.client.inventory.ItemLoader;
import net.sf.odinms.client.inventory.MapleInventoryType;
import net.sf.odinms.constants.GameConstants;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.server.MapleDueyActions;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DueyHandler {

    /*
     * 19 = Successful
     * 18 = One-of-a-kind Item is already in Reciever's delivery
     * 17 = The Character is unable to recieve the parcel
     * 15 = Same account
     * 14 = Name does not exist
     */
    public static final void DueyOperation(final SeekableLittleEndianAccessor slea, final MapleClient c) {

        final byte operation = slea.readByte();

        switch (operation) {
            case 1: { // Start Duey, 13 digit AS
                final String AS13Digit = slea.readMapleAsciiString();
        //		int unk = slea.readInt(); // Theres an int here, value = 1
                //  9 = error
                final int conv = c.getPlayer().getConversation();

                if (conv == 2) { // Duey
                    c.getSession().write(MaplePacketCreator.sendDuey((byte) 10, loadItems(c.getPlayer())));
                }
                break;
            }
            case 3: { // Send Item
                if (c.getPlayer().getConversation() != 2) {
                    return;
                }
                final byte inventId = slea.readByte();
                final short itemPos = slea.readShort();
                final short amount = slea.readShort();
                final int mesos = slea.readInt();
                final String recipient = slea.readMapleAsciiString();
                boolean quickdelivery = slea.readByte() > 0;

                final int finalcost = mesos + GameConstants.getTaxAmount(mesos) + (quickdelivery ? 0 : 5000);

                if (mesos >= 0 && mesos <= 100000000 && c.getPlayer().getMeso() >= finalcost) {
                    final int accid = MapleCharacterUtil.getIdByName(recipient);
                    if (accid != -1) {
                        if (accid != c.getAccID()) {
                            boolean recipientOn = false;
                            MapleClient rClient = null;
                            /*        try {
                             int channel = c.getChannelServer().getWorldInterface().find(recipient);
                             if (channel > -1) {
                             recipientOn = true;
                             ChannelServer rcserv = ChannelServer.getInstance(channel);
                             rClient = rcserv.getPlayerStorage().getCharacterByName(recipient).getClient();
                             }
                             } catch (RemoteException re) {
                             c.getChannelServer().reconnectWorld();
                             }*/

                            if (inventId > 0) {
                                final MapleInventoryType inv = MapleInventoryType.getByType(inventId);
                                final IItem item = c.getPlayer().getInventory(inv).getItem((byte) itemPos);
                                if (item == null) {
                                    c.getSession().write(MaplePacketCreator.sendDuey((byte) 17, null)); // Unsuccessfull
                                    return;
                                }
                                final byte flag = item.getFlag();
                                if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                                    c.getSession().write(MaplePacketCreator.enableActions());
                                    return;
                                }
                                if (c.getPlayer().getItemQuantity(item.getItemId(), false) >= amount) {
                                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                                    if (!ii.isDropRestricted(item.getItemId()) && !ii.isAccountShared(item.getItemId())) {
                                        if (addItemToDB(item, amount, mesos, c.getPlayer().getName(), accid, recipientOn)) {
                                            if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                                                MapleInventoryManipulator.removeFromSlot(c, inv, (byte) itemPos, item.getQuantity(), true);
                                            } else {
                                                MapleInventoryManipulator.removeFromSlot(c, inv, (byte) itemPos, amount, true, false);
                                            }
                                            c.getPlayer().gainMeso(-finalcost, false);
                                            c.getSession().write(MaplePacketCreator.sendDuey((byte) 19, null)); // Successfull
                                        } else {
                                            c.getSession().write(MaplePacketCreator.sendDuey((byte) 17, null)); // Unsuccessful
                                        }
                                    } else {
                                        c.getSession().write(MaplePacketCreator.sendDuey((byte) 17, null)); // Unsuccessfull
                                    }
                                } else {
                                    c.getSession().write(MaplePacketCreator.sendDuey((byte) 17, null)); // Unsuccessfull
                                }
                            } else {
                                if (addMesoToDB(mesos, c.getPlayer().getName(), accid, recipientOn)) {
                                    c.getPlayer().gainMeso(-finalcost, false);

                                    c.getSession().write(MaplePacketCreator.sendDuey((byte) 19, null)); // Successfull
                                } else {
                                    c.getSession().write(MaplePacketCreator.sendDuey((byte) 17, null)); // Unsuccessfull
                                }
                            }
        //                            if (recipientOn && rClient != null) {
                            //                              rClient.getSession().write(MaplePacketCreator.sendDueyMSG(Actions._MSG.getCode()));
                            //                        }
                        } else {
                            c.getSession().write(MaplePacketCreator.sendDuey((byte) 15, null)); // Same acc error
                        }
                    } else {
                        c.getSession().write(MaplePacketCreator.sendDuey((byte) 14, null)); // Name does not exist
                    }
                } else {
                    c.getSession().write(MaplePacketCreator.sendDuey((byte) 12, null)); // Not enough mesos
                }
                break;
            }
            case 5: { // Recieve 
                if (c.getPlayer().getConversation() != 2) {
                    return;
                }
                final int id = slea.readInt();
                //System.out.println("Item attempted : " + id);
                final MapleDueyActions dp = loadSingleItem(id, c.getPlayer().getId());
                if (dp == null) {
                    return;
                }
                if (dp.getItem() != null && !MapleInventoryManipulator.checkSpace(c, dp.getItem().getItemId(), dp.getItem().getQuantity(), dp.getItem().getOwner())) {
                    c.getSession().write(MaplePacketCreator.sendDuey((byte) 16, null)); // Not enough Space
                    return;
                } else if (dp.getMesos() < 0 || (dp.getMesos() + c.getPlayer().getMeso()) < 0) {
                    c.getSession().write(MaplePacketCreator.sendDuey((byte) 17, null)); // Unsuccessfull
                    return;
                }
                removeItemFromDB(id, c.getPlayer().getId()); // Remove first
                //System.out.println("Item removed : " + id);
                if (dp.getItem() != null) {
                    MapleInventoryManipulator.addFromDrop(c, dp.getItem(), false);
                }
                if (dp.getMesos() != 0) {
                    c.getPlayer().gainMeso(dp.getMesos(), false);
                }
                c.getSession().write(MaplePacketCreator.removeItemFromDuey(false, id));
                break;
            }
            case 6: { // Remove 
                if (c.getPlayer().getConversation() != 2) {
                    return;
                }
                final int id = slea.readInt();
                removeItemFromDB(id, c.getPlayer().getId());
                c.getSession().write(MaplePacketCreator.removeItemFromDuey(true, id));
                break;
            }
            case 8: { // Close Duey
                c.getPlayer().setConversation(0);
                break;
            }
            default: {
                System.out.println("Unhandled Duey operation : " + slea.toString());
                break;
            }
        }

    }

    private static final boolean addMesoToDB(final int mesos, final String sName, final int recipientID, final boolean isOn) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO dueys (RecieverId, SenderName, Mesos, TimeStamp, Checked, Type) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, recipientID);
            ps.setString(2, sName);
            ps.setInt(3, mesos);
            ps.setLong(4, System.currentTimeMillis());
            ps.setInt(5, isOn ? 0 : 1);
            ps.setInt(6, 3);

            ps.executeUpdate();
            ps.close();

            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    private static final boolean addItemToDB(final IItem item, final int quantity, final int mesos, final String sName, final int recipientID, final boolean isOn) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO dueys (RecieverId, SenderName, Mesos, TimeStamp, Checked, Type) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, recipientID);
            ps.setString(2, sName);
            ps.setInt(3, mesos);
            ps.setLong(4, System.currentTimeMillis());
            ps.setInt(5, isOn ? 0 : 1);

            ps.setInt(6, item.getType());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ItemLoader.DUEY.saveItems(Collections.singletonList(new Pair<IItem, MapleInventoryType>(item, GameConstants.getInventoryType(item.getItemId()))), rs.getInt(1));
            }
            rs.close();
            ps.close();

            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    public static final List<MapleDueyActions> loadItems(final MapleCharacter chr) {
        List<MapleDueyActions> s = new LinkedList<MapleDueyActions>();
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM dueys WHERE RecieverId = ?");
            ps.setInt(1, chr.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MapleDueyActions dueypack = getItemByPID(rs.getInt("id"));
                dueypack.setSender(rs.getString("SenderName"));
                dueypack.setMesos(rs.getInt("Mesos"));
                dueypack.setSentTime(rs.getLong("TimeStamp"));
                s.add(dueypack);
            }
            rs.close();
            ps.close();
            return s;
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }

    public static final MapleDueyActions loadSingleItem(final int id, final int charid) {
        List<MapleDueyActions> s = new LinkedList<MapleDueyActions>();
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM dueys WHERE Id = ? and RecieverId = ?");
            ps.setInt(1, id);
            ps.setInt(2, charid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                MapleDueyActions dueypack = getItemByPID(id);
                dueypack.setSender(rs.getString("SenderName"));
                dueypack.setMesos(rs.getInt("Mesos"));
                dueypack.setSentTime(rs.getLong("TimeStamp"));
                s.add(dueypack);
                rs.close();
                ps.close();
                return dueypack;
            } else {
                rs.close();
                ps.close();
                return null;
            }
        } catch (SQLException se) {
//	    se.printStackTrace();
            return null;
        }
    }

    public static final void reciveMsg(final MapleClient c, final int recipientId) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE dueys SET Checked = 0 where RecieverId = ?");
            ps.setInt(1, recipientId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private static final void removeItemFromDB(final int id, final int charid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM dueys WHERE Id = ? and RecieverId = ?");
            ps.setInt(1, id);
            ps.setInt(2, charid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private static final MapleDueyActions getItemByPID(final int id) {
        try {
            Map<Integer, Pair<IItem, MapleInventoryType>> iter = ItemLoader.DUEY.loadItems(false, id);
            if (iter != null && iter.size() > 0) {
                for (Pair<IItem, MapleInventoryType> i : iter.values()) {
                    return new MapleDueyActions(id, i.getLeft());
                }
            }
        } catch (Exception se) {
            se.printStackTrace();
        }
        return new MapleDueyActions(id);
    }
}
