package ru.doh1221.wintymc.server.utils.world.impl;

import ru.doh1221.wintymc.server.utils.world.LevelDatIO;
import ru.doh1221.wintymc.server.utils.world.LevelInfo;
import ru.doh1221.wintymc.server.utils.world.SessionLock;
import ru.doh1221.wintymc.server.utils.world.WorldLoader;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class LocalWorldLoader implements WorldLoader {

    public File directory;
    public LevelInfo info;
    public boolean exists;

    public LocalWorldLoader(File directory) {
        this.directory = directory;
    }

    @Override
    public File getDirectory() {
        return this.directory;
    }

    @Override
    public boolean worldExists() {
        return this.exists;
    }

    @Override
    public LevelInfo load() throws IOException {
        LevelInfo info = LevelDatIO.read(this.directory);
        exists = info != null;
        return info;
    }

    @Override
    public void save() throws IOException {
        if(!directory.exists()) {
            directory.mkdirs();
        }
        lock();
        LevelDatIO.writeAtomic(this.directory, info);
        unlock();
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
