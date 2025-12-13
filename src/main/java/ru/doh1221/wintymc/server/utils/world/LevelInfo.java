package ru.doh1221.wintymc.server.utils.world;

import ru.doh1221.wintymc.server.utils.nbt.NBTTagCompound;

public record LevelInfo(
        long randomSeed,
        int spawnX,
        int spawnY,
        int spawnZ,
        float spawnYaw,
        float spawnPitch,
        long worldTime,
        long lastPlayed,
        long sizeOnDisk,
        String levelName,
        int version,
        int rainTime,
        boolean raining,
        int thunderTime,
        boolean thundering,
        NBTTagCompound playerData
        )
{
    public static NBTTagCompound toNBT(LevelInfo info) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setLong("RandomSeed", info.randomSeed());
        nbttagcompound.setInt("SpawnX", info.spawnX());
        nbttagcompound.setInt("SpawnY", info.spawnY());
        nbttagcompound.setInt("SpawnZ", info.spawnZ());
        nbttagcompound.setFloat("SpawnYaw", info.spawnYaw());
        nbttagcompound.setFloat("SpawnPitch", info.spawnPitch());
        nbttagcompound.setLong("Time", info.worldTime());
        nbttagcompound.setLong("SizeOnDisk", info.sizeOnDisk());
        nbttagcompound.setLong("LastPlayed", info.lastPlayed());
        nbttagcompound.setString("LevelName", info.levelName());
        nbttagcompound.setInt("version", info.version());
        nbttagcompound.setInt("rainTime", info.rainTime());
        nbttagcompound.setBoolean("raining", info.raining());
        nbttagcompound.setInt("thunderTime", info.thunderTime());
        nbttagcompound.setBoolean("thundering", info.thundering());
        if(info.playerData() != null && info.playerData().hasKey("Player")) {
            nbttagcompound.setCompound("Player", info.playerData);
        }
        return nbttagcompound;
    }

    public static LevelInfo fromNBT(NBTTagCompound tag) {
        return new LevelInfo(
                tag.getLong("RandomSeed"),
                tag.getInt("SpawnX"),
                tag.getInt("SpawnY"),
                tag.getInt("SpawnZ"),
                tag.getFloat("SpawnYaw"),
                tag.getFloat("SpawnPitch"),
                tag.getLong("Time"),
                tag.getLong("SizeOnDisk"),
                tag.getLong("LastPlayed"),
                tag.getString("LevelName"),
                tag.getInt("version"),
                tag.getInt("rainTime"),
                tag.getBoolean("raining"),
                tag.getInt("thunderTime"),
                tag.getBoolean("thundering"),
                tag.getCompound("Player")
                );
    }

}
