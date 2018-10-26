/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.odinms.net.login;

import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MapleServerHandler;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.net.login.remote.LoginWorldInterface;
import net.sf.odinms.net.mina.MapleCodecFactory;
import net.sf.odinms.net.world.remote.WorldLoginInterface;
import net.sf.odinms.net.world.remote.WorldRegistry;
import net.sf.odinms.server.SpeedRankings;
import net.sf.odinms.server.TimerManager;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

public class LoginServer implements Runnable, LoginServerMBean {

    public static int PORT = 8484;
    private IoAcceptor acceptor;
    static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginServer.class);
    private static WorldRegistry worldRegistry = null;
    private Map<Integer, String> channelServer = new HashMap<Integer, String>();
    private LoginWorldInterface lwi;
    private WorldLoginInterface wli;
    private Properties prop = new Properties();
    private Properties initialProp = new Properties();
    private Boolean worldReady = Boolean.TRUE;
    private Properties subnetInfo = new Properties();
    private Map<Integer, Integer> load = new HashMap<Integer, Integer>();
    private String serverName;
    private String eventMessage;
    private int flag;
    private int maxCharacters;
    private Map<String, Integer> connectedIps = new HashMap<String, Integer>();
    int userLimit;
    int loginInterval;
    private long rankingInterval;
    private static LoginServer instance = new LoginServer();
    

    static {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            mBeanServer.registerMBean(instance, new ObjectName("net.sf.odinms.net.login:type=LoginServer,name=LoginServer"));
        } catch (Exception e) {
            log.error("MBEAN ERROR", e);
        }
    }

    private LoginServer() {
    }

    public static LoginServer getInstance() {
        return instance;
    }

    public Set<Integer> getChannels() {
        return channelServer.keySet();
    }

    public void addChannel(int channel, String ip) {
        channelServer.put(channel, ip);
        load.put(channel, 0);
    }

    public void removeChannel(int channel) {
        channelServer.remove(channel);
        load.remove(channel);
    }

    public String getIP(int channel) {
        return channelServer.get(channel);
    }

    public int getPossibleLogins() {
        int ret = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement limitCheck = con.prepareStatement("SELECT COUNT(*) FROM accounts WHERE loggedin > 1 AND gm = 0");
            ResultSet rs = limitCheck.executeQuery();
            if (rs.next()) {
                int usersOn = rs.getInt(1);
                if (usersOn < userLimit) {
                    ret = userLimit - usersOn;
                }
            }
            rs.close();
            limitCheck.close();
        } catch (Exception ex) {
            log.error("loginlimit error", ex);
        }
        return ret;
    }

    public void reconnectWorld() {
        try {
            wli.isAvailable(); // check if the connection is really gone
        } catch (RemoteException ex) {
            synchronized (worldReady) {
                worldReady = Boolean.FALSE;
            }
            synchronized (lwi) {
                synchronized (worldReady) {
                    if (worldReady) {
                        return;
                    }
                }
                log.warn("Reconnecting to world server");
                synchronized (wli) {
                    // completely re-establish the rmi connection
                    try {
                        FileReader fileReader = new FileReader(System.getProperty("net.sf.odinms.login.config"));
                        initialProp.load(fileReader);
                        fileReader.close();
                        Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"),
                                Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
                        worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
                        lwi = new LoginWorldInterfaceImpl();
                        wli = worldRegistry.registerLoginServer(initialProp.getProperty("net.sf.odinms.login.key"), lwi);
                        Properties dbProp = new Properties();
                        fileReader = new FileReader("db.properties");
                        dbProp.load(fileReader);
                        fileReader.close();
                        DatabaseConnection.setProps(dbProp);
                        DatabaseConnection.getConnection();
                        prop = wli.getWorldProperties();
                        userLimit = Integer.parseInt(prop.getProperty("net.sf.odinms.login.userlimit"));
                        serverName = prop.getProperty("net.sf.odinms.login.serverName");
                        eventMessage = prop.getProperty("net.sf.odinms.login.eventMessage");
                        flag = Integer.parseInt(prop.getProperty("net.sf.odinms.login.flag"));
                        maxCharacters = Integer.parseInt(prop.getProperty("net.sf.odinms.login.maxCharacters"));
                        try {
                            fileReader = new FileReader("subnet.properties");
                            subnetInfo.load(fileReader);
                            fileReader.close();
                        } catch (Exception e) {
                            log.info("Could not load subnet configuration, falling back to world defaults", e);
                        }
                    } catch (Exception e) {
                        log.error("Reconnecting failed", e);
                    }
                    worldReady = Boolean.TRUE;
                }
            }
            synchronized (worldReady) {
                worldReady.notifyAll();
            }
        }
    }

    @Override
    public void run() {
        try {
            FileReader fileReader = new FileReader(System.getProperty("net.sf.odinms.login.config"));
            initialProp.load(fileReader);
            fileReader.close();
            Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"),
                    Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
            worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
            lwi = new LoginWorldInterfaceImpl();
            wli = worldRegistry.registerLoginServer(initialProp.getProperty("net.sf.odinms.login.key"), lwi);
            Properties dbProp = new Properties();
            fileReader = new FileReader("db.properties");
            dbProp.load(fileReader);
            fileReader.close();
            DatabaseConnection.setProps(dbProp);
            DatabaseConnection.getConnection();
            prop = wli.getWorldProperties();
            userLimit = Integer.parseInt(prop.getProperty("net.sf.odinms.login.userlimit"));
            serverName = prop.getProperty("net.sf.odinms.login.serverName");
            eventMessage = prop.getProperty("net.sf.odinms.login.eventMessage");
            flag = Integer.parseInt(prop.getProperty("net.sf.odinms.login.flag"));
            maxCharacters = Integer.parseInt(prop.getProperty("net.sf.odinms.login.maxCharacters"));
            try {
                fileReader = new FileReader("subnet.properties");
                subnetInfo.load(fileReader);
                fileReader.close();
            } catch (Exception e) {
                log.trace("Could not load subnet configuration, falling back to world defaults", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not connect to world server.", e);
        }

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        acceptor = new SocketAcceptor();

        SocketAcceptorConfig cfg = new SocketAcceptorConfig();
        cfg.getSessionConfig().setTcpNoDelay(true);
        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));

        SpeedRankings.loadFromDB();

        TimerManager tMan = TimerManager.getInstance();
        tMan.start();
        loginInterval = Integer.parseInt(prop.getProperty("net.sf.odinms.login.interval"));
        //tMan.register(LoginWorker.getInstance(), loginInterval);
        rankingInterval = Long.parseLong(prop.getProperty("net.sf.odinms.login.ranking.interval"));
        PORT = Integer.parseInt(prop.getProperty("net.sf.odinms.login.port"));
        tMan.register(new RankingWorker(), rankingInterval);

        try {
            acceptor.bind(new InetSocketAddress(PORT), new MapleServerHandler(PacketProcessor.getProcessor(PacketProcessor.Mode.LOGINSERVER)), cfg);
            log.info("游戏端口： {}", PORT);
        } catch (IOException e) {
            log.error("Binding to port {} failed", PORT, e);
        }
    }

    public void shutdown() {
        log.info("Shutting down server...");
        try {
            worldRegistry.deregisterLoginServer(lwi);
        } catch (RemoteException e) {
        }
        TimerManager.getInstance().stop();
        System.exit(0);
    }

    public WorldLoginInterface getWorldInterface() {
        synchronized (worldReady) {
            while (!worldReady) {
                try {
                    worldReady.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return wli;
    }

    public static void main(String args[]) {
        try {
            LoginServer.getInstance().run();
        } catch (Exception ex) {
            log.error("Error initializing loginserver", ex);
        }
    }

    public int getLoginInterval() {
        return loginInterval;
    }

    public Properties getSubnetInfo() {
        return subnetInfo;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public String getServerName() {
        return serverName;
    }

    @Override
    public String getEventMessage() {
        return eventMessage;
    }

    @Override
    public int getFlag() {
        return flag;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public Map<Integer, Integer> getLoad() {
        return load;
    }

    public void setLoad(Map<Integer, Integer> load) {
        this.load = load;
    }

    public void addConnectedIP(String ip) {
        if (connectedIps.containsKey(ip)) {
            int connections = connectedIps.get(ip);
            connectedIps.remove(ip);
            connectedIps.put(ip, connections + 1);
        } else { // first connection from ip
            connectedIps.put(ip, 1);
        }
    }

    public void removeConnectedIp(String ip) {
        if (connectedIps.containsKey(ip)) {
            int connections = connectedIps.get(ip);
            connectedIps.remove(ip);
            if (connections - 1 != 0) {
                connectedIps.put(ip, connections - 1);
            }
        }
    }

    public boolean ipCanConnect(String ip) {
        if (connectedIps.containsKey(ip)) {
            if (connectedIps.get(ip) >= 5) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setEventMessage(String newMessage) {
        this.eventMessage = newMessage;
    }

    @Override
    public void setFlag(int newflag) {
        flag = newflag;
    }

    @Override
    public int getNumberOfSessions() {
        return acceptor.getManagedSessions(new InetSocketAddress(PORT)).size();
    }

    @Override
    public void setUserLimit(int newLimit) {
        userLimit = newLimit;
    }
}