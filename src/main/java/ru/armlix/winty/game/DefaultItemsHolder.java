package ru.armlix.winty.game;

import ru.armlix.winty.game.chunking.block.AssociatableBlock;
import ru.armlix.winty.game.items.AssociatableItem;

public class DefaultItemsHolder {

    public static AssociatableBlock stone = new AssociatableBlock(1, 0, (byte) 1, "stone");
    public static AssociatableBlock grass = new AssociatableBlock(2, 0, (byte) 2, "grass");
    public static AssociatableBlock dirt = new AssociatableBlock(3, 0, (byte) 3, "dirt");

    public static AssociatableItem stone_item = new AssociatableItem(1, 0, (byte) 1, "stone");
    public static AssociatableItem grass_item = new AssociatableItem(2, 0, (byte) 2, "grass");
    public static AssociatableItem dirt_item = new AssociatableItem(3, 0, (byte) 3, "dirt");

    static {
        stone.setAssociatedItem(stone_item);
        stone_item.setAssociatedBlock(stone);

        grass.setAssociatedItem(grass_item);
        grass_item.setAssociatedBlock(grass);

        dirt.setAssociatedItem(dirt_item);
        dirt_item.setAssociatedBlock(dirt);
    }

}
