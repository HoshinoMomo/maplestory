package net.sf.odinms.client.messages.commands;

import java.util.Collection;
import java.util.HashMap;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapFactory;

public class vipCommand
{
  public static boolean executevipCommand(MapleClient c, MessageCallback mc, String line) {
 //public static boolean executeVipCommand(MapleClient c, MessageCallback mc, String line) {
        String[] splitted = line.split(" ");
        ChannelServer cserv = c.getChannelServer();
        MapleCharacter player = c.getPlayer();
        if (splitted[0].equals("$移动")) {

if (splitted.length < 2&&c.getPlayer().vip >= 1){

     mc.dropMessage("指令用法: $移动 <地图名字>");
    mc.dropMessage("南港<南港>, 彩虹村<彩虹村>, 射手<射手村>, 魔法<魔法密林>,");
    mc.dropMessage("勇士<勇士部落>, 废气<废气都市>, 明珠港<明珠港>, 林中之城<林中之城>, 海滩<黄金海滩>,");
   mc.dropMessage("天空<天空之城>, 幸福村<幸福村>, 雪域<冰封雪域>, 玩具<玩具城>, 海底<海底世界>,");
   mc.dropMessage("神木<神木村>, 武陵<武陵村>, 百草堂<百草堂>, 总部<地球防卫总部>, 童话<童话村>,");
   mc.dropMessage("新叶<新叶城>, 考古<考古发掘地>, 鱼王<鱼王>, 蘑茹王<蘑茹王>, ");
                       mc.dropMessage("胖凤<胖凤>, 肥龙<肥龙>, 火马<火马>, 蝙蝠<蝙蝠魔>, ");
                       mc.dropMessage("昭和<昭和村>, 家族<家族>, 樱花<樱花村>, fm<自由市场>, 骨龙<骨龙>, PK<PK地图>");
}else{
    HashMap<String, Integer> gotomaps = new HashMap<String, Integer>();
    gotomaps.put("南港", 60000);
    gotomaps.put("彩虹村", 1010000);
    gotomaps.put("射手", 100000000);
    gotomaps.put("魔法", 101000000);
    gotomaps.put("勇士", 102000000);
    gotomaps.put("废气", 103000000);
    gotomaps.put("明珠港", 104000000);
    gotomaps.put("林中之城", 105040300);
    gotomaps.put("海滩", 110000000);
    gotomaps.put("天空", 200000000);
    gotomaps.put("幸福村", 209000000);
    gotomaps.put("雪域", 211000000);
    gotomaps.put("玩具", 220000000);
    gotomaps.put("海底", 230000000);
    gotomaps.put("神木", 240000000);
    gotomaps.put("武陵", 250000000);
    gotomaps.put("百草堂", 251000000);
    gotomaps.put("总部", 221000000);
    gotomaps.put("童话", 222000000);
    gotomaps.put("新叶", 600000000);
    gotomaps.put("考古", 990000000);
    gotomaps.put("鱼王", 230040420);
    gotomaps.put("蘑茹王", 100000005);
    gotomaps.put("胖凤", 240020101);
    gotomaps.put("肥龙", 240020401);
    gotomaps.put("火马", 682000001);
    gotomaps.put("蝙蝠", 105090900);
    gotomaps.put("邵和", 801000000);
    gotomaps.put("家族", 200000301);
    gotomaps.put("樱花", 800000000);
    gotomaps.put("自由", 910000000);
    gotomaps.put("骨龙", 240040511);
    gotomaps.put("PK", 800020400);

    if (gotomaps.containsKey(splitted[1])){

        MapleMap target = cserv.getMapFactory().getMap(gotomaps.get(splitted[1]));
        MaplePortal targetPortal = target.getPortal(0);
        player.changeMap(target, targetPortal);

    }else{
        mc.dropMessage("没有这样的地方 !!! 或者你不是VIP无法使用");
    }
    }

} else if (splitted[0].equals("$vip地图")) {
            if (c.getPlayer().vip >= 2) {
            MapleMap target = cserv.getMapFactory().getMap(920010000);
            MaplePortal targetPortal = target.getPortal(0);
            player.changeMap(target, targetPortal);
            }else{
            mc.dropMessage("您不是VIP2，不能使用此命令.");
            }

   } else if (splitted[0].equals("$治愈")) {
        if (c.getPlayer().vip >= 3) {
            player.setHp(player.getMaxHp());
            player.updateSingleStat(MapleStat.HP, player.getMaxHp());
            player.setMp(player.getMaxMp());
            player.updateSingleStat(MapleStat.MP, player.getMaxMp());
             }else{
            mc.dropMessage("您不是VIP3，不能使用此命令.");
             }}
          else if (splitted[0].equals("$vip扎昆")) {
      if (c.getPlayer().getVip() >= 3) {
        MapleMap target = cserv.getMapFactory().getMap(280030000);
        MaplePortal targetPortal = target.getPortal(0);
        player.changeMap(target, targetPortal);
      } else {
        mc.dropMessage("您不是VIP3，不能使用此命令.");
      }
    } else if (splitted[0].equals("$vip闹钟")) {
      if (c.getPlayer().getVip() >= 3) {
        MapleMap target = cserv.getMapFactory().getMap(220080001);
        MaplePortal targetPortal = target.getPortal(0);
        player.changeMap(target, targetPortal);
      } else {
        mc.dropMessage("您不是VIP3，不能使用此命令.");
      }
} else if (splitted[0].equals("$vip")) {
    if (c.getPlayer().vip >= 1) {
     mc.dropMessage("尊贵的VIP，以下是您的专属命令帮助.");
     mc.dropMessage("$vip地图   -----传送到VIP地图【VIP2】");
     mc.dropMessage("$移动      -----快捷传送.");
     mc.dropMessage("$治愈      -----VIP补满血魔");
     mc.dropMessage("$在线人数-----VIP免费查在线.");
     mc.dropMessage("$vip闹钟-----去闹钟地图刷BOSS.");
     mc.dropMessage("$vip扎昆-----去扎昆地图刷");
      }else{
            mc.dropMessage("您不是VIP，不能使用此命令.");
            }
	} else if (splitted[0].equals("$在线人数") && c.getPlayer().vip >= 2) {
            if (c.getPlayer().getMeso() < 0) {
                        mc.dropMessage("{ VIP指令 } 每次执行该命令系统将自动扣除您的查看费用0游戏币！");
                    } else {
			mc.dropMessage("[系统信息] 频道服务器 " + c.getChannel() + " 当前状态:");
			Collection<MapleCharacter> chrs = c.getChannelServer().getInstance(c.getChannel()).getPlayerStorage().getAllCharacters();
			for (MapleCharacter chr : chrs) {
				mc.dropMessage("[系统信息] 角色ID：" + chr.getId() + " 角色名：" + chr.getName() + " :所在地图ID: " + chr.getMapId());
			}
			mc.dropMessage("[系统信息] 频道服务器 " + c.getChannel() + "  当前总计： " + chrs.size() + "人在线.");
            }
        } else {
            if (player.vip >= 1) {
                mc.dropMessage("您输入的vip命令 " + splitted[0] + " 不存在.");
            }
            return false;
        }
        return true;
    }
}