package ru.doh1221.wintymc.server.game.items;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import lombok.SneakyThrows;

import java.io.IOException;

public class ItemRegistry {

    private static Int2ObjectArrayMap<Item> ItemRegistry = new Int2ObjectArrayMap<>();

    static { // TODO: Для блоков со сложной логикой создать свои классы
        
    }

    @SneakyThrows
    public static void registerItem(int ItemID, Item clazz) {
        if(!ItemRegistry.containsKey(ItemID)) {
            ItemRegistry.put(ItemID, clazz);
        } else {
            throw new IOException("Item ID already registered!");
        }
    }

    @SneakyThrows
    public static void unregisterItem(int ItemID) {
        ItemRegistry.remove(ItemID);
    }

    @SneakyThrows
    public static Item getItem(int ItemID) {
        return ItemRegistry.get(ItemID);
    }

/*    public static Item getByClassItem(Class<? extends Item> Item) {
        for(Item b : ItemRegistry.values()) {
            if(b.getClass().equals(Item)) {
                return b;
            }
        }
        return null;
    }*/

    public static Item getByItemName(String ItemName) {
        for(Item Item : ItemRegistry.values()) {
            if(Item.getName().equals(ItemName)) {
                return Item;
            }
        }
        return null;
    }

}
