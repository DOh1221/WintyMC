package ru.doh1221.wintymc.server.game.items;

import ru.doh1221.wintymc.server.game.MetaInfo;

public interface Item {
    public int getID();
    public String getName();
    public MetaInfo getMeta();
}
