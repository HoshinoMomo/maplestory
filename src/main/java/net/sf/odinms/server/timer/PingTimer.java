package net.sf.odinms.server.timer;

/**
 * @author EasyZhang
 * @date 2018/12/29 -  11:45
 */

public class PingTimer extends Timer {

    private static PingTimer instance = new PingTimer();

    private PingTimer() {
        name = "PingTimer";
    }

    public static PingTimer getInstance() {
        return instance;
    }
}
