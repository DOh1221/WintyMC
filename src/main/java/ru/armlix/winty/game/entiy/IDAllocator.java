package ru.armlix.winty.game.entiy;

public interface IDAllocator<T> {

    T allocate();
    void free(T id);

}
