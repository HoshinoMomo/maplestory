package net.sf.odinms.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * for service properties
 * @author EasyZhang
 */
public final class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static final String PROPERTIES_FILE_NAME = "classpath:/service.yaml";
    private static Map<String,String> properties;

    public static void init(){
        Yaml yaml = new Yaml();
        try {
            properties = yaml.load(new FileInputStream(PROPERTIES_FILE_NAME));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
            System.exit(-1);
        }
    }
    /* try {
         PreparedStatement ps = InitHikariCP.execute("SELECT * FROM auth_server_channel_ip");
         ResultSet rs = ps.executeQuery();
         while (rs.next()) {
             props.put(rs.getString("name") + rs.getInt("channelid"), rs.getString("value"));
         }
         rs.close();
         ps.close();
     } catch (SQLException ex) {
         ex.printStackTrace();
         System.exit(0); //Big ass error.
     }*/
    public static void reload(){
        init();
    }

    public static boolean isSkillBan(int id) {
        String[] blockSkills = getProperty("banSkills").split(",");
        return Arrays.stream(blockSkills).anyMatch(blockSkill->blockSkill.equals(String.valueOf(id)));
    }

    /**
     * you should use getProperty with default value
     * if you are sure, it will not be null,you can use it.
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return properties.get(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if(Objects.isNull(properties)){
            return defaultValue;
        }
        if(Objects.isNull(properties.get(key))){
            return defaultValue;

        }
        return properties.get(key);
    }
}
