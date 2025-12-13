package ru.doh1221.wintymc.server.utils.world;

import ru.doh1221.wintymc.server.utils.nbt.CompressedStreamTools;
import ru.doh1221.wintymc.server.utils.nbt.NBTTagCompound;

import java.io.*;
import java.util.Optional;

public final class LevelDatIO {

    private LevelDatIO() {}

    public static Optional<LevelInfo> read(File worldDir) {
        File levelDat = new File(worldDir, "level.dat");
        File levelDatOld = new File(worldDir, "level.dat_old");

        try {
            File target = levelDat.exists() ? levelDat : levelDatOld;
            if (!target.exists()) return Optional.empty();

            NBTTagCompound root = CompressedStreamTools.read(new FileInputStream(target));
            NBTTagCompound data = root.getCompound("Data");

            return Optional.of(LevelInfo.fromNBT(data));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void writeAtomic(File worldDir, LevelInfo meta) {
        try {
            File newFile = new File(worldDir, "level.dat_new");
            File oldFile = new File(worldDir, "level.dat_old");
            File file = new File(worldDir, "level.dat");

            NBTTagCompound root = new NBTTagCompound();
            root.setCompound("Data", LevelInfo.toNBT(meta));

            CompressedStreamTools.write(root, new FileOutputStream(newFile));

            if (oldFile.exists()) oldFile.delete();
            if (file.exists()) file.renameTo(oldFile);
            newFile.renameTo(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
