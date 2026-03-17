package ru.armlix.winty.game.entiy;

import lombok.Getter;
import lombok.Setter;
import ru.armlix.winty.game.Tickable;

import ru.armlix.winty.game.counters.TimeCounter;

public class Entity implements Tickable {

    // TIME MANAGEMENT
    @Getter
    @Setter
    protected volatile TimeCounter timeCounter;
    @Getter
    @Setter
    protected volatile long timeSpeedModifier = 1L;
    @Getter
    protected volatile long worldTime = 0;
    @Getter
    protected volatile long time = 0;
    @Getter
    protected volatile long lastTick = 0;

    // GAME DATA
    @Getter
    protected final int entityID;

    public Entity(IDAllocator<Integer> alloc) {
        entityID = alloc.allocate();
    }

    @Override
    public void tick(long l) {
        lastTick = System.currentTimeMillis();
        worldTime = l;
        if (timeCounter == null) {
            time = l;
        } else {
            time = timeCounter.getTime();
            timeCounter.increment(timeSpeedModifier);
        }
    }

}
