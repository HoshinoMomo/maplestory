package net.sf.odinms.server.timer;

/**
 * @author EasyZhang
 * @date 2018/12/29 -  11:43
 */

public class EventTimer extends Timer{

    private static EventTimer instance = new EventTimer();

    private EventTimer() {
        name = "EventTimer";
    }

    public static EventTimer getInstance() {
        return instance;
    }
}
