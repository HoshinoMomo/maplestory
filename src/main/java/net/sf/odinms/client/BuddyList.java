package net.sf.odinms.client;


import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.tools.MaplePacketCreator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BuddyList implements Serializable {

    /**
     * 预设的好友分组
     */
    public static final String DEFAULT_GROUP = "其他";

    /**
     * 好友名单操作
     *
     */
    public static enum BuddyOperation {

        ADDED, DELETED
    }

    /**
     * 好友名单操作结果
     */
    public static enum BuddyAddResult {

        BUDDYLIST_FULL, ALREADY_ON_LIST, OK
    }

  
    /**
     * 好友列表 id->实体
     */
    private final Map<Integer,BuddyEntry> buddies;

    /**
     * 好友列表的容量
     */
    private byte capacity;

    /**
     * 待处理的好友请求
     */
    private final Deque<BuddyEntry> pendingReqs = new LinkedList<>();

    /**
     * 构造函数
     *
     * @param capacity 好友容量
     */
    public BuddyList(byte capacity) {
        this.buddies = new LinkedHashMap<>();
        this.capacity = capacity;
    }

    public boolean contains(int characterId) {
        return buddies.containsKey(characterId);
    }

    /**
     * 好友是否在线
     *
     * @param charId 好友ID
     * @return 是否在线
     */
    public boolean containsVisible(int charId) {
        BuddyEntry ble = buddies.get(charId);
        if (ble == null) {
            return false;
        }
        return ble.isVisible();
    }

    /**
     * 取得目前好友列表的容量
     *
     * @return 好友列表的容量
     */
    public byte getCapacity() {
        return capacity;
    }

    /**
     * 设定好友清单容量
     *
     * @param newCapacity 新的容量
     */
    public void setCapacity(byte newCapacity) {
        this.capacity = newCapacity;
    }

    /**
     * 由好友ID取得好友
     *
     * @param characterId
     */
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

    /**
     *
     * @param data
     */
    public void loadFromTransfer(final Map<BuddyEntry, Boolean> data) {
        BuddyEntry biddyId;
        boolean pair;
        for (final Map.Entry<BuddyEntry, Boolean> qs : data.entrySet()) {
            biddyId = qs.getKey();
            pair = qs.getValue();
            if (!pair) {
                pendingReqs.push(biddyId);
            } else {
                put(new BuddyEntry(biddyId.getName(), biddyId.getCharacterId(), biddyId.getGroup(), -1, true, biddyId.getLevel(), biddyId.getJob()));
            }
        }
    }

    /**
     * 从数据库拿好友列表
     *
     * @param characterId
     * @throws SQLException
     */
    public void loadFromDb(int characterId) throws SQLException {

        Connection con = InitHikariCP.getCollection();
        PreparedStatement ps = con.prepareStatement("SELECT b.buddyid, b.pending, c.name as buddyname, c.job as buddyjob, c.level as buddylevel, b.groupname FROM buddies as b, characters as c WHERE c.id = b.buddyid AND b.characterid = ?");
        ps.setInt(1, characterId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int buddyid = rs.getInt("buddyid");
            String buddyname = rs.getString("buddyname");
            if (rs.getInt("pending") == 1) {
                pendingReqs.push(new BuddyEntry(buddyname, buddyid, rs.getString("groupname"), -1, false, rs.getInt("buddylevel"), rs.getInt("buddyjob")));
            } else {
                put(new BuddyEntry(buddyname, buddyid, rs.getString("groupname"), -1, true, rs.getInt("buddylevel"), rs.getInt("buddyjob")));
            }
        }
        rs.close();
        ps.close();
        ps = con.prepareStatement("DELETE FROM buddies WHERE pending = 1 AND characterid = ?");
        ps.setInt(1, characterId);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 取得并移除最后一个好友请求
     *
     * @return 最后一个请求
     */
    public BuddyEntry pollPendingRequest() {
        return pendingReqs.pollLast();
    }

    /**
     * 新增好友请求
     *
     * @param client 欲增加好友的角色客戶端
     * @param buddyId 新增的好友ID
     * @param buddyName 新增的好友名称
     * @param buddyChannel 新增的好友频道
     * @param buddyLevel 新增的好友的等级
     * @param buddyJob 新增的好友的职业
     */
    public void addBuddyRequest(MapleClient client, int buddyId, String buddyName, int buddyChannel, int buddyLevel, int buddyJob) {

        this.put(new BuddyEntry(buddyName, buddyId, BuddyList.DEFAULT_GROUP, buddyChannel, false, buddyLevel, buddyJob));

        if (pendingReqs.isEmpty()) {

            client.sendPacket(MaplePacketCreator.requestBuddylistAdd(buddyId, buddyName, buddyLevel, buddyJob));

        } else {

            BuddyEntry newPair = new BuddyEntry(buddyName, buddyId, BuddyList.DEFAULT_GROUP, -1, false, buddyJob, buddyLevel);
            pendingReqs.push(newPair);

        }
    }
}
