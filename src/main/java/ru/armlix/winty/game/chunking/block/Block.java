package ru.armlix.winty.game.chunking.block;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Block {

    @Getter
    int blockID;
    @Getter
    int subID;
    @Getter
    byte networkID;
    @Getter
    String name;

    // metadata

}
