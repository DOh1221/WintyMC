package ru.doh1221.wintymc.server.network.netty.tcp.handler;

import ru.doh1221.wintymc.server.WintyMC;
import ru.doh1221.wintymc.server.entity.Player;
import ru.doh1221.wintymc.server.network.netty.tcp.ChannelWrapper;
import ru.doh1221.wintymc.server.network.netty.tcp.ConnectionHandler;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.auth.Packet1Login;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world.Packet11PlayerPosition;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.player.world.Packet13PlayerPositionLook;
import ru.doh1221.wintymc.server.network.netty.tcp.packet.game.world.Packet4WorldTime;
import ru.doh1221.wintymc.server.utils.ChunkUtils;
import ru.doh1221.wintymc.server.utils.LongHash;

import java.util.ArrayList;
import java.util.List;

public class InGameHandler extends ConnectionHandler {

    public Player player;

    public InGameHandler(Player player) {
        this.player = player;
    }

    @Override
    public void disconnected(ChannelWrapper channelWrapper) {
        player.getChunkManager().clearAll();
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

        int radius = player.getChunkManager().getRadius();

        List<int[]> spiral = player.getChunkManager().spiralChunks(cx, cz, radius);
        for (int[] p : spiral) {
            player.getChunkManager().requestChunkSync(
                    WintyMC.getInstance().world,
                    p[0],
                    p[1]
            );
        }
    }

    @Override
    public void handle(Packet11PlayerPosition pos) {
        player.position.set(pos.x, pos.y, pos.z);
        handleChunkMovement();
    }

    @Override
    public void handle(Packet13PlayerPositionLook pos) {
        player.position.set(pos.x, pos.y, pos.z);
        handleChunkMovement();
    }

    private void handleChunkMovement() {
        double dx = player.position.getX() - player.getChunkManager().getLastChunkUpdateX();
        double dz = player.position.getZ() - player.getChunkManager().getLastChunkUpdateZ();

        if (dx * dx + dz * dz < 64.0) {
            return;
        }

        player.getChunkManager().setLastChunkUpdateX(player.position.getX());
        player.getChunkManager().setLastChunkUpdateZ(player.position.getZ());

        int newCX = ChunkUtils.toChunk(player.position.getX());
        int newCZ = ChunkUtils.toChunk(player.position.getZ());
        int radius = player.getChunkManager().getRadius();

        List<int[]> spiral = player.getChunkManager().spiralChunks(newCX, newCZ, radius);
        for (int[] p : spiral) {
            player.getChunkManager().requestChunkSync(
                    WintyMC.getInstance().world,
                    p[0],
                    p[1]
            );
        }

        List<Long> loaded = new ArrayList<>(player.getChunkManager().getLoadedChunks());
        for (long key : loaded) {
            int cx = LongHash.lsw(key);
            int cz = LongHash.msw(key);

            if (Math.abs(cx - newCX) > radius ||
                    Math.abs(cz - newCZ) > radius) {
                player.getChunkManager().unloadChunk(cx, cz);
            }
        }
    }

    @Override
    public String toString() {
        return "InGameHandler";
    }
}
