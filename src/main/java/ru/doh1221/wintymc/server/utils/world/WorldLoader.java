package ru.doh1221.wintymc.server.utils.world;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public interface WorldLoader extends Closeable {

    File getDirectory();

    boolean worldExists(String worldName);

    LevelInfo loadWorldInfo(String worldName) throws IOException;

    LevelInfo createWorld(LevelInfo info) throws IOException;

    void saveWorldInfo(LevelInfo info) throws IOException;

    //ChunkStorage getChunkStorage(String worldName);

    void lock() throws IOException;

    void unlock();

    boolean isLocked();

    @Override
    void close() throws IOException;
}
