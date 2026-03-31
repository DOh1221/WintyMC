package ru.armlix.winty.game.entiy;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import ru.armlix.winty.game.Tickable;

import ru.armlix.winty.game.counters.TimeCounter;
import ru.armlix.winty.utils.location.Location;

import java.util.List;

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
    protected volatile long previousTickEntityTime;
    @Getter
    protected volatile long entityTime = 0;
    @Getter
    protected volatile long lastTick = 0;
    @Setter
    @Getter
    protected Location location;

    // GAME DATA
    @Getter
    protected final int entityID;
    @Getter
    protected List<Entity> observableEntities = new ObjectArrayList<>();

    public Entity(IDAllocator<Integer> alloc) {
        entityID = alloc.allocate();
    }

    @Override
    public void tick(long l) {
        lastTick = System.currentTimeMillis();
        worldTime = l;
        previousTickEntityTime = entityTime;
        if (timeCounter == null) {
            entityTime = l;
        } else {
            entityTime = timeCounter.getTime();
            timeCounter.increment(timeSpeedModifier);
        }
    }

    public void addObservable(Entity entity) {
        this.observableEntities.add(entity);
    }

    public void addObservable(long entityID) {
        // TODO
    }

    public void removeObservable(Entity entity) {
        this.observableEntities.remove(entity);
    }

    public void removeObservable(long entityID) {
        // TODO
    }

    public void clearObservables() {
        this.observableEntities.clear();
    }


}
