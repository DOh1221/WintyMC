package ru.doh1221.wintymc.server.game.world.test;

import ru.doh1221.wintymc.server.utils.location.Loc3D;

public abstract class TileEntity {
    protected Loc3D pos;

    public void setPosition(Loc3D pos) {
        this.pos = pos;
    }

    public Loc3D getPos() {
        return pos;
    }
}
