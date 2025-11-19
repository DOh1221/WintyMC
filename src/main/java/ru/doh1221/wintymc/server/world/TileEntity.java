package ru.doh1221.wintymc.server.world;

public abstract class TileEntity {
    protected BlockPos pos;

    public void setPosition(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }
}
