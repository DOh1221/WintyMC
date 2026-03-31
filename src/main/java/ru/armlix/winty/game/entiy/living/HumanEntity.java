package ru.armlix.winty.game.entiy.living;

import ru.armlix.winty.game.entiy.IDAllocator;

public class HumanEntity extends LivingEntity {

    protected String displayName = "Entity " + entityID;

    public HumanEntity(IDAllocator<Integer> alloc) {
        super(alloc);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
