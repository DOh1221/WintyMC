package ru.armlix.winty.game.chunking.block;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Block {

    @Getter
    final int blockID;
    @Getter
    final int subID;
    @Getter
    final byte networkID;
    @Getter
    final String name;

    // metadata

}
