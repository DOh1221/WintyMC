package ru.doh1221.wintymc.server.game.blocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import lombok.SneakyThrows;

import java.io.IOException;

public class BlockRegistry {

    private static Int2ObjectArrayMap<Class<? extends Block>> blockRegistry = new Int2ObjectArrayMap<>();

    static {
        registerBlock(1, BlockStone.class);
    }

    @SneakyThrows
    public static void registerBlock(int blockID, Class<? extends Block> clazz) {
        if(!blockRegistry.containsKey(blockID)) {
            blockRegistry.put(blockID, clazz);
        } else {
            throw new IOException("Block ID already registered!");
        }
    }

    @SneakyThrows
    public static void unregisterBlock(int blockID) {
        blockRegistry.remove(blockID);
    }

}
