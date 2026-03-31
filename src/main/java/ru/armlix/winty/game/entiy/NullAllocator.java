package ru.armlix.winty.game.entiy;

public class NullAllocator implements IDAllocator<Integer> {
    @Override
    public Integer allocate() {
        return 0;
    }

    @Override
    public void free(Integer id) {

    }
}
