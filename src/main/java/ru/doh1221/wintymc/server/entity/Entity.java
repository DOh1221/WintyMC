package ru.doh1221.wintymc.server.entity;

public class Entity {

    public static int entityCounter = 0;
    public int entityID = 0;

    public Entity() {
        this.entityID = entityCounter++;
    }

}
