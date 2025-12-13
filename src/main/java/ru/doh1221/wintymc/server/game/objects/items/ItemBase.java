package ru.doh1221.wintymc.server.game.objects.items;

import ru.doh1221.wintymc.server.game.objects.MetaInfo;

public class ItemBase implements Item {

    private final int id;
    private final String name;
    private MetaInfo metaInfo;

    public ItemBase(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ItemBase(int id, String name, MetaInfo metaInfo) {
        this(id, name);
        this.metaInfo = metaInfo;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public MetaInfo getMeta() {
        return this.metaInfo;
    }

}
