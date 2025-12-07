package ru.doh1221.wintymc.server.game.blocks;

import java.util.Random;

public interface Block {
    public int getID();
    public String getName();
    public int idDropped(int i, Random random);
    public Block setHardness(float hardness);
    public float getHardness();
    public Block setResistance(float resistance);
    public float getResistance();
}
