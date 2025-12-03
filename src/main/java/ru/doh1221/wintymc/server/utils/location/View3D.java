package ru.doh1221.wintymc.server.utils.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class View3D extends Loc3D {

    private float pitch;
    private float yaw;

    public     View3D(double x, double y, double z, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public View2D getView2D() {
        return new View2D(this.x, this.z, this.pitch, this.yaw);
    }

}
