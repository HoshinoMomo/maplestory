package net.sf.odinms.server.timer;

/**
 * @author EasyZhang
 * @date 2018/12/29 -  11:46
 */

public class CheatTimer extends Timer{

    private static CheatTimer instance = new CheatTimer();

    private CheatTimer() {
        name = "CheatTimer";
    }

    public static CheatTimer getInstance() {
        return instance;
    }
}
