package net.sf.odinms.server.timer;

/**
 * @author EasyZhang
 * @date 2018/12/29 -  11:34
 */

public class WorldTimer extends Timer {

    private static WorldTimer instance = new WorldTimer();

    private WorldTimer() {
        name = "WorldTimer";
    }

    public static WorldTimer getInstance() {
        return instance;
    }
}
