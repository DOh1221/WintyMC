package ru.doh1221.wintymc.server.game.blocks;

import java.util.Objects;
import java.util.Random;

public class BlockGrass extends BlockBase {
    public BlockGrass(int id, String name) {
        super(id, name);
    }

    @Override
    public int idDropped(int i, Random random) {
        return Objects.requireNonNull(BlockRegistry.getByBlockName("dirt")).getID();
    }
}
