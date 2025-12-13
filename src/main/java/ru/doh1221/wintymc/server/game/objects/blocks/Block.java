package ru.doh1221.wintymc.server.game.objects.blocks;

import ru.doh1221.wintymc.server.game.objects.ItemData;

public interface Block {
    public int getID();

    public String getName();

    public ItemData[] dropInfo();

    public float getHardness();

    public Block setHardness(float hardness);

    public float getResistance();

    public Block setResistance(float resistance);
}
