package ru.doh1221.wintymc.server.utils.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class View2D extends Loc2D {

    private double pitch;
    private double yaw;

    public View2D(int x, int z, double pitch, double yaw) {
        this.x = x;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public View3D getView3D(int y) {
        return new View3D(this.x, y, this.z, this.pitch, this.yaw);
    }

    public View3D getView3D() {
        return new View3D(this.x, 0, this.z, this.pitch, this.yaw);
    }

}
