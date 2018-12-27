package net.sf.odinms.client.messages.commands;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.constants.ServerConstants.PlayerGMRank;
import net.sf.odinms.scripting.NPCScriptManager;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

import java.util.Arrays;

/**
 *
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class STR extends AbstractStatCommands {

        public STR() {
            stat = MapleStat.STR;
        }
    }

    public static class DEX extends AbstractStatCommands {

        public DEX() {
            stat = MapleStat.DEX;
        }
    }

    public static class INT extends AbstractStatCommands {

        public INT() {
            stat = MapleStat.INT;
        }
    }

    public static class LUK extends AbstractStatCommands {

        public LUK() {
            stat = MapleStat.LUK;
        }
    }

    public abstract static class AbstractStatCommands extends CommandExecute {

        protected MapleStat stat = null;
        private static int statLim = 999;

        private void setStat(MapleCharacter player, int amount) {
            switch (stat) {
                case STR:
                    player.getStat().setStr((short) amount);
                    player.updateSingleStat(MapleStat.STR, player.getStat().getStr());
                    break;
                case DEX:
                    player.getStat().setDex((short) amount);
                    player.updateSingleStat(MapleStat.DEX, player.getStat().getDex());
                    break;
                case INT:
                    player.getStat().setInt((short) amount);
                    player.updateSingleStat(MapleStat.INT, player.getStat().getInt());
                    break;
                case LUK:
                    player.getStat().setLuk((short) amount);
                    player.updateSingleStat(MapleStat.LUK, player.getStat().getLuk());
                    break;
                default:
                    break;
            }
        }

        private int getStat(MapleCharacter player) {
            switch (stat) {
                case STR:
                    return player.getStat().getStr();
                case DEX:
                    return player.getStat().getDex();
                case INT:
                    return player.getStat().getInt();
                case LUK:
                    return player.getStat().getLuk();
                default:
                    throw new RuntimeException();
            }
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "数字有问题啊亲.");
                return 0;
            }
            int change = 0;
            try {
                change = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "数字有问题啊亲.");
                return 0;
            }
            if (change <= 0) {
                c.getPlayer().dropMessage(5, "你得输入一个比0大的数.");
                return 0;
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().dropMessage(5, "你没有足够的法力值.");
                return 0;
            }
            if (getStat(c.getPlayer()) + change > statLim) {
                c.getPlayer().dropMessage(5, "属性值最大为 " + statLim + ".");
                return 0;
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()) + change);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().dropMessage(5, StringUtil.makeEnumHumanReadable(stat.name()) + " has been raised by " + change + ".");
            return 1;
        }
    }

    public static class Relive extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(MaplePacketCreator.enableActions());
            c.getPlayer().dropMessage(1, "假死已处理完毕.");
            c.getPlayer().dropMessage(6, "当前延迟 " + c.getPlayer().getClient().getLatency() + " 毫秒");
            return 1;
        }
    }

    /**
     * 存档
     */
    public static class Save extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().saveToDB(false, false);
            c.getPlayer().dropMessage("存档成功");
            return 1;
        }
    }

    /**
     * 领取点券
     */
    public static class GainPoint extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(MaplePacketCreator.enableActions());
            NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 9270034);
            return 1;
        }
    }

    /**
     * 爆率
     */
    public static class Mobdrop extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(MaplePacketCreator.enableActions());
            NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 2000);
            return 1;
        }
    }

    /**
     * 查看怪物
     */
    public static class Mob extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMonster mob = null;
            for (MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000.0D, Arrays.asList(new MapleMapObjectType[]{MapleMapObjectType.MONSTER}))) {
                mob = (MapleMonster) monstermo;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物: " + mob.toString());
                    break;
                }
            }
            if (mob == null) {
                c.getPlayer().dropMessage(6, "查看失败: 1.没有找到需要查看的怪物信息. 2.你周围没有怪物出现. 3.有些怪物禁止查看.");
            }
            return 1;
        }
    }

    public static class Help extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "指令列表 :");
            c.getPlayer().dropMessage(5, "@Relive          < 解除卡怪 >");
            c.getPlayer().dropMessage(5, "@Mob             < 查看怪物 >");
            c.getPlayer().dropMessage(5, "@GainPoint       < 充值领取点券 >");
            c.getPlayer().dropMessage(5, "@Save            < 储存当前人物信息 >");
            return 1;
        }
    }
}
