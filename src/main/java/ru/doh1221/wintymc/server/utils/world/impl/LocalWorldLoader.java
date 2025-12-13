package ru.doh1221.wintymc.server.utils.world.impl;

import ru.doh1221.wintymc.server.utils.world.LevelDatIO;
import ru.doh1221.wintymc.server.utils.world.LevelInfo;
import ru.doh1221.wintymc.server.utils.world.SessionLock;
import ru.doh1221.wintymc.server.utils.world.WorldLoader;

import java.io.File;
import java.io.IOException;

public class LocalWorldLoader implements WorldLoader {

    public File directory;
    public LevelInfo info;

    @Override
    public File getDirectory() {
        return this.directory;
    }

    @Override
    public boolean worldExists(String worldName) {
        return false;
    }

    @Override
    public LevelInfo loadWorldInfo(String worldName) throws IOException {
        return LevelDatIO.read(this.directory).get();
    }

    @Override
    public LevelInfo createWorld(LevelInfo info) throws IOException {
        return null;
    }

    @Override
    public void saveWorldInfo(LevelInfo info) throws IOException {

        LevelDatIO.writeAtomic(this.directory, info);
    }

    @Override
    public void lock() throws IOException {
        SessionLock.lock(this.directory);
    }

    @Override
    public void unlock() {
        SessionLock.unlock(this.directory);
    }

    @Override
    public boolean isLocked() {
        return SessionLock.check(this.directory) != -1;
    }

    @Override
    public void close() throws IOException {

    }
}
