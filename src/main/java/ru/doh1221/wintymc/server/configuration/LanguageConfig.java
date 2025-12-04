package ru.doh1221.wintymc.server.configuration;

public class LanguageConfig implements LanguageMapping {

    private final PropertiesConfig configFile;

    public LanguageConfig(String folder, String file, String store) {
        this.configFile = new PropertiesConfig(folder, file, store);
        defaults();
    }

    public void defaults() {
        configFile.addDefault("kick.message", "Server still starting! Join in few minutes");
        configFile.addDefault("kick.server.outofdate", "Server is out of date!");
        configFile.addDefault("kick.client.outofdate", "Client is out of date!");
        configFile.saveProperties();
        configFile.loadProperties();
    }

    @Override
    public String getTranslation(String name) {
        return configFile.getString(name);
    }

}
