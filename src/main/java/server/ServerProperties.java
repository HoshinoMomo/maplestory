package server;

import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import database.DatabaseConnection;
import java.io.*;

/**
 *
 * @author Emilyx3
 */
public class ServerProperties {

    private static final Properties props = new Properties();
    private static final Properties skillProps = new Properties();
    private static String[] blockSkills;
    private static final String[] toLoad = {
        "服务端配置.ini"
    };

    private ServerProperties() {
    }

    static {
        for (String s : toLoad) {
            InputStreamReader fr;
            try {
                fr = new InputStreamReader(new FileInputStream(s), "UTF-8");//这里是用utf-8加载配置的，否则乱码
                props.load(fr);
                fr.close();
            } catch (IOException ex) {
                System.out.println("加载服务端配置出错，请检查：" + ex);
            }

        }
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM auth_server_channel_ip");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                props.put(rs.getString("name") + rs.getInt("channelid"), rs.getString("value"));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0); //Big ass error.
        }
    }

    public static void loadSkills() {
        try {
            FileReader sp = new FileReader("服务端配置.ini");
            skillProps.load(sp);
            sp.close();
        } catch (IOException ex) {
            System.out.println("加载 服务端配置.ini 配置出错2 " + ex);
        }
        blockSkills = null;
        blockSkills = skillProps.getProperty("BlockSkills").split(",");
    }

    public static String[] getBlockSkills() {
        return blockSkills;
    }

    public static boolean getBlockSkills(int id) {
        String[] Str = getBlockSkills();
        String skillId = id + "";
        for (int i = 0; i < Str.length; i++) {
            if (Str[i].equals(skillId)) {
                System.out.println("禁止技能:" + skillId);
                return true;
            }
        }
        return false;
    }

    public static String getProperty(String s) {
        return props.getProperty(s);
    }

    public static void setProperty(String prop, String newInf) {
        props.setProperty(prop, newInf);
    }

    public static String getProperty(String s, String def) {
        return props.getProperty(s, def);
    }
}
