package ru.armlix.winty.game.chunking.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import ru.armlix.winty.game.DefaultItemsHolder;

public class BlockRegistry {

    private static Int2ObjectOpenHashMap<Block> blocks =  new Int2ObjectOpenHashMap<>();

    public static void registerBlock(Block block) {
        if(blocks.containsKey(block.getBlockID())) {
            throw new RuntimeException("Block with ID " + block.getBlockID() + " already exists");
        }
        blocks.put(block.getBlockID(), block);
    }

    public static void unregisterBlock(int id) {
        blocks.remove(id);
    }

    static {
        registerBlock(DefaultItemsHolder.dirt);
        registerBlock(DefaultItemsHolder.grass);
        registerBlock(DefaultItemsHolder.stone);
    }

}
