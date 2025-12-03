package ru.doh1221.wintymc.server.utils.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Loc3D {

    protected int x;
    protected int y;
    protected int z;

    public Loc3D() { }

    public Loc2D get2D() {
        return new Loc2D(x, z);
    }

}
