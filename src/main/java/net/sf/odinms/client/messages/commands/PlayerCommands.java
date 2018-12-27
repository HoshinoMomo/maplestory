
package net.sf.odinms.client.messages.commands;

import java.awt.Point;
import java.sql.SQLException;
import net.sf.odinms.server.MapleAchievements;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.MaplePacketCreator;

public class PlayerCommands implements Command {
	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
																					IllegalCommandSyntaxException {
	MapleCharacter player = c.getPlayer();
        ChannelServer cserv = c.getChannelServer();

	 if(splitted[0].equals("@导游")) {
		NPCScriptManager npc = NPCScriptManager.getInstance();
		npc.start(c, 9000020);
        } else if (splitted[0].equals("@str") || splitted[0].equals("@int") || splitted[0].equals("@luk") || splitted[0].equals("@dex")) {
            int amount = Integer.parseInt(splitted[1]);
		boolean str = splitted[0].equals("@str");
		boolean Int = splitted[0].equals("@int");
		boolean luk = splitted[0].equals("@luk");
		boolean dex = splitted[0].equals("@dex");
          if(amount > 0 && amount <= player.getRemainingAp() && amount <= 32763 || amount < 0 && amount >= -32763 && Math.abs(amount) + player.getRemainingAp() <= 32767) {
		if (str && amount + player.getStr() <= 32767 && amount + player.getStr() >= 4) {
		player.setStr(player.getStr() + amount);
		player.updateSingleStat(MapleStat.STR, player.getStr());
		player.setRemainingAp(player.getRemainingAp() - amount);
		player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
	} else if (Int && amount + player.getInt() <= 32767 && amount + player.getInt() >= 4) {
		player.setInt(player.getInt() + amount);
		player.updateSingleStat(MapleStat.INT, player.getInt());
		player.setRemainingAp(player.getRemainingAp() - amount);
		player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
	} else if (luk && amount + player.getLuk() <= 32767 && amount + player.getLuk() >= 4) {
		player.setLuk(player.getLuk() + amount);
		player.updateSingleStat(MapleStat.LUK, player.getLuk());
		player.setRemainingAp(player.getRemainingAp() - amount);
		player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
	} else if (dex && amount + player.getDex() <= 32767 && amount + player.getDex() >= 4) {
		player.setDex(player.getDex() + amount);
		player.updateSingleStat(MapleStat.DEX, player.getDex());
		player.setRemainingAp(player.getRemainingAp() - amount);
		player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
	} else {
	mc.dropMessage("请确保你的某个属性值不超过32767点！.");
	}
		} else {
			mc.dropMessage("请确保你的某个属性值不超过32767点！并且有足够的点加！.");
		}
	} else if (splitted[0].equals("@emo")) {
		player.setHp(0);
		player.updateSingleStat(MapleStat.HP, 0);
	} else if (splitted[0].equals("@清零")) {
                player.setExp(0);
                player.updateSingleStat(MapleStat.EXP, player.getExp());
		mc.dropMessage("经验清理完毕！");
	} else if (splitted[0].equals("@tongji")){
		mc.dropMessage("你目前有： " + c.getPlayer().getStr() + " 点力量, " + c.getPlayer().getDex() + " 点敏捷, " + c.getPlayer().getLuk() + " 点运气, " + c.getPlayer().getInt() + " 点智力.");
		mc.dropMessage("你目前有 " + c.getPlayer().getRemainingAp() + " 属性点！");
	 } else if (splitted[0].equalsIgnoreCase("@帮助")) {
                mc.dropMessage("============================================================");
                mc.dropMessage("     你可以使用所有命令如下表单：");
                mc.dropMessage("============================================================");
                mc.dropMessage("@str/@dex/@int/@luk <数量> 快速加属性点!.");
                mc.dropMessage("@save   在线存档！.");
                mc.dropMessage("@导游   打开导游！.");
                mc.dropMessage("@卡NPC  解决NPC不能对话的命令！.");
                mc.dropMessage("@自由  一键回到自由市场！卡地图不能出去使用！.");
                mc.dropMessage("@清零 在线经验清零！");
                mc.dropMessage("@统计 查看自己的数据统计！");
                mc.dropMessage("@改名    更改名字.");
                mc.dropMessage("$vip 查看VIP指令！.");
                } else if (splitted[0].equals("@改名")) {
            if (player.getMeso() > 1000000000 && MapleCharacterUtil.canCreateChar(splitted[1], 0)) {
                player.setName(splitted[1]);
                c.getSession().write(MaplePacketCreator.getCharInfo(player));
                player.getMap().removePlayer(player);
                player.getMap().addPlayer(player);
                mc.dropMessage("成功改名.");
            } else {
                mc.dropMessage("你需要10E冒险币去更改你的名字.");
            }
       } else if (splitted[0].equals("@banben")) {
            mc.dropMessage("小乐冒险岛服务端\r\n次端制作者者：小乐。QQ：390824898");


        } else if (splitted[0].equals("@自由")) {

            if ((c.getPlayer().getMapId() < 910000000) || (c.getPlayer().getMapId() > 910000022)){
               new ServernoticeMapleClientMessageCallback(6, c).dropMessage("欢迎来到自由市场，回市场的命令是@自由 哦！");
               c.getSession().write(MaplePacketCreator.enableActions());
                 MapleMap to;
                 MaplePortal pto;
                to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(910000000);
                c.getPlayer().saveLocation("FREE_MARKET");//取消
                pto = to.getPortal("out00");
                c.getPlayer().changeMap(to, pto);
             } else {
              new ServernoticeMapleClientMessageCallback(5, c).dropMessage("你已经在自由市场了，还想出去不成？.");
               c.getSession().write(MaplePacketCreator.enableActions());
             }


	
        } else if (splitted[0].equals("@save")) {
                if (!player.getCheatTracker().Spam(60000, 0)) { // 1 minute
                    player.saveToDB(true);
                    mc.dropMessage("信息已经成功存档！");
                } else {
                    mc.dropMessage("你每分钟只能保存一次！");
                }
	} else if (splitted[0].equalsIgnoreCase("@achievements")) {
            mc.dropMessage("Your finished achievements:");
            for (Integer i : c.getPlayer().getFinishedAchievements()) {
                mc.dropMessage(MapleAchievements.getInstance().getById(i).getName() + " - " + MapleAchievements.getInstance().getById(i).getReward() + " NX.");
            }
        } else if (splitted[0].equals("@卡NPC")) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.getSession().write(MaplePacketCreator.enableActions());
                    mc.dropMessage("已解决无法和NPC对话的问题");
   
   
                }
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[] {
                    new CommandDefinition("统计", "", "See myap from anywhere", 0),
                    new CommandDefinition("清零", "", "Fixed negative exp", 0),
                    new CommandDefinition("帮助", "", "Does Sexual Commands", 0),
                    new CommandDefinition("save", "", "S3xual So S3xual Saves UR ACC", 0),
                    new CommandDefinition("str", "<amount>", "Sets your strength to a higher amount if you have enough AP or takes it away if you aren't over 32767 AP.", 0),
                    new CommandDefinition("int", "<amount>", "Sets your intelligence to a higher amount if you have enough AP or takes it away if you aren't over 32767 AP.", 0),
                    new CommandDefinition("luk", "<amount>", "Sets your luck to a higher amount if you have enough AP or takes it away if you aren't over 32767 AP.", 0),
                    new CommandDefinition("dex", "<amount>", "Sets your dexterity to a higher amount if you have enough AP or takes it away if you aren't over 32767 AP.", 0),
                    new CommandDefinition("自由", "", "一键回程", 0),
                    new CommandDefinition("卡NPC", "", "Stuck", 0),
                    new CommandDefinition("导游", "", "Spinel is pregnant", 0),
                    new CommandDefinition("版本", "", "查看版本", 0),
                    new CommandDefinition("改名", "", "改名的", 0),
   

		};
	}

     public static void Fake(Exception e)
  {
    e.toString();
  }

    }