package ru.doh1221.wintymc.server.game.objects.items;

import ru.doh1221.wintymc.server.game.objects.MetaInfo;

public interface Item {
    public int getID();

    public String getName();

    public MetaInfo getMeta();
}
