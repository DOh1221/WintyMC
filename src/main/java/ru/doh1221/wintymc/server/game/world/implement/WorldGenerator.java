package ru.doh1221.wintymc.server.game.world.implement;

import ru.doh1221.wintymc.server.game.world.World;

import java.util.Random;

public abstract class WorldGenerator {

    public WorldGenerator() {}

    public abstract boolean a(World world, Random random, int i, int j, int k);

    public void a(double d0, double d1, double d2) {}
}
