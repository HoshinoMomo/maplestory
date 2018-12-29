package net.sf.odinms.server.timer;

/**
 * @author EasyZhang
 * @date 2018/12/29 -  11:45
 */

public class MobTimer extends Timer {

    private static MobTimer instance = new MobTimer();

    private MobTimer() {
        name = "MobTimer";
    }

    public static MobTimer getInstance() {
        return instance;
    }
}
