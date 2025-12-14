package ru.doh1221.wintymc.server.network.netty.tcp.handler;

import ru.doh1221.wintymc.server.WintyMC;
import ru.doh1221.wintymc.server.entity.Player;
import ru.doh1221.wintymc.server.network.netty.tcp.ConnectionHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet1Login;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world.Packet11PlayerPosition;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world.Packet13PlayerPositionLook;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet4WorldTime;
import ru.doh1221.wintymc.server.utils.ChunkUtils;
import ru.doh1221.wintymc.server.utils.LongHash;

public class InGameHandler extends ConnectionHandler {

    public Player player;

    public InGameHandler(Player player) {
        this.player = player;
    }

    @Override
    public void handle(Packet1Login packet1Login) {
        if (packet1Login.protocolVersion > 14) {
            player.kick(WintyMC.getInstance().langMap.getTranslation("kick.server.outofdate"));
            return;
        } else if (packet1Login.protocolVersion < 14) {
            player.kick(WintyMC.getInstance().langMap.getTranslation("kick.server.outofdate"));
            return;
        }

        WintyMC.getInstance().getLogger().info(player.username + " [" + player.connection.getHandle().remoteAddress() + "] logged in");
        player.connection.write(new Packet1Login(player.entityID, "CoolServer", 1851285L, (byte) 0));
        // TODO Player Data Loading

        // TODO World Chunk loading
        WintyMC.getInstance().world.playerJoinedWorld(player);
        player.connection.write(new Packet4WorldTime(12000));
        player.connection.write(new Packet13PlayerPositionLook(WintyMC.getInstance().world.getChunkGenerator().getSpawnPosition()));
        player.connection.write(new Packet4WorldTime(12001));

        initChunks();
    }

    private void initChunks() {
        int cx = ChunkUtils.toChunk(player.position.getX());
        int cz = ChunkUtils.toChunk(player.position.getZ());

        int radius = player.chunkManager.getRadius();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int ccx = cx + dx;
                int ccz = cz + dz;

                player.chunkManager.requestChunkAsync(
                        WintyMC.getInstance().world,
                        ccx,
                        ccz
                );
            }
        }
    }

/*
    @Override
    public void handle(Packet11PlayerPosition pos) {
        double oldX = player.position.getX();
        double oldZ = player.position.getZ();

        player.position.set(pos.x, pos.y, pos.z);

        handleChunkMovement(oldX, oldZ);
    }

    @Override
    public void handle(Packet13PlayerPositionLook pos) {
        double oldX = player.position.getX();
        double oldZ = player.position.getZ();

        player.position.set(
                pos.x,
                pos.y,
                pos.z
        );

        handleChunkMovement(oldX, oldZ);
    }*/

    private void handleChunkMovement(double oldX, double oldZ) {
        int oldCX = ChunkUtils.toChunk(oldX);
        int oldCZ = ChunkUtils.toChunk(oldZ);

        int newCX = ChunkUtils.toChunk(player.position.getX());
        int newCZ = ChunkUtils.toChunk(player.position.getZ());

        if (oldCX == newCX && oldCZ == newCZ) {
            return;
        }

        int radius = player.chunkManager.getRadius();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int cx = newCX + dx;
                int cz = newCZ + dz;

                player.chunkManager.requestChunkAsync(
                        WintyMC.getInstance().world,
                        cx,
                        cz
                );
            }
        }

        player.chunkManager.getLoadedChunks().forEach(key -> {
            int cx = LongHash.lsw(key);
            int cz = LongHash.msw(key);

            if (Math.abs(cx - newCX) > radius ||
                    Math.abs(cz - newCZ) > radius) {

                player.chunkManager.unloadChunk(cx, cz);
            }
        });
    }


    @Override
    public String toString() {
        return "InGameHandler";
    }
}
