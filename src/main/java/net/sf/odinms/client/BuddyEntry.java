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
package net.sf.odinms.client;

/**
 *  好友个体
 *
 * @author Flower
 */
public class BuddyEntry {

    /**
     * 姓名
     */
    private final String name;

    /**
     * 分组
     */
    private String group;

    /**
     * ID
     */
    private final int characterId;

    /**
     * 等级
     */
    private final int level;

    /**
     * 职业
     */
    private final int job;

    /**
     * 是否可见
     */
    private boolean visible;

    /**
     * 频道
     */
    private int channel;

    public BuddyEntry(String name, int characterId, String group, int channel, boolean visible, int level, int job) {
        super();
        this.name = name;
        this.characterId = characterId;
        this.group = group;
        this.channel = channel;
        this.visible = visible;
        this.level = level;
        this.job = job;
    }

    /**
     * @return 如果好友不在线返回-1
     */
    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isOnline() {
        return channel >= 0;
    }

    public void setOffline() {
        channel = -1;
    }

    public String getName() {
        return name;
    }

    public int getCharacterId() {
        return characterId;
    }

    public int getLevel() {
        return level;
    }

    public int getJob() {
        return job;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String newGroup) {
        this.group = newGroup;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + characterId;
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        final BuddyEntry other = (BuddyEntry) obj;
        return characterId == other.characterId;
    }
}
