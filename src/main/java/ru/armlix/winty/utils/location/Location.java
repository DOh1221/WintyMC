package ru.armlix.winty.utils.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.armlix.winty.game.world.World;

@AllArgsConstructor
@Setter
@Getter
public class Location {

    protected World world;
    protected double x, y, z;
    protected float yaw, pitch;

    public void add(Location location) {
        this.add(location.x, location.y, location.z, location.yaw, pitch);
    }

    public void add(double x, double y, double z, float yaw, float pitch) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.yaw += yaw;
        this.pitch += pitch;
    }

    public void subtract(Location location) {
        this.subtract(location.x, location.y, location.z, location.yaw, location.pitch);
    }

    public void subtract(double x, double y, double z, float yaw, float pitch) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.yaw -= yaw;
        this.pitch -= pitch;
    }

    public int getBlockX() {
        return (int) Math.floor(x);
    }

    public int getBlockY() {
        return (int) Math.floor(y);
    }

    public int getBlockZ() {
        return (int) Math.floor(z);
    }

}
