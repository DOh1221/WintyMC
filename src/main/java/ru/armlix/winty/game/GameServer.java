package ru.armlix.winty.game;

import lombok.Getter;
import ru.armlix.winty.game.entiy.IDAllocator;
import ru.armlix.winty.game.entiy.SimpleAllocator;

public class GameServer {

    public static IDAllocator<Integer> alloc = new SimpleAllocator();

}
