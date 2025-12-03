package ru.doh1221.wintymc.server.utils.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Loc2D {

    protected int x;
    protected int z;

    public Loc2D() { }

    public Loc3D get3D(int y) {
        return new Loc3D(x, y, z);
    }

    public Loc3D get3D() {
        return new Loc3D(x,0, z);
    }

}
