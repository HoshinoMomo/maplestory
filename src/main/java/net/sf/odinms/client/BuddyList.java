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

package net.sf.odinms.client;

import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.tools.MaplePacketCreator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class BuddyList {

    /**
     * 预设的好友分组
     */
    public static final String DEFAULT_GROUP = "其他";

    /**
     * 好友名单操作
     *
     */
    public enum BuddyOperation {

        ADDED, DELETED
    }

    /**
     * 好友名单操作结果
     */
    public enum BuddyAddResult {

        BUDDY_LIST_FULL, ALREADY_ON_LIST, OK
    }
    /**
     * 好友列表 id->实体
     */
    private Map<Integer, BuddyEntry> buddies = new LinkedHashMap<>();

    /**
     * 好友列表的容量
     */
    private int capacity;

    /**
     * 待处理的好友请求
     */
    private Deque<CharacterNameAndId> pendingRequests = new LinkedList<>();

    /**
     * 构造函数
     *
     * @param capacity 好友容量
     */
    public BuddyList(int capacity) {
        super();
        this.capacity = capacity;
    }

    public boolean contains(int characterId) {
        return buddies.containsKey(characterId);
    }

    /**
     * 好友是否在线
     *
     * @param characterId 好友ID
     * @return 是否在线
     */
    public boolean containsVisible(int characterId) {
        BuddyEntry ble = buddies.get(characterId);
        if (ble == null) {
            return false;
        }
        return ble.isVisible();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BuddyEntry get(int characterId) {
        return buddies.get(characterId);
    }

    /**
     * 由好友名称取得好友
     *
     * @param characterName 角色名
     */
    public BuddyEntry get(String characterName) {
        for (BuddyEntry ble : buddies.values()) {
            if (ble.getName().equals(characterName)) {
                return ble;
            }
        }
        return null;
    }

    /**
     * 新增好友
     *
     * @param newEntry 新增的好友
     */
    public void put(BuddyEntry newEntry) {
        buddies.put(newEntry.getCharacterId(), newEntry);
    }

    /**
     * 刪除好友
     *
     * @param characterId 角色ID
     */
    public void remove(int characterId) {
        buddies.remove(characterId);
    }

    /**
     * 获得好友列表
     *
     * @return 好友好友列表
     */
    public Collection<BuddyEntry> getBuddies() {
        return buddies.values();
    }

    /**
     * 取得好友列表是不是满了
     *
     */
    public boolean isFull() {
        return buddies.size() >= capacity;
    }

    /**
     * 取得所有好友的ID
     *
     */
    public Collection<Integer> getBuddiesIds() {
        return buddies.keySet();
    }

    public void loadFromDb(int characterId) throws SQLException {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT b.buddyid, b.group, b.pending, c.name as buddyname FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.characterid = ?");
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("pending") == 1) {
                    pendingRequests.push(new CharacterNameAndId(rs.getInt("buddyid"), rs.getString("buddyname")));
                } else {
                    put(new BuddyEntry(rs.getString("buddyname"), rs.getString("group"), rs.getInt("buddyid"), -1, true));
                }
            }
            rs.close();
            ps.close();

            ps = DatabaseConnection.getConnection().prepareStatement("DELETE FROM buddies WHERE pending = 1 AND characterid = ?");
            ps.setInt(1, characterId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
        }
    }

    /**
     * 取得并移除最后一个好友请求
     *
     * @return 最后一个请求
     */
    public CharacterNameAndId pollPendingRequest() {
        return pendingRequests.pollLast();
    }

    /**
     * 是否有来自那个人的好友请求
     * @param name
     * @return
     */
    public boolean hasPendingRequestFrom(String name) {
        for (CharacterNameAndId cnai : this.pendingRequests) {
            if (cnai.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 新增好友请求
     *
     * @param c 欲增加好友的角色客戶端
     * @param cidFrom 新增的好友ID
     * @param nameFrom 新增的好友名称
     * @param channelFrom 新增的好友频道
     */
    public void addBuddyRequest(MapleClient c, int cidFrom, String nameFrom, int channelFrom) {
        put(new BuddyEntry(nameFrom, cidFrom, channelFrom, false));
        if (pendingRequests.isEmpty()) {
            c.getSession().write(MaplePacketCreator.requestBuddylistAdd(cidFrom, nameFrom));
        } else {
            pendingRequests.push(new CharacterNameAndId(cidFrom, nameFrom));
        }
    }
}