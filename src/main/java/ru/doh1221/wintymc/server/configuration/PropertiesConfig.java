package ru.doh1221.wintymc.server.configuration;

import java.io.*;
import java.util.Properties;

public class PropertiesConfig {

    private final File File;
    private final String store;
    private Properties properties;

    public PropertiesConfig(String folder, String path, String store) {
        this.File = new File(path);
        File Folder = new File(folder);

        try {
            if (!Folder.exists()) {
                Folder.mkdir();
            }
            if (!this.File.exists()) {
                this.File.createNewFile();
            }


            FileReader reader = new FileReader(path);
            this.properties = new Properties();

            properties.load(reader);

            properties.store(new FileWriter(path), store);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.store = store;

    }

    public void addDefault(String string, String value) {
        properties.setProperty(string, properties.getProperty(string, value));
    }

    public void setProperty(String string, String value) {
        properties.setProperty(string, value);
    }

    public String getString(String string) {
        return properties.getProperty(string);
    }

    public boolean getBoolean(String string) {
        return Boolean.parseBoolean(properties.getProperty(string));
    }

    public int getInt(String string) {
        return Integer.parseInt(properties.getProperty(string));
    }

    public byte getByte(String string) {
        return Byte.parseByte(properties.getProperty(string));
    }

    public Long getLong(String string) {
        return Long.parseLong(properties.getProperty(string));
    }

    public void saveProperties() {
        try {
            FileOutputStream fr = new FileOutputStream(this.File);
            properties.store(fr, store);
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadProperties() {
        try {
            FileInputStream fi = new FileInputStream(this.File);
            properties.load(fi);
            fi.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

