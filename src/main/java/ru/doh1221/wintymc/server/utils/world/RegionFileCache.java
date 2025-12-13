package ru.doh1221.wintymc.server.utils.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegionFileCache {

    private static final Map<File, SoftReference<RegionFile>> CACHE = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 64 * 64;

    private RegionFileCache() {}

    public static synchronized RegionFile getRegionFile(File worldDir, int chunkX, int chunkZ) {
        File regionDir = new File(worldDir, "region");
        File regionFile = new File(regionDir, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mcr");

        SoftReference<RegionFile> ref = CACHE.get(regionFile);
        if (ref != null) {
            RegionFile cached = ref.get();
            if (cached != null) {
                return cached;
            }
        }

        if (!regionDir.exists() && !regionDir.mkdirs()) {
            System.err.println("Не удалось создать папку region: " + regionDir.getAbsolutePath());
        }

        if (CACHE.size() >= MAX_CACHE_SIZE) {
            clearCache();
        }

        RegionFile newRegionFile = new RegionFile(regionFile);
        CACHE.put(regionFile, new SoftReference<>(newRegionFile));
        return newRegionFile;
    }

    public static synchronized void clearCache() {
        Iterator<SoftReference<RegionFile>> iterator = CACHE.values().iterator();
        while (iterator.hasNext()) {
            SoftReference<RegionFile> ref = iterator.next();
            RegionFile regionFile = ref.get();
            if (regionFile != null) {
                try {
                    regionFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        CACHE.clear();
    }

    public static DataInputStream getChunkInputStream(File worldDir, int chunkX, int chunkZ) {
        RegionFile regionFile = getRegionFile(worldDir, chunkX, chunkZ);
        return regionFile.getChunkDataInputStream(chunkX & 31, chunkZ & 31);
    }

    public static DataOutputStream getChunkOutputStream(File worldDir, int chunkX, int chunkZ) {
        RegionFile regionFile = getRegionFile(worldDir, chunkX, chunkZ);
        return regionFile.getChunkDataOutputStream(chunkX & 31, chunkZ & 31);
    }
}
