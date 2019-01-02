package net.sf.odinms.server.timer;

/**
 * @author EasyZhang
 * @date 2018/12/29 -  11:44
 */

public class CloneTimer extends Timer {

    private static CloneTimer instance = new CloneTimer();

    private CloneTimer() {
        name = "CloneTimer";
    }

    public static CloneTimer getInstance() {
        return instance;
    }
}
