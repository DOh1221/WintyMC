package ru.doh1221.wintymc.server.game.objects.blocks;

import ru.doh1221.wintymc.server.game.objects.ItemData;

public class BlockBase implements Block {

    private final int id;
    private final String name;
    protected float blockHardness;
    protected float blockResistance;

    public BlockBase(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemData[] dropInfo() {
        return new ItemData[]{new ItemData(this.id, true, null)};
    }

    @Override
    public Block setHardness(float hardness) {
        this.blockHardness = hardness;
        return this;
    }

    @Override
    public float getHardness() {
        return blockHardness;
    }

    @Override
    public Block setResistance(float resistance) {
        this.blockResistance = resistance;
        return this;
    }

    @Override
    public float getResistance() {
        return this.blockResistance;
    }
}
