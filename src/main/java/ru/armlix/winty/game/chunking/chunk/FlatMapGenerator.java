package ru.armlix.winty.game.chunking.chunk;

import ru.armlix.winty.game.world.WorldInfo;

public final class FlatMapGenerator implements IChunkGenerator {

    private static final int CX = 16;
    private static final int CZ = 16;
    private static final int CY = 128;
    private static final byte[] TEMPLATE = createTemplate();

    private static byte[] createTemplate() {
        byte[] blocks = new byte[CX * CY * CX];
        for (int x = 0; x < CX; x++) {
            for (int z = 0; z < CZ; z++) {
                blocks[Chunk.index(x, 1, z)] = 1;
            }
        }
        return blocks;
    }

    @Override
    public Chunk generateChunk(WorldInfo worldInfo, int x, int z) {
        return new Chunk(x, z, TEMPLATE.clone());
    }
}
