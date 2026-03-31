package ru.armlix.winty.game.items;

import lombok.Getter;
import lombok.Setter;
import ru.armlix.winty.game.chunking.block.Block;

@Setter
@Getter
public class AssociatableItem extends Item {

    public Block associatedBlock;

    public AssociatableItem(int itemID, int subID, byte networkID, String name) {
        super(itemID, subID, networkID, name);
    }

}
