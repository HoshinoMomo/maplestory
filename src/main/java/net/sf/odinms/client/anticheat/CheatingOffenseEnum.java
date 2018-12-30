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
package net.sf.odinms.client.anticheat;

/**
 * 作弊行为的枚举
 */
public enum CheatingOffenseEnum {

    FAST_SUMMON_ATTACK((byte) 5, 6000, 50, (byte) 2), //召唤兽快速攻击
    FAST_ATTACK((byte) 5, 6000, 200, (byte) 2), //快速攻击
    FAST_ATTACK2((byte) 5, 6000, 500, (byte) 2), //快速攻击2
    MOVE_MONSTERS((byte) 5, 30000, 500, (byte) 2), //怪物移动
    FAST_HP_MP_REGEN((byte) 5, 20000, 100, (byte) 2), //HP_MP_快速回复
    SAME_DAMAGE((byte) 5, 180000),  //伤害相同
    ATTACK_WITHOUT_GETTING_HIT((byte) 1, 30000, 1200, (byte) 0),  //攻击无敌
    HIGH_DAMAGE_MAGIC((byte) 5, 30000),  //高魔法伤害
    HIGH_DAMAGE_MAGIC_2((byte) 10, 180000), //高魔法伤害2
    HIGH_DAMAGE((byte) 5, 30000),  //高物理伤害
    HIGH_DAMAGE_2((byte) 10, 180000), //高物理伤害2
    EXCEED_DAMAGE_CAP((byte) 5, 60000, 800, (byte) 0), //超过伤害上限
    ATTACK_FARAWAY_MONSTER((byte) 5, 180000), // NEEDS A SPECIAL FORMULAR! 需要特殊的公式，攻击过远的怪物
    ATTACK_FARAWAY_MONSTER_SUMMON((byte) 5, 180000, 200, (byte) 2), //召唤物攻击过远的怪物
    REGEN_HIGH_HP((byte) 10, 30000, 1000, (byte) 2), //回复过高的hp
    REGEN_HIGH_MP((byte) 10, 30000, 1000, (byte) 2), //回复过高的mp
    ITEMVAC_CLIENT((byte) 3, 10000, 100),  //客户端全图吸物
    ITEMVAC_SERVER((byte) 2, 10000, 100, (byte) 2),  //服务端全图吸物
    PET_ITEMVAC_CLIENT((byte) 3, 10000, 100), //客户端全图宠物吸物
    PET_ITEMVAC_SERVER((byte) 2, 10000, 100, (byte) 2), //服务端全图宠物吸物
    USING_FARAWAY_PORTAL((byte) 1, 60000, 100, (byte) 0), //使用过远的传送点
    FAST_TAKE_DAMAGE((byte) 1, 60000, 100),  //怪物碰撞过快
    HIGH_AVOID((byte) 5, 180000, 100), //回避率过高
    FAST_MOVE((byte) 1, 60000),  //快速移动
    HIGH_JUMP((byte) 1, 60000),  //跳跃过高
    MISMATCHING_BULLETCOUNT((byte) 1, 300000), //匹配不当的布告栏
    ETC_EXPLOSION((byte) 1, 300000), //其他异常
    ATTACKING_WHILE_DEAD((byte) 1, 300000),  //死亡后攻击
    USING_UNAVAILABLE_ITEM((byte) 1, 300000), //使用不存在的道具
    FAMING_SELF((byte) 1, 300000), // purely for marker reasons (appears in the database)给自己加声望
    FAMING_UNDER_15((byte) 1, 300000), //等级低于15级使用声望
    EXPLODING_NONEXISTANT((byte) 1, 300000), //金钱炸弹—金钱不存在
    SUMMON_HACK((byte) 1, 300000), //召唤兽欺骗
    SUMMON_HACK_MOBS((byte) 1, 300000), //召唤兽打怪异常
    ARAN_COMBO_HACK((byte) 1, 600000, 50, (byte)2), //战神联机欺骗
    HEAL_ATTACKING_UNDEAD((byte) 20, 30000, 100), //治愈术攻击非不死系怪物
    ATTRACT_MODS((byte) 1, 7000, 5); //吸怪

    private final byte points;
    /**
     * 有效的持续时间
     */
    private final long validityDuration;

    /**
     * 超过几次自动封号
     */
    private final int autoBanCount;

    /**
     * 0 = Disabled, 1 = Enabled, 2 = DC
     */
    private byte banType;

    public final byte getPoints() {
        return points;
    }

    public final long getValidityDuration() {
        return validityDuration;
    }

    public final boolean shouldAutoBan(final int count) {
        if (autoBanCount == -1) {
            return false;
        }
        return count >= autoBanCount;
    }

    public final byte getBanType() {
        return banType;
    }

    public final void setEnabled(final boolean enabled) {
        banType = (byte) (enabled ? 1 : 0);
    }

    public final boolean isEnabled() {
        return banType >= 1;
    }

    CheatingOffenseEnum(final byte points, final long validityDuration) {
        this(points, validityDuration, -1, (byte) 1);
    }

    CheatingOffenseEnum(final byte points, final long validityDuration, final int autoBanCount) {
        this(points, validityDuration, autoBanCount, (byte) 1);
    }

    CheatingOffenseEnum(final byte points, final long validityDuration, final int autoBanCount, final byte banType) {
        this.points = points;
        this.validityDuration = validityDuration;
        this.autoBanCount = autoBanCount;
        this.banType = banType;
    }
}
