package ru.armlix.winty.game.chunking.chunk;

import ru.armlix.winty.game.Tickable;
import ru.armlix.winty.game.world.WorldInfo;

import java.util.concurrent.CompletableFuture;

public interface IChunkProvider extends Tickable {

    CompletableFuture<Chunk> getChunkAt(WorldInfo info, int cx, int cz);

}
