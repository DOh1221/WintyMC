package ru.armlix.winty.game.entiy;

import ru.armlix.winty.game.items.ItemStack;

public class DroppedItemEntity extends Entity {

    ItemStack item;

    public DroppedItemEntity(ItemStack item, IDAllocator<Integer> alloc) {
        super(alloc);
    }

    @Override
    public void addObservable(Entity entity) {
    }

    @Override
    public void addObservable(long entityID) {
    }

    @Override
    public void removeObservable(Entity entity) {
        this.observableEntities.remove(entity);
    }

    @Override
    public void removeObservable(long entityID) {
    }

    @Override
    public void clearObservables() {
    }

}
