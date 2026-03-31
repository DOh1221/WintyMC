package ru.armlix.winty.server;

import lombok.Getter;
import lombok.Setter;

public class ServerDefinition {

    @Getter
    @Setter
    private String serverName;
    @Getter
    @Setter
    private String version;

    @Getter
    private int TPS;
    @Getter
    private long tickDuration;

    public void setTPS(int TPS) {
        this.TPS = TPS;
        tickDuration = 100 / TPS;
    }

}
