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
package net.sf.odinms.client.messages;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.commands.AdminCommand;
import net.sf.odinms.client.messages.commands.CommandExecute;
import net.sf.odinms.client.messages.commands.CommandObject;
import net.sf.odinms.client.messages.commands.GMCommand;
import net.sf.odinms.client.messages.commands.InternCommand;
import net.sf.odinms.client.messages.commands.PlayerCommand;
import net.sf.odinms.constants.ServerConstants;
import net.sf.odinms.constants.ServerConstants.CommandType;
import net.sf.odinms.constants.ServerConstants.PlayerGMRank;
import net.sf.odinms.database.pool.HikariCPProxy;
import net.sf.odinms.tools.FileoutputUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static net.sf.odinms.constants.ServerConstants.CommandType.NORMAL;

/**
 * @author odinms
 * rewrite by EasyZhang
 */
public class CommandProcessor {
    private final static Logger logger = LoggerFactory.getLogger(CommandProcessor.class);

    private final static HashMap<String, CommandObject> commands = new HashMap<String, CommandObject>();
    private final static HashMap<Integer, ArrayList<String>> commandList = new HashMap<Integer, ArrayList<String>>();
    private final static String NO_PATTERN_FOUND = "输入的玩家命令不存在,可以使用 @help 来查看指令.";

    static {

        Class[] CommandFiles = {
                PlayerCommand.class, GMCommand.class, InternCommand.class, AdminCommand.class
        };

        for (Class<?> clazz : CommandFiles) {
            try {
                PlayerGMRank rankNeeded =
                        (PlayerGMRank) clazz.getMethod("getPlayerLevelRequired", new Class[]{}).invoke(null, (Object[]) null);
                Class<?>[] innerClasses = clazz.getDeclaredClasses();
                ArrayList<String> cL = new ArrayList<>();
                for (Class<?> innerClass : innerClasses) {
                    try {
                        if (!Modifier.isAbstract(innerClass.getModifiers()) && !innerClass.isSynthetic()) {
                            Object object = innerClass.newInstance();
                            if (object instanceof CommandExecute) {
                                cL.add(rankNeeded.getCommandPrefix() + innerClass.getSimpleName().toLowerCase());
                                commands.put(rankNeeded.getCommandPrefix() + innerClass.getSimpleName().toLowerCase(),
                                        new CommandObject(rankNeeded.getCommandPrefix() + innerClass.getSimpleName().toLowerCase(),
                                                (CommandExecute) object, rankNeeded.getLevel()));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
                    }
                }
                Collections.sort(cL);
                commandList.put(rankNeeded.getLevel(), cL);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
            }
        }
    }

    private static void sendDisplayMessage(MapleClient c, String msg, CommandType type) {
        if (c.getPlayer() == null) {
            return;
        }
        if (type == NORMAL) {
            c.getPlayer().dropMessage(6, msg);
        } else {
            c.getPlayer().dropMessage(-2, "錯誤 : " + msg);
        }
    }

    public static boolean processCommand(MapleClient c, String line, CommandType type) {
        if (line.charAt(0) == ServerConstants.PlayerGMRank.NORMAL.getCommandPrefix()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();

            CommandObject commandObject = commands.get(splitted[0]);

            if (commandObject == null || commandObject.getType() != type) {
                sendDisplayMessage(c, NO_PATTERN_FOUND, type);
                return true;
            }
            try {
                int ret = commandObject.execute(c, splitted);
                if (ret == CommandResult.ERROR) {
                    sendDisplayMessage(c, NO_PATTERN_FOUND, type);
                }
            } catch (Exception e) {
                sendDisplayMessage(c, NO_PATTERN_FOUND, type);
                if (c.getPlayer().isGM()) {
                    sendDisplayMessage(c, "错误: " + e, type);
                }
            }
            return true;
        }

        if (c.getPlayer().getGMLevel() > ServerConstants.PlayerGMRank.NORMAL.getLevel()) {
            //Redundant for now, but in case we change symbols later. This will become extensible.
            if (line.charAt(0) == ServerConstants.PlayerGMRank.GM.getCommandPrefix() ||
                    line.charAt(0) == ServerConstants.PlayerGMRank.ADMIN.getCommandPrefix() ||
                    line.charAt(0) == ServerConstants.PlayerGMRank.INTERN.getCommandPrefix()) {
                String[] splitted = line.split(" ");
                splitted[0] = splitted[0].toLowerCase();
                //GM Commands
                if (line.charAt(0) == '!') {
                    CommandObject co = commands.get(splitted[0]);
                    if ("!help".equals(splitted[0])) {
                        dropHelp(c);
                        return true;
                    } else if (co == null || co.getType() != type) {
                        sendDisplayMessage(c, "输入的命令不存在.", type);
                        return true;
                    }
                    if (c.getPlayer().getGMLevel() >= co.getReqGMLevel()) {
                        int ret = co.execute(c, splitted);
                        //incase d/c after command or something
                        if (ret > 0 && c.getPlayer() != null) {
                            logGMCommandToDB(c.getPlayer(), line);
                            logger.info("[ {} ] 使用了指令: {}" + line, c.getPlayer().getName(), line);
                        }
                    } else {
                        sendDisplayMessage(c, "您的权限等级不足以使用次命令.", type);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static void logGMCommandToDB(MapleCharacter player, String command) {

        PreparedStatement ps = null;
        try {
            ps = HikariCPProxy.execute("INSERT INTO gmlog (cid, name, command, mapid, ip) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, player.getId());
            ps.setString(2, player.getName());
            ps.setString(3, command);
            ps.setInt(4, player.getMap().getId());
            ps.setString(5, player.getClient().getSessionIPAddress());
            ps.executeUpdate();
        } catch (SQLException ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ex);
            ex.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                /*
                 * Err.. Fuck?
                 */
            }
        }
    }

    private static void dropHelp(MapleClient c) {
        final StringBuilder sb = new StringBuilder("指令列表: ");
        for (int i = 0; i <= c.getPlayer().getGMLevel(); i++) {
            if (commandList.containsKey(i)) {
                for (String s : commandList.get(i)) {
                    sb.append(s);
                    sb.append(" ");
                }
            }
        }
        c.getPlayer().dropMessage(6, sb.toString());
    }
}