package net.sf.odinms.server;

/**
 *
 * @author RM
 */
public interface ShutdownServerMBean extends Runnable {

    public void shutdown();
}
