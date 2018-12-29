package net.sf.odinms.server.timer;

/**
 * @author EasyZhang
 * @date 2018/12/29 -  11:44
 */

public class EtcTimer extends Timer {

    private static EtcTimer instance = new EtcTimer();

    private EtcTimer() {
        name = "EtcTimer";
    }

    public static EtcTimer getInstance() {
        return instance;
    }
}
