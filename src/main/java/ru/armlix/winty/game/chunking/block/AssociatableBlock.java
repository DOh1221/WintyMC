package ru.armlix.winty.game.chunking.block;

import lombok.Getter;
import lombok.Setter;
import ru.armlix.winty.game.items.Item;

@Setter
@Getter
public class AssociatableBlock extends Block {

    public Item associatedItem;

    public AssociatableBlock(int blockID, int subID, byte networkID, String name) {
        super(blockID, subID, networkID, name);
    }

}
