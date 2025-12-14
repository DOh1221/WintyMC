package ru.doh1221.wintymc.server.utils.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Loc3D {

    protected double x;
    protected double y;
    protected double z;

    public Loc3D() {
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Loc2D get2D() {
        return new Loc2D(x, z);
    }

}
