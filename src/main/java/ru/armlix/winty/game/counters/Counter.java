package ru.armlix.winty.game.counters;

public interface Counter<T> {

    T increment();
    T decrement();

    T increment(T i);
    T decrement(T i);

    T getPrevious();
}
