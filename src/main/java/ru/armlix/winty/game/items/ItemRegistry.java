package ru.armlix.winty.game.items;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class ItemRegistry {

    private static Int2ObjectOpenHashMap<Item> items =  new Int2ObjectOpenHashMap<>();

    public static void registerItem(Item item) {
        if(items.containsKey(item.getItemID())) {
            throw new RuntimeException("Item with ID " + item.getItemID() + " already exists");
        }
        items.put(item.getItemID(), item);
    }

    public static void unregisterBlock(int id) {
        items.remove(id);
    }

    static {
        registerItem(new Item(1, 0, (byte) 0,"stone"));
        registerItem(new Item(2, 0, (byte) 0,"grass"));
        registerItem(new Item(3, 0, (byte) 0,"dirt"));
    }

}
