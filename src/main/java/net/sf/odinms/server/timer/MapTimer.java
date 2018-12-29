package net.sf.odinms.server.timer;

/**
 * @author EasyZhang
 * @date 2018/12/29 -  11:42
 */

public class MapTimer extends Timer {

    private static MapTimer instance = new MapTimer();

    private MapTimer() {
        name = "MapTimer";
    }

    public static MapTimer getInstance() {
        return instance;
    }
}
