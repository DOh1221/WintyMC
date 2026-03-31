package ru.armlix.winty.server;

public class Startup {

    public static void main(String[] args) {
        ServerDefinition serverDefinition = new ServerDefinition();
        serverDefinition.setServerName("Dummy Server");
        serverDefinition.setVersion("Beta 1.7.3");
        serverDefinition.setTPS(20);

        Server server = new MinecraftServer(serverDefinition);

        server.start();

    }

}
