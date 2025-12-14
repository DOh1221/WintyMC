package ru.doh1221.wintymc.server.utils.world;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public interface WorldLoader extends Closeable {

    File getDirectory();

    boolean worldExists();

    LevelInfo load() throws IOException;

    void save() throws IOException;

    void lock() throws IOException;

    void unlock();

    boolean isLocked();

    @Override
    void close() throws IOException;
}
