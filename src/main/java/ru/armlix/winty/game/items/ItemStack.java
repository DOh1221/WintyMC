package ru.armlix.winty.game.items;

public class ItemStack {

    int id;
    int count = 1;
    // metadata

    public ItemStack(Item item) {
        this.id = item.itemID;
    }

    public ItemStack(Item item, int count) {
        this.id = item.itemID;
        this.count = count;
    }

}
