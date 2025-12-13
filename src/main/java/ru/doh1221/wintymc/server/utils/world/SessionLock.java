package ru.doh1221.wintymc.server.utils.world;

import java.io.*;

public final class SessionLock {

    private static final String FILE = "session.lock";

    private SessionLock() {}

    public static void lock(File worldDir) {
        try {
            File lock = new File(worldDir, FILE);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(lock));
            out.writeLong(System.currentTimeMillis());
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create session.lock", e);
        }
    }

    public static void unlock(File worldDir) {
        File lock = new File(worldDir, FILE);
        if(lock.exists()) {
            lock.delete();
        }
    }

    public static long check(File worldDir) {
        try {
            File lock = new File(worldDir, FILE);
            if (!lock.exists()) return -1;

            DataInputStream in = new DataInputStream(new FileInputStream(lock));
            long timestamp = in.readLong();
            in.close();
            return timestamp;
        } catch (IOException e) {
            throw new RuntimeException("Can't check session.lock state", e);
        }
    }
}
