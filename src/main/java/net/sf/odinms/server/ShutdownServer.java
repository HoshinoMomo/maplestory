package net.sf.odinms.server;

import net.sf.odinms.database.pool.InitHikariCP;
import net.sf.odinms.handling.channel.ChannelServer;
import net.sf.odinms.handling.login.LoginServer;
import net.sf.odinms.handling.world.World.Alliance;
import net.sf.odinms.handling.world.World.Broadcast;
import net.sf.odinms.handling.world.World.Family;
import net.sf.odinms.handling.world.World.Guild;
import net.sf.odinms.server.timer.BuffTimer;
import net.sf.odinms.server.timer.CloneTimer;
import net.sf.odinms.server.timer.EtcTimer;
import net.sf.odinms.server.timer.EventTimer;
import net.sf.odinms.server.timer.MapTimer;
import net.sf.odinms.server.timer.PingTimer;
import net.sf.odinms.server.timer.WorldTimer;
import net.sf.odinms.tools.MaplePacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ShutdownServer extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownServer.class);
    private static final String ANNOUNCE = "MapleStory World will stop, please get offline.";
    private static final ShutdownServer instance = new ShutdownServer();
    public static boolean running = false;
    public int mode = 0;

    private ShutdownServer(){}

    public static ShutdownServer getInstance() {
        return instance;
    }


    @Override
    public void run() {

        //Timer
        WorldTimer.getInstance().stop();
        MapTimer.getInstance().stop();
        BuffTimer.getInstance().stop();
        CloneTimer.getInstance().stop();
        EventTimer.getInstance().stop();
        EtcTimer.getInstance().stop();
        PingTimer.getInstance().stop();

        //Merchant
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.closeAllMerchant();
        }

        //Guild
        Guild.save();
        //Alliance
        Alliance.save();
        //Family
        Family.save();

        Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, ANNOUNCE));
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.setServerMessage(ANNOUNCE);
        }

        Set<Integer> channels = ChannelServer.getAllInstance();
        for (Integer channel : channels) {
            ChannelServer cs = ChannelServer.getInstance(channel);
            cs.saveAll();
            cs.setFinishShutdown();
            cs.shutdown();
        }

        LoginServer.shutdown();

        InitHikariCP.close();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("关闭服务端错误 - 2" + e);
        }
        System.exit(0);

    }
}
