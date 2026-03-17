package ru.armlix.winty.game;

public interface Tickable {

    default void tick() {

    }

    default void tick(int time) {
        tick();
    }

    default void tick(long time) {
        tick();
    }

    default void tick(long timestamp, int time) {
        tick();
    }
}
