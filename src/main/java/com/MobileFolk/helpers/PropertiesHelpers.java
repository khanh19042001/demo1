package com.MobileFolk.helpers;

import com.MobileFolk.utils.LanguageUtils;
import com.MobileFolk.utils.Log;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PropertiesHelpers {

    private static Properties properties;
    private static String linkFile;
    private static FileInputStream file;
    private static FileOutputStream out;
    private static String relPropertiesFilePathDefault = "src/test/resources/config/config.properties";
    private static String ENV_CONFIG_PATH = "src/test/resources/config/environment.json";
    private static ResourceBundle resourceConfig;
    private static String SOURCE_DIR = System.getProperty("user.dir") + File.separator;

    private PropertiesHelpers(String language) {
        Locale locale;
        if ("vi".equals(language)) {
            locale = new Locale("vi", "VI");
        } else if ("ja".equals(language)) {
            locale = Locale.JAPANESE;
        } else {
            locale = Locale.ENGLISH;
        }
        resourceConfig = ResourceBundle.getBundle("language", locale);
    }

    public static PropertiesHelpers getInstance(String language) {
        return new PropertiesHelpers(language);
    }

    public static String getLanguageValue(String key) {
        try {
            if (resourceConfig == null) getInstance(properties.getProperty("language"));
            return resourceConfig.getString(key);
        } catch (Exception e) {
            return null;
        }
    }

    //@Step("Loaded all properties files")
    public static synchronized Properties loadAllFiles() {
        LinkedList<String> files = new LinkedList<>();
        if (Objects.nonNull(properties)) return properties;

        files.add("src/test/resources/config/config.properties");
        files.add("src/test/resources/config/datatest.properties");

        try {
            properties = new Properties();

            for (String f : files) {
                Properties tempProp = new Properties();
                linkFile = SOURCE_DIR + f;
                file = new FileInputStream(linkFile);
                tempProp.load(file);
                properties.putAll(tempProp);
            }
            Log.info("Loaded all properties files.");
            Log.info("TestBase: Properties For Testing:\n" + properties);
            return properties;
        } catch (Exception e) {
            Log.info(e.getMessage());
            return new Properties();
        }
    }

    public static synchronized JSONObject loadAllEnvConfiguration() {
        String filePath = SOURCE_DIR + ENV_CONFIG_PATH;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                return new JSONObject(content);
            }
        } catch (IOException e) {
            Log.error("Read the environment config : Exception " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void setFile(String relPropertiesFilePath) {
        properties = new Properties();
        try {
            linkFile = SOURCE_DIR + relPropertiesFilePath;
            file = new FileInputStream(linkFile);
            properties.load(file);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDefaultFile() {
        properties = new Properties();
        try {
            linkFile = SOURCE_DIR + relPropertiesFilePathDefault;
            file = new FileInputStream(linkFile);
            properties.load(file);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getValue(String key) {
        String keyval = null;
        try {
            if (file == null) {
                properties = new Properties();
                linkFile =SOURCE_DIR + relPropertiesFilePathDefault;
                file = new FileInputStream(linkFile);
                properties.load(file);
                file.close();
            }
            // Lấy giá trị từ file đã Set
            keyval = properties.getProperty(key);
            return LanguageUtils.convertCharset_ISO_8859_1_To_UTF8(keyval);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return keyval;
    }

    public static void setValue(String key, String keyValue) {
        try {
            if (file == null) {
                properties = new Properties();
                file = new FileInputStream(SOURCE_DIR+ relPropertiesFilePathDefault);
                properties.load(file);
                file.close();
                out = new FileOutputStream(SOURCE_DIR+ relPropertiesFilePathDefault);
            }
            //Ghi vào cùng file Prop với file lấy ra
            out = new FileOutputStream(linkFile);
            System.out.println(linkFile);
            properties.setProperty(key, keyValue);
            properties.store(out, null);
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



}
