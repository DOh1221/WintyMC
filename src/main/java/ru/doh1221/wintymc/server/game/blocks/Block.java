package ru.doh1221.wintymc.server.game.blocks;

import ru.doh1221.wintymc.server.game.ItemData;

public interface Block {
    public int getID();
    public String getName();
    public ItemData[] dropInfo();
    public Block setHardness(float hardness);
    public float getHardness();
    public Block setResistance(float resistance);
    public float getResistance();
}
