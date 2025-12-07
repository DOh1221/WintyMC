package ru.doh1221.wintymc.server.game.blocks;

import java.util.Random;

public class BlockStone extends BlockBase{
    public BlockStone(int id, String name) {
        super(id, name);
    }

    @Override
    public int idDropped(int i, Random random) {
        return 10;
    }
}
