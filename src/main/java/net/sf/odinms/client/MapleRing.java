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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.odinms.database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Danny
 */
public class MapleRing implements Comparable<MapleRing> {

    private int ringId;
    private int ringId2;
    private int partnerId;
    private int itemId;
    private String partnerName;
    private boolean equipped;
    private static Logger log = LoggerFactory.getLogger(MapleRing.class);

    private MapleRing(int id, int id2, int partnerId, int itemid, String partnername) {
        this.ringId = id;
        this.ringId2 = id2;
        this.partnerId = partnerId;
        this.itemId = itemid;
        this.partnerName = partnername;
    }

    public static Equip loadFromDb(int itemid, byte position, int uniqueid) {
        try {
            Connection con = DatabaseConnection.getConnection(); // Get a connection to the database

            PreparedStatement ps = con.prepareStatement("SELECT * FROM rings WHERE ringid = ?"); // Get ring details..

            ps.setInt(1, uniqueid);
            ResultSet rs = ps.executeQuery();
            rs.next();
            Equip ret = new Equip(itemid, position, true, rs.getInt("partnerRingId"), rs.getInt("partnerChrId"), rs.getString("partnerName"));
            ret.setUniqueId(uniqueid);
            rs.close();
            ps.close();

            return ret;
        } catch (SQLException ex) {
            log.error("Error loading ring from DB", ex);
            return null;
        }
    }

    public static void createRing(int id1, int id2, int chrId1, int chrId2, String partnerName1, String partnerName2) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO rings (ringid, partnerRingId, partnerChrId, partnerName) VALUES (?, ?, ?, ?)");
            ps.setInt(1, id1);
            ps.setInt(2, id2);
            ps.setInt(3, chrId2);
            ps.setString(4, partnerName2);
            ps.executeUpdate();
            ps.close();

            ps = con.prepareStatement("INSERT INTO rings (ringid, partnerRingId, partnerChrId, partnerName) VALUES (?, ?, ?, ?)");
            ps.setInt(1, id2);
            ps.setInt(2, id1);
            ps.setInt(3, chrId1);
            ps.setString(4, partnerName1);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            log.error("Error saving ring to DB", ex);
        }
    }

    public int getRingId() {
        return ringId;
    }

    public int getPartnerRingId() {
        return ringId2;
    }

    public int getPartnerChrId() {
        return partnerId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MapleRing) {
            if (((MapleRing) o).getRingId() == getRingId()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.ringId;
        return hash;
    }

    @Override
    public int compareTo(MapleRing other) {
        if (ringId < other.getRingId()) {
            return -1;
        } else if (ringId == other.getRingId()) {
            return 0;
        } else {
            return 1;
        }
    }

    public static boolean checkRingDB(MapleCharacter player) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM rings WHERE partnerChrId = ?");
            ps.setInt(1, player.getId());
            ResultSet rs = ps.executeQuery();
            boolean ret = rs.next();
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException pie) {
            return true;
        }
    }

    public static void removeRingFromDb(MapleCharacter player) {
        try {
            Connection con = DatabaseConnection.getConnection();
            int otherId;
            PreparedStatement ps = con.prepareStatement("SELECT partnerRingId FROM rings WHERE partnerChrId = ?");
            ps.setInt(1, player.getId());
            ResultSet rs = ps.executeQuery();
            rs.next();
            otherId = rs.getInt("partnerRingId");
            rs.close();
            ps = con.prepareStatement("DELETE FROM rings WHERE partnerChrId = ?");
            ps.setInt(1, player.getId());
            ps.executeUpdate();
            ps = con.prepareStatement("DELETE FROM rings WHERE partnerChrId = ?");
            ps.setInt(1, otherId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException sex) {
        }
    }
}