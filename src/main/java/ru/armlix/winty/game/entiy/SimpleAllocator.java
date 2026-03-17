package ru.armlix.winty.game.entiy;

public class SimpleAllocator implements IDAllocator<Integer> {

    int currentId = 0;

    @Override
    public Integer allocate() {
        return currentId++;
    }

    @Override
    public void free(Integer id) {

    }
}
