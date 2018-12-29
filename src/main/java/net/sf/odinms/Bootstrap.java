package net.sf.odinms;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.constants.ServerConstants;
import net.sf.odinms.database.pool.InitHikariCP;
import net.sf.odinms.handling.MapleServerHandler;
import net.sf.odinms.handling.cashshop.CashShopServer;
import net.sf.odinms.handling.channel.ChannelServer;
import net.sf.odinms.handling.channel.MapleGuildRanking;
import net.sf.odinms.handling.login.LoginServer;
import net.sf.odinms.handling.world.World;
import net.sf.odinms.handling.world.family.MapleFamilyBuff;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.CashItemFactory;
import net.sf.odinms.server.ItemMakerFactory;
import net.sf.odinms.server.MapleCarnivalFactory;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.RandomRewards;
import net.sf.odinms.server.ServerProperties;
import net.sf.odinms.server.ShutdownServer;
import net.sf.odinms.server.SpeedRunner;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.server.timer.BuffTimer;
import net.sf.odinms.server.timer.CheatTimer;
import net.sf.odinms.server.timer.CloneTimer;
import net.sf.odinms.server.timer.EtcTimer;
import net.sf.odinms.server.timer.EventTimer;
import net.sf.odinms.server.timer.MapTimer;
import net.sf.odinms.server.timer.MobTimer;
import net.sf.odinms.server.timer.WorldTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Bootstrap {

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    //maven bootstrap
    public static void main(String[] args) {
        //init database collection pool
        //if collection pool is close means init fail
        if(!InitHikariCP.init()){
            return;
        }
        //auto open autoRegister
        ServerConstants.autoRegister = true;
        try {
            try (PreparedStatement ps = InitHikariCP.execute("UPDATE accounts SET loggedin = 0")) {//重置数据库登录状态
                ps.executeUpdate();
            }
            try (PreparedStatement ps = InitHikariCP.execute("UPDATE accounts SET lastGainHM = 0")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
           logger.error(e.getMessage(),e);
        }
        //init world server
        World.init();

        System.out.println("====================================================-[ 时钟线程 ]");
        long szxctime = System.currentTimeMillis();
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        System.out.println("时钟线程加载完成 耗时：" + (System.currentTimeMillis() - szxctime) / 1000.0 + "秒");

        System.out.println("====================================================-[ 加载NPC ]");
        long npctime = System.currentTimeMillis();
        MapleLifeFactory.loadQuestCounts();
        System.out.println("NPC数据加载完成 耗时：" + (System.currentTimeMillis() - npctime) / 1000.0 + "秒");


        System.out.println("====================================================-[ 加载任务 ]");
        long sjtime = System.currentTimeMillis();
        MapleQuest.initQuests();
        System.out.println("任务数据加载完成 耗时：" + (System.currentTimeMillis() - sjtime) / 1000.0 + "秒");


        System.out.println("====================================================-[ 加载道具数据 ]");
        long jzdjtime = System.currentTimeMillis();
        ItemMakerFactory.getInstance();//Loading ItemMakerFactory :::
        MapleItemInformationProvider.getInstance().runEtc();
        MapleItemInformationProvider.getInstance().runItems();
        System.out.println("加载道具数据完成 耗时：" + (System.currentTimeMillis() - jzdjtime) / 1000.0 + "秒");



        /**
         * check whether login name contains ForbiddenName
         */
        //LoginInformationProvider.getInstance();



        System.out.println("====================================================-[ 随机奖励 ]");
        long sjjltime = System.currentTimeMillis();
        RandomRewards.getInstance();
        System.out.println("随机奖励加载完成 耗时：" + (System.currentTimeMillis() - sjjltime) / 1000.0 + "秒");


        System.out.println("====================================================-[ 加载技能 ]");
        long jntime = System.currentTimeMillis();
        SkillFactory.getSkill(99999999);
        System.out.println("技能加载完成 耗时：" + (System.currentTimeMillis() - jntime) / 1000.0 + "秒");


        System.out.println("====================================================-[ 加载学院技能 ]");
        long xytime = System.currentTimeMillis();
        MapleFamilyBuff.getBuffEntry();
        System.out.println("加载学院技能完成 耗时：" + (System.currentTimeMillis() - xytime) / 1000.0 + "秒");


        System.out.println("====================================================-[ 加载怪物技能 ]");
        long gwjntime = System.currentTimeMillis();
        MapleCarnivalFactory.getInstance();
        System.out.println("加载怪物技能完成 耗时：" + (System.currentTimeMillis() - gwjntime) / 1000.0 + "秒");


        System.out.println("====================================================-[ 加载排名系统 ]");
        long jztime = System.currentTimeMillis();
        MapleGuildRanking.getInstance().RankingUpdate();
        System.out.println("加载排名系统完成 耗时：" + (System.currentTimeMillis() - jztime) / 1000.0 + "秒");
        MapleServerHandler.registerMBean();


        // RankingWorker.getInstance().run();
        // MTSStorage.load();
        System.out.println("====================================================-[ 加载商城道具 ]");
        long sctime = System.currentTimeMillis();
        CashItemFactory.getInstance().initialize();
        System.out.println("加载商城道具完成 耗时：" + (System.currentTimeMillis() - sctime) / 1000.0 + "秒");



        System.out.println("====================================================-[ 登录服务器 ]");
        LoginServer.run_startup_configurations();


        System.out.println("====================================================-[ 频道服务器 ]");
        ChannelServer.startChannel_Main();


        System.out.println("====================================================-[ 商城服务器 ]");
        CashShopServer.run_startup_configurations();


        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
        autoSave();
        if (Boolean.parseBoolean(ServerProperties.getProperty("RandDrop"))) {
            ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        try {
            SpeedRunner.getInstance().loadSpeedRuns();
        } catch (SQLException e) {
            System.out.println("SpeedRunner错误:" + e);
        }
        World.registerRespawn();
        LoginServer.setOn();
        System.out.println("\r\n经验倍率:" + Integer.parseInt(ServerProperties.getProperty("Exp")) + "  物品倍率：" + Integer.parseInt(ServerProperties.getProperty("Drop")) + "  金币倍率" + Integer.parseInt(ServerProperties.getProperty("Meso")));
        System.out.println("\r\n当前开放职业: "
                + " 冒险家 = " + Boolean.parseBoolean(ServerProperties.getProperty("冒险家"))
                + " 骑士团 = " + Boolean.parseBoolean(ServerProperties.getProperty("骑士团"))
                + " 战神 = " + Boolean.parseBoolean(ServerProperties.getProperty("战神")));
        System.out.println("\r\n服务端启动完毕 可以进入游戏了:::");
    }

    /**
     * add autoSave thread to time event
     */
    private static void autoSave() {
        logger.info("open autoSave....");
        WorldTimer.getInstance().register(()-> {
            int totalCharacter = 0;
            try {
                for (ChannelServer channelServer : ChannelServer.getAllInstances()) {
                    for (MapleCharacter chr : channelServer.getPlayerStorage().getAllCharacters()) {
                        if (chr == null) {
                            continue;
                        }
                        totalCharacter++;
                        chr.saveToDB(false, false);
                    }
                }
            } catch (Exception e) {
                logger.info(e.getMessage(),"get all player throws exception");
            }
            logger.info("save{}player",totalCharacter);
        }, 60 * 1000);
    }

    public static class Shutdown implements Runnable {
        @Override
        public void run() {
            new Thread(ShutdownServer.getInstance()).start();
        }
    }
}
