package ru.armlix.winty.game.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.armlix.winty.game.chunking.block.Block;

@AllArgsConstructor
public class Item {

    @Getter
    final int itemID;
    @Getter
    final int subID;
    @Getter
    final byte networkID;
    @Getter
    final String name;

    // metadata

}
