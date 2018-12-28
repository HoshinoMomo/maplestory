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

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.constants.GameConstants;
import net.sf.odinms.handling.world.World;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.Timer.CheatTimer;
import net.sf.odinms.tools.FileoutputUtil;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CheatTracker {

    private static final Logger logger = LoggerFactory.getLogger(CheatTracker.class);

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final Map<CheatingOffenseEnum, CheatingOffenseEntry> offenses = new LinkedHashMap<>();
    private final WeakReference<MapleCharacter> chr;
    // For keeping track of speed attack hack
    private int lastAttacktickCount = 0;
    private byte attackTickResetCount = 0;
    private long serverClientAtkTickDiff = 0;
    private long lastDamage = 0;
    private long takingDamageSince;
    private int numSequentialDamage = 0;
    private long lastDamageTakenTime = 0;
    private byte numZeroDamageTaken = 0;
    private int numSequentialSummonAttack = 0;
    private long summonSummonTime = 0;
    private int numSameDamage = 0;
    private Point lastMonsterMove;
    private int monsterMoveCount;
    private int attacksWithoutHit = 0;
    private byte dropsPerSecond = 0;
    private long lastDropTime = 0;
    private byte msgsPerSecond = 0;
    private long lastMsgTime = 0;
    private ScheduledFuture<?> invalidationTask;
    private int gmMessage = 50;
    private int lastTickCount = 0;
    private int tickSame = 0;
    private long lastASmegaTime = 0;
    private long[] lastTime = new long[6];
    private long lastSaveTime = 0L;

    public CheatTracker(final MapleCharacter chr) {
        this.chr = new WeakReference<>(chr);
        invalidationTask = CheatTimer.getInstance().register(new InvalidationTask(), 60000);
        takingDamageSince = System.currentTimeMillis();
    }

    public final void checkAttack(final int skillId, final int tickCount) {
        final short AtkDelay = GameConstants.getAttackDelay(skillId);
        if ((tickCount - lastAttacktickCount) < AtkDelay) {
            registerOffense(CheatingOffenseEnum.FAST_ATTACK);
        }
        //system time minus tick count
        final long minusTickCount = System.currentTimeMillis() - tickCount; // hack = - more
        if (serverClientAtkTickDiff - minusTickCount > 250) { // 250 is the ping,
            // TODO
            registerOffense(CheatingOffenseEnum.FAST_ATTACK2);
        }
        // if speed hack, client tickCount values will be running at a faster pace
        // For lagging, it isn't an issue since TIME is running simotaniously, client
        // will be sending values of older time

//	System.out.println("Delay [" + skillId + "] = " + (tickCount - lastAttacktickCount) + ", " + (serverClientAtkTickDiff - minusTickCount));
        // Without ++, the difference will always be at 100
        if (++attackTickResetCount >= (AtkDelay <= 200 ? 2 : 4)) {
            attackTickResetCount = 0;
            serverClientAtkTickDiff = minusTickCount;
        }
        chr.get().updateTick(tickCount);
        lastAttacktickCount = tickCount;
    }

    public final void checkTakeDamage(final int damage) {
        numSequentialDamage++;
        lastDamageTakenTime = System.currentTimeMillis();

        // System.out.println("tb" + timeBetweenDamage);
        // System.out.println("ns" + numSequentialDamage);
        // System.out.println(timeBetweenDamage / 1500 + "(" + timeBetweenDamage / numSequentialDamage + ")");
        if (lastDamageTakenTime - takingDamageSince / 500 < numSequentialDamage) {
            registerOffense(CheatingOffenseEnum.FAST_TAKE_DAMAGE);
        }
        if (lastDamageTakenTime - takingDamageSince > 4500) {
            takingDamageSince = lastDamageTakenTime;
            numSequentialDamage = 0;
        }
        /*
         * (non-thieves) Min Miss Rate: 2% Max Miss Rate: 80% (thieves) Min Miss
         * Rate: 5% Max Miss Rate: 95%
         */
        if (damage == 0) {
            numZeroDamageTaken++;
            if (numZeroDamageTaken >= 35) { // Num count MSEA a/b players
                numZeroDamageTaken = 0;
                registerOffense(CheatingOffenseEnum.HIGH_AVOID);
            }
        } else if (damage != -1) {
            numZeroDamageTaken = 0;
        }
    }

    public final void checkSameDamage(final int dmg) {
        if (dmg > 2000 && lastDamage == dmg) {
            numSameDamage++;

            if (numSameDamage > 5) {
                numSameDamage = 0;
                registerOffense(CheatingOffenseEnum.SAME_DAMAGE, numSameDamage + " times: " + dmg);
            }
        } else {
            lastDamage = dmg;
            numSameDamage = 0;
        }
    }

    public final void checkMoveMonster(final Point pos, MapleCharacter chr) {

        //double dis = Math.abs(pos.distance(lastMonsterMove));
        if (pos.equals(this.lastMonsterMove)) {
            monsterMoveCount++;
            if (monsterMoveCount > 50) {
                //   registerOffense(CheatingOffense.MOVE_MONSTERS);
                monsterMoveCount = 0;
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[Admin notice] Player:[" + MapleCharacterUtil.makeMapleReadable(chr.getName()) + "] map:[" + chr.getMapId() + "] is moving monster! ").getBytes());
                String note = "时间：" + FileoutputUtil.CurrentReadable_Time() + " "
                        + "|| 玩家名字：" + chr.getName() + ""
                        + "|| 玩家地图：" + chr.getMapId() + "\r\n";
                FileoutputUtil.packetLog("日志\\log\\吸怪检测\\" + chr.getName() + ".log", note);
            }
            /*
             * } else if ( dis > 1500 ) { monsterMoveCount++; if
             * (monsterMoveCount > 15) {
             * registerOffense(CheatingOffense.MOVE_MONSTERS); }
             */
        } else {
            lastMonsterMove = pos;
            monsterMoveCount = 1;
        }
    }

    public final void resetSummonAttack() {
        summonSummonTime = System.currentTimeMillis();
        numSequentialSummonAttack = 0;
    }

    public final boolean checkSummonAttack() {
        numSequentialSummonAttack++;
        //estimated
        // System.out.println(numMPRegens + "/" + allowedRegens);
        if ((System.currentTimeMillis() - summonSummonTime) / (2000 + 1) < numSequentialSummonAttack) {
            registerOffense(CheatingOffenseEnum.FAST_SUMMON_ATTACK);
            return false;
        }
        return true;
    }

    public final void checkDrop() {
        checkDrop(false);
    }

    public final void checkDrop(final boolean dc) {
        if ((System.currentTimeMillis() - lastDropTime) < 1000) {
            dropsPerSecond++;
            if (dropsPerSecond >= (dc ? 32 : 16) && chr.get() != null) {
//                if (dc) {
//                    chr.get().getClient().getSession().close();
//                } else {
                chr.get().getClient().setMonitored(true);
//                }
            }
        } else {
            dropsPerSecond = 0;
        }
        lastDropTime = System.currentTimeMillis();
    }

    public boolean canAvatarSmega2() {
        if (lastASmegaTime + 10000 > System.currentTimeMillis() && chr.get() != null && !chr.get().isGM()) {
            return false;
        }
        lastASmegaTime = System.currentTimeMillis();
        return true;
    }

    public synchronized boolean GMSpam(int limit, int type) {
        if (type < 0 || lastTime.length < type) {
            type = 1; // default xD
        }
        if (System.currentTimeMillis() < limit + lastTime[type]) {
            return true;
        }
        lastTime[type] = System.currentTimeMillis();
        return false;
    }

    public final void checkMsg() { //ALL types of msg. caution with number of  msgsPerSecond
        if ((System.currentTimeMillis() - lastMsgTime) < 1000) { //luckily maplestory has auto-check for too much msging
            msgsPerSecond++;
            /*
             * if (msgsPerSecond > 10 && chr.get() != null) {
             * chr.get().getClient().getSession().close(); }
             */
        } else {
            msgsPerSecond = 0;
        }
        lastMsgTime = System.currentTimeMillis();
    }

    public final int getAttacksWithoutHit() {
        return attacksWithoutHit;
    }

    public final void setAttacksWithoutHit(final boolean increase) {
        if (increase) {
            this.attacksWithoutHit++;
        } else {
            this.attacksWithoutHit = 0;
        }
    }

    public final void registerOffense(final CheatingOffenseEnum offense) {
        registerOffense(offense, null);
    }

    public final void registerOffense(final CheatingOffenseEnum offense, final String param) {
        final MapleCharacter chrhardref = chr.get();
        if (chrhardref == null || !offense.isEnabled() || chrhardref.isClone() || chrhardref.isGM()) {
            return;
        }
        CheatingOffenseEntry entry = null;
        readLock.lock();
        try {
            entry = offenses.get(offense);
        } finally {
            readLock.unlock();
        }
        if (entry != null && entry.isExpired()) {
            expireEntry(entry);
            entry = null;
        }
        if (entry == null) {
            entry = new CheatingOffenseEntry(offense, chrhardref.getId());
        }
        if (param != null) {
            entry.setParam(param);
        }
        entry.incrementCount();
        if (offense.shouldAutoBan(entry.getCount())) {
            final byte type = offense.getBanType();
            if (type == 1) {
                AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
            } else if (type == 2) {
                //怪物碰撞过快 回避率过高 快速攻击 快速攻击2 怪物移动 伤害相同
                String outputFileName = "断线";
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[GM信息] " + chrhardref.getName() + " 自动断线 类别: " + offense.toString() + " 原因: " + (param == null ? "" : (" - " + param))).getBytes());
                FileoutputUtil.logToFile_chr(chrhardref, "日志/logs/Hack/" + outputFileName + ".txt", "\r\n " + FileoutputUtil.NowTime() + " 类别" + offense.toString() + " 原因 " + (param == null ? "" : (" - " + param)));
                chrhardref.getClient().getSession().close();
                //chrhardref.getClient().getSession().close();
            } else if (type == 3) {
            }
            gmMessage = 50;
            return;
        }
        writeLock.lock();
        try {
            offenses.put(offense, entry);
        } finally {
            writeLock.unlock();
        }
        switch (offense) {
            case HIGH_DAMAGE_MAGIC:
            case HIGH_DAMAGE_MAGIC_2:
            case HIGH_DAMAGE_2:
            case HIGH_DAMAGE:
            case FAST_ATTACK:
            case FAST_ATTACK2:
            case ATTACK_FARAWAY_MONSTER:
            case ATTACK_FARAWAY_MONSTER_SUMMON:
            case SAME_DAMAGE:
            case ATTRACT_MODS:
            case MOVE_MONSTERS:
            case HIGH_AVOID:
                String show = offense.name();
                gmMessage--;
                if (gmMessage % 5 == 0) {
                    String msg = "[管理员信息] " + chrhardref.getName() + " 疑似 " + show
                            + "地图ID [" + chrhardref.getMapId() + "]" + ""
                            + (param == null ? "" : (" - " + param));
                    World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, msg).getBytes());
                    FileoutputUtil.logToFile_chr(chrhardref, FileoutputUtil.hack_log, show);
                }
                if (gmMessage == 0) {

                    // System.out.println(MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + "怀疑使用外挂");
                    // World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "[管理員訊息] 开挂玩家[" + MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + "] 地图ID[" + chrhardref.getMapId() + "] suspected of hacking! " + StringUtil.makeEnumHumanReadable(offense.name()) + (param == null ? "" : (" - " + param))).getBytes());
                    /*
                     * String note = "时间：" +
                     * FileoutputUtil.CurrentReadable_Time() + " " + "|| 玩家名字："
                     * + chrhardref.getName() + "" + "|| 玩家地图：" +
                     * chrhardref.getMapId() + "" + "|| 外挂类型：" + offense.name()
                     * + "\r\n"; FileoutputUtil.packetLog("日志\\log\\外挂检测\\" +
                     * chrhardref.getName() + ".log", note);
                     */ //                    AutobanManager.getInstance().autoban(chrhardref.getClient(), StringUtil.makeEnumHumanReadable(offense.name()));
                    gmMessage = 50;
                }
                break;
        }
        CheatingOffensePersister.getInstance().persistEntry(entry);
    }

    public void updateTick(int newTick) {
        if (newTick == lastTickCount) { //definitely packet spamming
/*
             * if (tickSame >= 5) { chr.get().getClient().getSession().close();
             * //i could also add a check for less than, but i'm not too worried
             * at the moment :)
             } else {
             */
            tickSame++;
//	    }
        } else {
            tickSame = 0;
        }
        lastTickCount = newTick;
    }

    public final void expireEntry(final CheatingOffenseEntry coe) {
        writeLock.lock();
        try {
            offenses.remove(coe.getOffense());
        } finally {
            writeLock.unlock();
        }
    }

    public final int getPoints() {
        int ret = 0;
        CheatingOffenseEntry[] cheatingOffenseEntries;
        readLock.lock();
        try {
            cheatingOffenseEntries = offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
        } finally {
            readLock.unlock();
        }
        for (final CheatingOffenseEntry entry : cheatingOffenseEntries) {
            if (entry.isExpired()) {
                expireEntry(entry);
            } else {
                ret += entry.getPoints();
            }
        }
        return ret;
    }

    public final Map<CheatingOffenseEnum, CheatingOffenseEntry> getOffenses() {
        return Collections.unmodifiableMap(offenses);
    }

    public final String getSummary() {
        StringBuilder ret = new StringBuilder();
        List<CheatingOffenseEntry> offenseList = new ArrayList<>();
        readLock.lock();
        try {
            for (final CheatingOffenseEntry entry : offenses.values()) {
                if (!entry.isExpired()) {
                    offenseList.add(entry);
                }
            }
        } finally {
            readLock.unlock();
        }
        offenseList = offenseList.stream().sorted(Comparator.comparing(CheatingOffenseEntry::getPoints)).collect(Collectors.toList());
        final int to = Math.min(offenseList.size(), 4);
        for (int x = 0; x < to; x++) {
            ret.append(StringUtil.makeEnumHumanReadable(offenseList.get(x).getOffense().name()));
            ret.append(": ");
            ret.append(offenseList.get(x).getCount());
            if (x != to - 1) {
                ret.append(" ");
            }
        }
        return ret.toString();
    }

    public final void dispose() {
        if (invalidationTask != null) {
            invalidationTask.cancel(false);
        }
        invalidationTask = null;
    }

    /**
     * 检测是否能保存角色数据 只针对 PLAYER_UPDATE 这个封包 设置3分钟保存 以免频繁的保存数据
     *
     * @return
     */
    public boolean canSaveDB() {
        if (lastSaveTime + 3 * 60 * 1000 > System.currentTimeMillis() && chr.get() != null) {
            return false;
        }
        lastSaveTime = System.currentTimeMillis();
        return true;
    }

    public int getLastSaveTime() {
        if (lastSaveTime <= 0) {
            lastSaveTime = System.currentTimeMillis();
        }
        return (int) (((lastSaveTime + (3 * 60 * 1000)) - System.currentTimeMillis()) / 1000);
    }

    private final class InvalidationTask implements Runnable {

        @Override
        public final void run() {
            CheatingOffenseEntry[] offenses_copy;
            readLock.lock();
            try {
                offenses_copy = offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
            } finally {
                readLock.unlock();
            }
            for (CheatingOffenseEntry offense : offenses_copy) {
                if (offense.isExpired()) {
                    expireEntry(offense);
                }
            }
            if (chr.get() == null) {
                dispose();
            }
        }
    }
}
