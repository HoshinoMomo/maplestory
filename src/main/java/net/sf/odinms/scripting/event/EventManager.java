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

package net.sf.odinms.scripting.event;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptException;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.server.MapleSquad;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.maps.MapleMap;
import java.util.Iterator;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.tools.MaplePacketCreator;
/**
 *
 * @author Matze
 */
public class EventManager {

    private Invocable iv;
    private ChannelServer cserv;
    private Map<String, EventInstanceManager> instances = new HashMap<String, EventInstanceManager>();
    private Properties props = new Properties();
    private String name;
private MapleCharacter chr;

    public EventManager(ChannelServer cserv, Invocable iv, String name) {
        this.iv = iv;
        this.cserv = cserv;
        this.name = name;
    }
public int getHour() {
    Calendar cal = Calendar.getInstance();
    int hour = cal.get(11);
    return hour;
  }
public void zidonglaba(String Text)
        {
            if (Text.isEmpty())
		{
			chr.dropMessage("[注意]文字过长，不能发送，最长为20个字！");
			return;
		}
		for (Iterator n$ = ChannelServer.getAllInstances().iterator(); n$.hasNext();)
		{
			ChannelServer cservs = (ChannelServer)n$.next();
			Iterator i$ = cservs.getPlayerStorage().getAllCharacters().iterator();
			while (i$.hasNext())
			{
				MapleCharacter players = (MapleCharacter)i$.next();
                                if (getHour() >= 0 || getHour() >= 5 || getHour() >= 10 || getHour() >=15 || getHour() >= 20)
                                    Text = "小乐冒险岛活动多多精彩多多.小乐祝大家游戏愉快!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("小乐管理员").append(":").append(Text).toString(), 5120015, true));
                                if (getHour() >= 1 || getHour() >= 6 || getHour() >= 11 || getHour() >=16 || getHour() >= 21)
                                    Text = "让我们一起拒绝外挂,一起携手打造美好的冒险环境!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("小乐管理员").append(":").append(Text).toString(), 5120015, true));
                                if (getHour() >= 2 || getHour() >= 7 || getHour() >= 12 || getHour() >= 17 || getHour() >= 22)
                                    Text = "祝大家能够在小乐冒险岛里玩的开心,好友成群!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("小乐管理员").append(":").append(Text).toString(), 5120015, true));
                                if (getHour() >= 3 || getHour() >= 8 || getHour() >= 13 || getHour() >= 18 || getHour() >= 23)
                                    Text = "小乐冒险岛有你们的支持,我们会做得更好!有bug要联系GM哦~";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("小乐管理员").append(":").append(Text).toString(), 5120015, true));
                                if (getHour() >= 4 || getHour() >= 9 || getHour() >= 14 || getHour() >= 19 || getHour() >= 24)
                                    Text = "欢迎来到小乐冒险岛,2线为全线PK频道,PK死后要掉装备和钱钱哟!";
                                       players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("MapleMs管理员").append(":").append(Text).toString(), 5120015, true));
			}
		}

	}

        public void save() {
        for (ChannelServer chan : ChannelServer.getAllInstances()) {
        for (MapleCharacter chr : chan.getPlayerStorage().getAllCharacters()) {
              chr.saveToDB(true);
        }
        }
    }
    public void cancel() {
        try {
            iv.invokeFunction("cancelSchedule", (Object) null);
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void schedule(final String methodName, long delay) {
        TimerManager.getInstance().schedule(new Runnable() {
            public void run() {
                try {
                    iv.invokeFunction(methodName, (Object) null);
                } catch (ScriptException ex) {
                    Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, delay);
    }

    public ScheduledFuture<?> scheduleAtTimestamp(final String methodName, long timestamp) {
        return TimerManager.getInstance().scheduleAtTimestamp(new Runnable() {

            public void run() {
                try {
                    iv.invokeFunction(methodName, (Object) null);
                } catch (ScriptException ex) {
                    Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, timestamp);
    }

    public ChannelServer getChannelServer() {
        return cserv;
    }

    public EventInstanceManager getInstance(String name) {
        return instances.get(name);
    }

    public Collection<EventInstanceManager> getInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

    public EventInstanceManager newInstance(String name) {
        EventInstanceManager ret = new EventInstanceManager(this, name);
        instances.put(name, ret);
        return ret;
    }

    public void disposeInstance(String name) {
        instances.remove(name);
    }

    public Invocable getIv() {
        return iv;
    }

    public void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public String getName() {
        return name;
    }

    //PQ method: starts a PQ
    public void startInstance(MapleParty party, MapleMap map) {
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", (Object) null));
            eim.registerParty(party, map);
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startInstance(MapleParty party, MapleMap map, boolean partyid) {
        try {
            EventInstanceManager eim;
            if (partyid) {
                eim = (EventInstanceManager) (iv.invokeFunction("setup", party.getId()));
            } else {
                eim = (EventInstanceManager) (iv.invokeFunction("setup", (Object) null));
            }
            eim.registerParty(party, map);
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startInstance(MapleSquad squad, MapleMap map) {
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", squad.getLeader().getId()));
            eim.registerSquad(squad, map);
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //non-PQ method for starting instance
    public void startInstance(EventInstanceManager eim, String leader) {
        try {
            iv.invokeFunction("setup", eim);
            eim.setProperty("leader", leader);
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //returns EventInstanceManager
    public EventInstanceManager startEventInstance(MapleParty party, MapleMap map, boolean partyid) {
        try {
            EventInstanceManager eim;
            if (partyid) {
                eim = (EventInstanceManager) (iv.invokeFunction("setup", party.getId()));
            } else {
                eim = (EventInstanceManager) (iv.invokeFunction("setup", (Object) null));
            }
            eim.registerParty(party, map);
            return eim;
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
