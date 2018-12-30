package net.sf.odinms.server.timer;

/**
 * @author EasyZhang
 * @date 2018/12/29 -  11:42
 */

public class BuffTimer extends Timer {

    private static BuffTimer instance = new BuffTimer();

    private BuffTimer() {
        name = "BuffTimer";
    }

    public static BuffTimer getInstance() {
        return instance;
    }
}
