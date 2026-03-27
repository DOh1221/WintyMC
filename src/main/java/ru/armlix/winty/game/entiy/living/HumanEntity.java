package ru.armlix.winty.game.entiy.living;

import ru.armlix.winty.game.entiy.IDAllocator;

public class HumanEntity extends LivingEntity {

    protected String displayname = "";

    public HumanEntity(IDAllocator<Integer> alloc) {
        super(alloc);
    }

}
