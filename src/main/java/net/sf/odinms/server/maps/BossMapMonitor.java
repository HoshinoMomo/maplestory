/*
	此文件是 小乐冒险岛 核心服务器文件之一 -<MapleStory Server>
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    此文件的制作者为 OdinMs 团队  以上是开发者的信息
    请不要随意修改否则后果自负
    此2次开发:小乐冒险岛 第1版
    此端基于Aexr端进行修改
    修改制作人员:小乐

    小乐联系QQ:390824898




   您应该已经收到一份拷贝的GNU通用公共许可证Affero程式一起。如果不是，请参阅
    <http://www.gnu.org/licenses/>.
*/
package net.sf.odinms.server.maps;

import net.sf.odinms.server.MaplePortal;
import java.util.concurrent.ScheduledFuture;
import net.sf.odinms.server.TimerManager;

public class BossMapMonitor {

    private MaplePortal portal;
    private MapleMap map;
    private MapleMap pMap;
    private ScheduledFuture<?> schedule;

    public BossMapMonitor(final MapleMap map, MapleMap pMap, MaplePortal portal) {
        this.map = map;
        this.pMap = pMap;
        this.portal = portal;
        schedule = TimerManager.getInstance().register(new Runnable() {

            public void run() {
                if (map.playerCount() <= 0) {
                    BossMapMonitor.this.run();
                }
            }
        }, 10000);
    }

    private void run() {
        map.killAllMonsters(false);
        map.resetReactors();
        pMap.resetReactors();
        portal.setPortalState(MapleMapPortal.OPEN);
        if (map.mobCount() == 0 && map.playerCount() == 0) {
            schedule.cancel(false);
        } else {
            System.out.println("BossMapMonitor is fucked up..");
        }
    }
}
