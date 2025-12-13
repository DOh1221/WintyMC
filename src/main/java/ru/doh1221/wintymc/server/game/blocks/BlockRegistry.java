package ru.doh1221.wintymc.server.game.blocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import lombok.SneakyThrows;

import java.io.IOException;

public class BlockRegistry {

    private static Int2ObjectArrayMap<Block> blockRegistry = new Int2ObjectArrayMap<>();

    static { // TODO: Для блоков со сложной логикой создать свои классы
        registerBlock(1, new BlockBase(1, "stone").setHardness(1.5F).setResistance(10F));
        registerBlock(2, new BlockBase(2, "grass").setHardness(0.6F));
        registerBlock(3, new BlockBase(3, "dirt").setHardness(0.5F));
        registerBlock(4, new BlockBase(4, "stonebrick").setHardness(2.0F).setResistance(10F));
        registerBlock(5, new BlockBase(5, "wood").setHardness(2.0F).setResistance(5F));
        registerBlock(6, new BlockBase(6, "sapling").setHardness(0.0F));
        registerBlock(7, new BlockBase(7, "bedrock").setHardness(-1.0F).setResistance(6000000F));
        registerBlock(8, new BlockBase(8, "water").setHardness(100F)); // Still or moving?
        registerBlock(9, new BlockBase(9, "water").setHardness(100F)); // Still or moving?
        registerBlock(10, new BlockBase(10, "lava").setHardness(0.0F)); // Still or moving?
        registerBlock(11, new BlockBase(11, "lava").setHardness(100F)); // Still or moving?
        registerBlock(12, new BlockBase(12, "sand").setHardness(0.5F));
        registerBlock(13, new BlockBase(13, "gravel").setHardness(0.6F));
        registerBlock(14, new BlockBase(14, "oreGold").setHardness(3.0F).setResistance(5F));
        registerBlock(15, new BlockBase(15, "oreIron").setHardness(3.0F).setResistance(5F));
        registerBlock(16, new BlockBase(16, "oreCoal").setHardness(3.0F).setResistance(5F));
        registerBlock(17, new BlockBase(17, "log").setHardness(2.0F));
        registerBlock(18, new BlockBase(18, "leaves").setHardness(0.2F));
        registerBlock(19, new BlockBase(19, "sponge").setHardness(0.6F));
        registerBlock(20, new BlockBase(20, "glass").setHardness(0.3F));
        registerBlock(21, new BlockBase(21, "oreLapis").setHardness(3.0F).setResistance(5F));
        registerBlock(22, new BlockBase(22, "blockLapis").setHardness(3.0F).setResistance(5F));
        registerBlock(23, new BlockBase(23, "dispenser").setHardness(3.5F));
        registerBlock(24, new BlockBase(24, "sandStone").setHardness(0.8F));
        registerBlock(25, new BlockBase(25, "musicBlock").setHardness(0.8F));
        registerBlock(26, new BlockBase(26, "bed").setHardness(0.2F));
        registerBlock(27, new BlockBase(27, "goldenRail").setHardness(0.7F));
        registerBlock(28, new BlockBase(28, "detectorRail").setHardness(0.7F));
        registerBlock(29, new BlockBase(29, "pistonStickyBase"));
        registerBlock(30, new BlockBase(30, "web").setHardness(4.0F));
        registerBlock(31, new BlockBase(31, "tallgrass").setHardness(0.0F));
        registerBlock(32, new BlockBase(32, "deadbush").setHardness(0.0F));
        registerBlock(33, new BlockBase(33, "pistonBase"));
        registerBlock(34, new BlockBase(34, "pistonExtension"));
        registerBlock(35, new BlockBase(35, "cloth").setHardness(0.8F)); // предположительно, cloth был ID 35, но в оригинале не указан явно
        registerBlock(36, new BlockBase(36, "pistonMoving"));
        registerBlock(37, new BlockBase(37, "flower").setHardness(0.0F));
        registerBlock(38, new BlockBase(38, "rose").setHardness(0.0F));
        registerBlock(39, new BlockBase(39, "mushroom").setHardness(0.0F));
        registerBlock(40, new BlockBase(40, "mushroom").setHardness(0.0F));
        registerBlock(41, new BlockBase(41, "blockGold").setHardness(3.0F).setResistance(10F));
        registerBlock(42, new BlockBase(42, "blockIron").setHardness(5.0F).setResistance(10F));
        registerBlock(43, new BlockBase(43, "stoneSlab").setHardness(2.0F).setResistance(10F));
        registerBlock(44, new BlockBase(44, "stoneSlab").setHardness(2.0F).setResistance(10F));
        registerBlock(45, new BlockBase(45, "brick").setHardness(2.0F).setResistance(10F));
        registerBlock(46, new BlockBase(46, "tnt").setHardness(0.0F));
        registerBlock(47, new BlockBase(47, "bookshelf").setHardness(1.5F));
        registerBlock(48, new BlockBase(48, "stoneMoss").setHardness(2.0F).setResistance(10F));
        registerBlock(49, new BlockBase(49, "obsidian").setHardness(10.0F).setResistance(2000F));
        registerBlock(50, new BlockBase(50, "torch").setHardness(0.0F));
        registerBlock(51, new BlockBase(51, "fire").setHardness(0.0F));
        registerBlock(52, new BlockBase(52, "mobSpawner").setHardness(5.0F));
        registerBlock(53, new BlockBase(53, "stairsWood").setHardness(2.0F));
        registerBlock(54, new BlockBase(54, "chest").setHardness(2.5F));
        registerBlock(55, new BlockBase(55, "redstoneDust").setHardness(0.0F));
        registerBlock(56, new BlockBase(56, "oreDiamond").setHardness(3.0F).setResistance(5F));
        registerBlock(57, new BlockBase(57, "blockDiamond").setHardness(5.0F).setResistance(10F));
        registerBlock(58, new BlockBase(58, "workbench").setHardness(2.5F));
        registerBlock(59, new BlockBase(59, "crops").setHardness(0.0F));
        registerBlock(60, new BlockBase(60, "farmland").setHardness(0.6F));
        registerBlock(61, new BlockBase(61, "furnace").setHardness(3.5F));
        registerBlock(62, new BlockBase(62, "furnace").setHardness(3.5F));
        registerBlock(63, new BlockBase(63, "sign").setHardness(1.0F));
        registerBlock(64, new BlockBase(64, "doorWood").setHardness(3.0F));
        registerBlock(65, new BlockBase(65, "ladder").setHardness(0.4F));
        registerBlock(66, new BlockBase(66, "rail").setHardness(0.7F));
        registerBlock(67, new BlockBase(67, "stairsStone").setHardness(2.0F));
        registerBlock(68, new BlockBase(68, "sign").setHardness(1.0F));
        registerBlock(69, new BlockBase(69, "lever").setHardness(0.5F));
        registerBlock(70, new BlockBase(70, "pressurePlate").setHardness(0.5F));
        registerBlock(71, new BlockBase(71, "doorIron").setHardness(5.0F));
        registerBlock(72, new BlockBase(72, "pressurePlate").setHardness(0.5F));
        registerBlock(73, new BlockBase(73, "oreRedstone").setHardness(3.0F).setResistance(5F));
        registerBlock(74, new BlockBase(74, "oreRedstone").setHardness(3.0F).setResistance(5F));
        registerBlock(75, new BlockBase(75, "notGate").setHardness(0.0F));
        registerBlock(76, new BlockBase(76, "notGate").setHardness(0.0F));
        registerBlock(77, new BlockBase(77, "button").setHardness(0.5F));
        registerBlock(78, new BlockBase(78, "snow").setHardness(0.1F));
        registerBlock(79, new BlockBase(79, "ice").setHardness(0.5F));
        registerBlock(80, new BlockBase(80, "snow").setHardness(0.2F));
        registerBlock(81, new BlockBase(81, "cactus").setHardness(0.4F));
        registerBlock(82, new BlockBase(82, "clay").setHardness(0.6F));
        registerBlock(83, new BlockBase(83, "reeds").setHardness(0.0F));
        registerBlock(84, new BlockBase(84, "jukebox").setHardness(2.0F).setResistance(10F));
        registerBlock(85, new BlockBase(85, "fence").setHardness(2.0F).setResistance(5F));
        registerBlock(86, new BlockBase(86, "pumpkin").setHardness(1.0F));
        registerBlock(87, new BlockBase(87, "hellrock").setHardness(0.4F));
        registerBlock(88, new BlockBase(88, "hellsand").setHardness(0.5F));
        registerBlock(89, new BlockBase(89, "lightgem").setHardness(0.3F));
        registerBlock(90, new BlockBase(90, "portal").setHardness(-1.0F));
        registerBlock(91, new BlockBase(91, "litpumpkin").setHardness(1.0F));
        registerBlock(92, new BlockBase(92, "cake").setHardness(0.5F));
        registerBlock(93, new BlockBase(93, "diode").setHardness(0.0F));
        registerBlock(94, new BlockBase(94, "diode").setHardness(0.0F));
        registerBlock(95, new BlockBase(95, "lockedchest").setHardness(0.0F));
        registerBlock(96, new BlockBase(96, "trapdoor").setHardness(3.0F));
    }

    @SneakyThrows
    public static void registerBlock(int blockID, Block clazz) {
        if(!blockRegistry.containsKey(blockID)) {
            blockRegistry.put(blockID, clazz);
        } else {
            throw new IOException("Block ID already registered!");
        }
    }

    @SneakyThrows
    public static void unregisterBlock(int blockID) {
        blockRegistry.remove(blockID);
    }

    @SneakyThrows
    public static Block getBlock(int blockID) {
        return blockRegistry.get(blockID);
    }

/*    public static Block getByClassBlock(Class<? extends Block> block) {
        for(Block b : blockRegistry.values()) {
            if(b.getClass().equals(block)) {
                return b;
            }
        }
        return null;
    }*/

    public static Block getByBlockName(String blockName) {
        for(Block block : blockRegistry.values()) {
            if(block.getName().equals(blockName)) {
                return block;
            }
        }
        return null;
    }

}
