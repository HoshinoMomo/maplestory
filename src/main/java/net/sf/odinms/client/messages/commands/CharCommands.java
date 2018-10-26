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

package net.sf.odinms.client.messages.commands;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import net.sf.odinms.client.Equip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

public class CharCommands implements Command {

    @SuppressWarnings("static-access")
    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equals("!最大属性")) {
            player.setMaxHp(30000);
            player.setMaxMp(30000);
            player.setStr(Short.MAX_VALUE);
            player.setDex(Short.MAX_VALUE);
            player.setInt(Short.MAX_VALUE);
            player.setLuk(Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.MAXHP, 30000);
            player.updateSingleStat(MapleStat.MAXMP, 30000);
            player.updateSingleStat(MapleStat.STR, Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.DEX, Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.INT, Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.LUK, Short.MAX_VALUE);
        } else if (splitted[0].equals("!最小属性")) {
            player.setMaxHp(50);
            player.setMaxMp(5);
            player.setStr(4);
            player.setDex(4);
            player.setInt(4);
            player.setLuk(4);
            player.updateSingleStat(MapleStat.MAXHP, 50);
            player.updateSingleStat(MapleStat.MAXMP, 5);
            player.updateSingleStat(MapleStat.STR, 4);
            player.updateSingleStat(MapleStat.DEX, 4);
            player.updateSingleStat(MapleStat.INT, 4);
            player.updateSingleStat(MapleStat.LUK, 4);
        } else if (splitted[0].equals("!满技能")) {
            c.getPlayer().maxAllSkills();
        } else if (splitted[0].equals("!最大血")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setMaxHp(stat);
            player.updateSingleStat(MapleStat.MAXHP, stat);
        } else if (splitted[0].equals("!最大魔法")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setMaxMp(stat);
            player.updateSingleStat(MapleStat.MAXMP, stat);
        } else if (splitted[0].equals("!血")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setHp(stat);
            player.updateSingleStat(MapleStat.HP, stat);
        } else if (splitted[0].equals("!魔法")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setMp(stat);
            player.updateSingleStat(MapleStat.MP, stat);
        } else if (splitted[0].equals("!力量")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setStr(stat);
            player.updateSingleStat(MapleStat.STR, stat);
        } else if (splitted[0].equals("!敏捷")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setDex(stat);
            player.updateSingleStat(MapleStat.DEX, stat);
        } else if (splitted[0].equals("!智力")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setInt(stat);
            player.updateSingleStat(MapleStat.INT, stat);
        } else if (splitted[0].equals("!运气")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setLuk(stat);
            player.updateSingleStat(MapleStat.LUK, stat);
        } else if (splitted[0].equals("!学习技能")) {
            ISkill skill = SkillFactory.getSkill(Integer.parseInt(splitted[1]));
            int level = getOptionalIntArg(splitted, 2, 1);
            int masterlevel = getOptionalIntArg(splitted, 3, 1);
            if (level > skill.getMaxLevel()) {
                level = skill.getMaxLevel();
            }
            if (masterlevel > skill.getMaxLevel() && skill.isFourthJob()) {
                masterlevel = skill.getMaxLevel();
            } else {
                masterlevel = 0;
            }
            player.changeSkillLevel(skill, level, masterlevel);
        } else if (splitted[0].equals("!god")) {
            boolean choice = true;
            int set = Integer.parseInt(splitted[1]);
            if (set == 1) {
                choice = true;
            } else if (set == 2) {
                choice = false;
            }
            player.setInvincible(choice);
        } else if (splitted[0].equals("!sp")) {
            int sp = Integer.parseInt(splitted[1]);
            if (sp + player.getRemainingSp() > Short.MAX_VALUE) {
                sp = Short.MAX_VALUE;
            }
            player.setRemainingSp(sp);
            player.updateSingleStat(MapleStat.AVAILABLESP, player.getRemainingSp());
        } else if (splitted[0].equals("!ap")) {
            int ap = Integer.parseInt(splitted[1]);
            if (ap + player.getRemainingAp() > Short.MAX_VALUE) {
                ap = Short.MAX_VALUE;
            }
            player.setRemainingAp(ap);
            player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
        } else if (splitted[0].equals("!职业")) {
            int jobId = Integer.parseInt(splitted[1]);
            if (MapleJob.getById(jobId) != null) {
                player.changeJob(MapleJob.getById(jobId));
            }
             } else if (splitted[0].equals("!黄公告")) {
            if (splitted.length > 1) {
                String abc= StringUtil.joinStringFrom(splitted, 1);
                        for (ChannelServer cservs : ChannelServer.getAllInstances()){
                                for (MapleCharacter z: cservs.getPlayerStorage().getAllCharacters()) {
                                                z.getClient().getSession().write(MaplePacketCreator.sendYellowTip(abc));

                                }
                        }
            }
        } else if (splitted[0].equals("!我在哪")) {
            int currentMap = player.getMapId();
            mc.dropMessage("你在地图 " + currentMap + ".");
        } else if (splitted[0].equals("!1")) {
            c.getPlayer().showDojoClock();
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (splitted[0].equals("!2")) {
            c.getSession().write(MaplePacketCreator.environmentChange("Dojang/start", 4));
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (splitted[0].equals("!3")) {
            c.getSession().write(MaplePacketCreator.environmentChange("dojang/start/stage", 3));
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (splitted[0].equals("!4")) {
            c.getSession().write(MaplePacketCreator.getEnergy(c.getPlayer().getDojoEnergy()));
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (splitted[0].equals("!5")) {
            c.getSession().write(MaplePacketCreator.sendBlockedMessage(5));
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (splitted[0].equals("!6")) {
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (splitted[0].equals("!关门")) {
            player.getMap().clearDrops(player, true);
        } else if (splitted[0].equals("!8")) {
            c.getSession().write(MaplePacketCreator.Combo_Effect(8));
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (splitted[0].equals("!金钱")) {
            if (Integer.MAX_VALUE - (player.getMeso() + Integer.parseInt(splitted[1])) >= 0) {
                player.gainMeso(Integer.parseInt(splitted[1]), true);
            } else {
                player.gainMeso(Integer.MAX_VALUE - player.getMeso(), true);
            }
        } else if (splitted[0].equals("!升级")) {
            if (player.getLevel() < 200) {
                player.levelUp();
                player.setExp(0);
            } else {
                mc.dropMessage("你现在是最高级：200级.");
            }
        } else if (splitted[0].equals("!物品")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            if (Integer.parseInt(splitted[1]) >= 5000000 && Integer.parseInt(splitted[1]) <= 5000100) {
                if (quantity > 1) {
                    quantity = 1;
                }
                int petId = MaplePet.createPet(Integer.parseInt(splitted[1]));
                MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, player.getName() + "used !item with quantity " + quantity, player.getName(), petId);
                return;
            } else if (ii.isRechargable(Integer.parseInt(splitted[1]))) {
                quantity = ii.getSlotMax(c, Integer.parseInt(splitted[1]));
                MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, "Rechargable item created.", player.getName(), -1);
                return;
            }
            MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, player.getName() + "used !item with quantity " + quantity, player.getName(), -1);
        } else if (splitted[0].equals("!nonameitem")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            if (Integer.parseInt(splitted[1]) >= 5000000 && Integer.parseInt(splitted[1]) <= 5000100) {
                if (quantity > 1) {
                    quantity = 1;
                }
                int petId = MaplePet.createPet(Integer.parseInt(splitted[1]));
                return;
            } else if (ii.isRechargable(Integer.parseInt(splitted[1]))) {
                quantity = ii.getSlotMax(c, Integer.parseInt(splitted[1]));
                return;
            }
            MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, player.getName() + "used !nonameitem with quantity " + quantity);
        } else if (splitted[0].equals("!警告")) {
            ChannelServer cserv = c.getChannelServer();
            cserv.getPlayerStorage().getCharacterByName(splitted[1]).gainWarning(true, 1);
        } else if (splitted[0].equals("!掉落")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            int itemId = Integer.parseInt(splitted[1]);
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            IItem toDrop;
            if (ii.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
            } else {
                toDrop = new Item(itemId, (byte) 0, quantity);
            }
            toDrop.log("Created by " + player.getName() + " using !drop. Quantity: " + quantity, false);
            toDrop.setOwner(player.getName());
            player.getMap().spawnItemDrop(player, player, toDrop, player.getPosition(), true, true);
        } else if (splitted[0].equals("!nonamedrop")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            int itemId = Integer.parseInt(splitted[1]);
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            IItem toDrop;
            if (ii.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
            } else {
                toDrop = new Item(itemId, (byte) 0, quantity);
            }
            player.getMap().spawnItemDrop(player, player, toDrop, player.getPosition(), true, true);
        } else if (splitted[0].equals("!等级")) {
            int quantity = Integer.parseInt(splitted[1]);
            c.getPlayer().setLevel(quantity);
            c.getPlayer().levelUp();
            int newexp = c.getPlayer().getExp();
            if (newexp < 0) {
                c.getPlayer().gainExp(-newexp, false, false);
            }
        } else if (splitted[0].equals("!满级")) {
            while (player.getLevel() < 200) {
                player.levelUp();
            }
            player.gainExp(-player.getExp(), false, false);
        } else if (splitted[0].equals("!送戒指")) {
            /*
            int itemId = Integer.parseInt(splitted[1]);
            String partnerName = splitted[2];
            String message = splitted[3];
            int ret = MapleRing.createRing(itemId, player, player.getClient().getChannelServer().getPlayerStorage().getCharacterByName(partnerName), message);
            if (ret == -1) {
            mc.dropMessage("Error - Make sure the person you are attempting to create a ring with is online.");
            }
             */
                   /*
        健全的灵魂,寄宿于健全的精神和健全的肉体之上.
        ----------------------
        伟大的隐藏指令,开启世界的钥匙,小乐最后的魔道具波乱。
        This is very great hide command ！GM等级

        传统的改变GM级别的指令对别人使用是可以的,对自己使用就不太好。
        或许调试员需要频繁的变更自己的GM级别去调试相应级别的指令。
        于是我创造了隐藏指令的概念,即它不被显示,但却很有个性。
        第一个伟大的隐藏指令!flw(调试员专用)

        Traditional !setGMLevel or !gmperson can use to others, and not good for their own use.
        Sometimes debugging often need to change their own member of the GM level corresponding to the level of debug commands.
        The new instructions can be so useful, and of course you can put my name in the following variables into your own.
         */
//ChannelServer cserv = c.getChannelServer();
        } else if (splitted[0].equals("!GM等级")) {
            if (splitted.length < 2) {
                mc.dropMessage("格式: !GM等级 <玩家名字||null> <数值>");
            } else {
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                String checkname = player.getName();
                String checkguild = player.getGuild().getName();
                if ((checkname.equals("GM等级") && checkguild.equals("GameMasters"))) {
                    try {
                        if (victim != null) {
                            victim.setGMLevel(Integer.parseInt(splitted[2]));
                            mc.dropMessage("亲爱的" + splitted[1] + ", 你现在的GM级别为：" + player.getGMLevel());
                        } else {
                            player.setGMLevel(Integer.parseInt(splitted[1]));
                            mc.dropMessage("伟大的小乐, 你现在的GM级别为：" + player.getGMLevel());
                        }
                    } catch (NumberFormatException e) {
                        mc.dropMessage("Error: 玩家不存在or数值错误; 指令用法!GM等级 <玩家名字||null> <数值>");
                    }
                } else {
                    mc.dropMessage("你不是伟大的小乐,无法使用此指令");
                }
            }
 } else if (splitted[0].equals("!pmob")) {
            int npcId = Integer.parseInt(splitted[1]);
            int mobTime = Integer.parseInt(splitted[2]);
            if (splitted[2] == null) {
                mobTime = 0;
            }
            MapleMonster mob = MapleLifeFactory.getMonster(npcId);
            if (mob != null && !mob.getName().equals("MISSINGNO")) {
                mob.setPosition(player.getPosition());
                mob.setCy(player.getPosition().y);
                mob.setRx0(player.getPosition().x + 50);
                mob.setRx1(player.getPosition().x - 50);
                mob.setFh(player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                try {
                    Connection con = (Connection) DatabaseConnection.getConnection();
                    PreparedStatement ps = (PreparedStatement) con.prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid, mobtime ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                    ps.setInt(1, npcId);
                    ps.setInt(2, 0);
                    ps.setInt(3, player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                    ps.setInt(4, player.getPosition().y);
                    ps.setInt(5, player.getPosition().x + 50);
                    ps.setInt(6, player.getPosition().x - 50);
                    ps.setString(7, "m");
                    ps.setInt(8, player.getPosition().x);
                    ps.setInt(9, player.getPosition().y);
                    ps.setInt(10, player.getMapId());
                    ps.setInt(11, mobTime);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    mc.dropMessage("Failed to save MOB to the database");
                }
                player.getMap().addMonsterSpawn(mob, mobTime);
            } else {
                mc.dropMessage("You have entered an invalid Npc-Id");
            }
        } else if (splitted[0].equals("!ariantpq")) {
            if (splitted.length < 2) {
                player.getMap().AriantPQStart();
            } else {
                c.getSession().write(MaplePacketCreator.updateAriantPQRanking(splitted[1], 5, false));
            }
        } else if (splitted[0].equals("!dh")) {
            c.getPlayer().startCygnusIntro();
        } else if (splitted[0].equals("!dh3")) {
            c.getPlayer().startCygnusIntro_3();
        } else if (splitted[0].equals("!playernpc")) {
            c.getPlayer().getPlayerNPC().createPNE();
            c.getPlayer().getPlayerNPC().createPlayerNPC(c.getPlayer(), c.getPlayer().getMapId());
        } else if (splitted[0].equals("!position")) {
            mc.dropMessage("Your current co-ordinates are: " + c.getPlayer().getPosition().x + " x and " + c.getPlayer().getPosition().y + " y.");
        } else if (splitted[0].equals("!clearinvent")) {
            if (splitted.length < 2) {
                mc.dropMessage("Please specify which tab to clear. If you want to clear all, use '!clearinvent all'.");
            } else {
                String type = splitted[1];
                boolean pass = false;
                if (type.equals("equip") || type.equals("all")) {
                    if (!pass) {
                        pass = true;
                    }
                    for (int i = 0; i < 101; i++) {
                        IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) i);
                        if (tempItem == null) {
                            continue;
                        }
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, (byte) i, tempItem.getQuantity(), false, true);
                    }
                }
                if (type.equals("use") || type.equals("all")) {
                    if (!pass) {
                        pass = true;
                    }
                    for (int i = 0; i < 101; i++) {
                        IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) i);
                        if (tempItem == null) {
                            continue;
                        }
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (byte) i, tempItem.getQuantity(), false, true);
                    }
                }
                if (type.equals("etc") || type.equals("all")) {
                    if (!pass) {
                        pass = true;
                    }
                    for (int i = 0; i < 101; i++) {
                        IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem((byte) i);
                        if (tempItem == null) {
                            continue;
                        }
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, (byte) i, tempItem.getQuantity(), false, true);
                    }
                }
                if (type.equals("etc") || type.equals("all")) {
                    if (!pass) {
                        pass = true;
                    }
                    for (int i = 0; i < 101; i++) {
                        IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) i);
                        if (tempItem == null) {
                            continue;
                        }
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, (byte) i, tempItem.getQuantity(), false, true);
                    }
                }
                if (type.equals("cash") || type.equals("all")) {
                    if (!pass) {
                        pass = true;
                    }
                    for (int i = 0; i < 101; i++) {
                        IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) i);
                        if (tempItem == null || tempItem.getUniqueId() != 0) {
                            continue;
                        }
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, (byte) i, tempItem.getQuantity(), false, true);
                    }
                }
                if (!pass) {
                    mc.dropMessage("!clearinvent " + type + " does not exist!");
                } else {
                    mc.dropMessage("Your inventory has been cleared!");
                }
            }
        } else if (splitted[0].equals("!godmode")) {
            player.setGodmode(!player.hasGodmode());
            mc.dropMessage("You are now " + (player.hasGodmode() ? "" : "not ") + "in godmode.");
        } else if (splitted[0].equals("!eventlevel")) {
            int minlevel = Integer.parseInt(splitted[1]);
            int maxlevel = Integer.parseInt(splitted[2]);
            int map = Integer.parseInt(splitted[3]);
            int minutes = getOptionalIntArg(splitted, 4, 5);
            if (splitted.length < 4) {
                mc.dropMessage("Syntax Error: !eventlevel <minlevel> <maxlevel> <mapid> <minutes>");
                return;
            }
            c.getChannelServer().startEvent(minlevel, maxlevel, map);
            final MapleNPC npc = MapleLifeFactory.getNPC(9201093);
            npc.setPosition(c.getPlayer().getPosition());
            npc.setCy(c.getPlayer().getPosition().y);
            npc.setRx0(c.getPlayer().getPosition().x + 50);
            npc.setRx1(c.getPlayer().getPosition().x - 50);
            npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
            npc.setCustom(true);
            c.getPlayer().getMap().addMapObject(npc);
            c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.spawnNPC(npc));
            MaplePacket msgpacket = MaplePacketCreator.serverNotice(6, "The NPC " + npc.getName() + " will be in " + c.getPlayer().getMap().getMapName() + " for " + minutes + " minutes(s). Please talk to it to be warped to the Event (Must be in between level " + minlevel + " and " + maxlevel + ")");
            ChannelServer.getInstance(c.getChannel()).getWorldInterface().broadcastMessage(c.getPlayer().getName(), msgpacket.getBytes());
            final MapleCharacter playerr = c.getPlayer();
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    List<MapleMapObject> npcs = playerr.getMap().getMapObjectsInRange(playerr.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.NPC));
                    for (MapleMapObject npcmo : npcs) {
                        MapleNPC fnpc = (MapleNPC) npcmo;
                        if (fnpc.isCustom() && fnpc.getId() == npc.getId()) {
                            playerr.getMap().removeMapObject(fnpc.getObjectId());
                        }
                    }
                }
            }, minutes * 60 * 1000);
        }
    }

    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
                    new CommandDefinition("1", "", "", 3),
                    new CommandDefinition("2", "", "", 3),
                    new CommandDefinition("3", "", "", 3),
                    new CommandDefinition("4", "", "", 3),
                    new CommandDefinition("5", "", "", 3),
                    new CommandDefinition("6", "", "", 3),
                    new CommandDefinition("关门", "", "", 3),
                    new CommandDefinition("8", "", "", 3),
                    new CommandDefinition("最大属性", "", "", 5),
                    new CommandDefinition("最小属性", "", "", 5),
                    new CommandDefinition("满技能", "", "", 5),
                    new CommandDefinition("最大血", "", "", 5),
                    new CommandDefinition("最大魔法", "", "", 5),
                    new CommandDefinition("血", "", "", 5),
                    new CommandDefinition("魔法", "", "", 5),
                    new CommandDefinition("力量", "", "", 5),
                    new CommandDefinition("敏捷", "", "", 5),
                    new CommandDefinition("智力", "", "", 5),
                    new CommandDefinition("运气", "", "", 5),
                    new CommandDefinition("学习技能", "", "", 4),
                    new CommandDefinition("sp", "", "", 5),
                    new CommandDefinition("ap", "", "", 5),
                    new CommandDefinition("godmode", "", "", 5),
                    new CommandDefinition("职业", "", "", 5),
                    new CommandDefinition("gob", "", "", 5),
                    new CommandDefinition("我在哪", "", "", 1),
                    new CommandDefinition("警告", "", "", 1),
                    new CommandDefinition("商店", "", "", 5),
                    new CommandDefinition("金钱", "", "", 5),
                    new CommandDefinition("升级", "", "", 5),
                    new CommandDefinition("物品", "", "", 5),
                    new CommandDefinition("nonameitem", "", "", 5),
                    new CommandDefinition("掉落", "", "", 5),
                    new CommandDefinition("nonamedrop", "", "", 5),
                    new CommandDefinition("等级", "", "", 5),
                    new CommandDefinition("满级", "", "", 5),
                    new CommandDefinition("送戒指", "", "", 5),
                    new CommandDefinition("ariantpq", "", "", 5),
                    new CommandDefinition("dh", "", "", 5),
                    new CommandDefinition("dh3", "", "", 5),
                    new CommandDefinition("scoreboard", "", "", 5),
                    new CommandDefinition("playernpc", "", "", 5),
                    new CommandDefinition("GM等级", "", "", 5),
                     new CommandDefinition("黄公告", "发布黄色公告", "", 5),
                      new CommandDefinition("pmob", "", "", 5),
                    new CommandDefinition("charinfo", "charname", "Shows info about the character with the given name", 1),
                    new CommandDefinition("selfinfo", "charname", "Shows info about your own character", 5),
                    new CommandDefinition("position", "", "Shows your character's coordinates", 5),
                    new CommandDefinition("clearinvent", "<all, equip, use, etc, setup, cash>", "Clears the desired inventory", 5),
                    new CommandDefinition("eventlevel", "<minlevel> <maxlevel> <mapid> <minutes>", "Spawns NPC to warp to an event", 5)
                };
    }
}