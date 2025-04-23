package com.scouter.cobblemonoutbreaks.manager;

import com.google.common.collect.ConcurrentHashMultiset;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.scouter.cobblemonoutbreaks.codec.OutbreaksExtraCodec;
import com.scouter.cobblemonoutbreaks.config.OutbreakConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class OutbreakWorldManager extends SavedData {
    // Map to store the remaining time for each player

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Factory<OutbreakWorldManager> OUTBREAK_WORLD_MANAGER_FACTORY = new Factory<>(OutbreakWorldManager::new, OutbreakWorldManager::new);

    private ConcurrentHashMultiset<ChunkPos> chunkPosList = ConcurrentHashMultiset.create();

    private static final Codec<List<ChunkPos>> CHUNK_POS_CODEC =
            Codec.list(OutbreaksExtraCodec.CHUNK_POS_CODEC_STRING);


    private  int timeLeft;

    public static OutbreakWorldManager get(Level level){
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        ServerLevel serverLevel = level.getServer().overworld();
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = serverLevel.getDataStorage();
        // Get the OutbreakPlayerManager if it already exists. Otherwise, create a new one.
        return storage.computeIfAbsent(OUTBREAK_WORLD_MANAGER_FACTORY, "outbreakworldmanager");
    }


    public int decreaseAndGetTime() {
        --timeLeft;
        setDirty();
        return timeLeft;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void increaseTimeLeft() {
        timeLeft++;
        setDirty();
    }

    public void decreaseTimeLeft() {
        timeLeft--;
        setDirty();
    }

    public void setTimeLeft(int time) {
        timeLeft = time;
        setDirty();
    }

    public void clearTimeLeft(){
        timeLeft = 0;
        setDirty();
    }

    public void setTimeLeftToNewConfig(){
        int time = OutbreakConfigManager.getConfig().getSpawningConfig().getRandomOutbreakTimer();
        timeLeft = time;
        setDirty();
    }

    public boolean containsChunk(ChunkPos pos) {
        return chunkPosList.contains(pos);
    }

    public boolean containsChunk(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        boolean containsChunk = chunkPosList.contains(chunkPos);
        return containsChunk;
    }

    public void addChunkToList(ChunkPos pos) {
        chunkPosList.add(pos);
        setDirty();
    }

    public void removeChunkFromList(ChunkPos pos) {
        chunkPosList.remove(pos);
        setDirty();
    }

    public void clearChunkList() {
        chunkPosList.clear();
        setDirty();
    }

    public OutbreakWorldManager(){
    }


    public OutbreakWorldManager(CompoundTag compoundTag, HolderLookup.Provider provider) {
        timeLeft = compoundTag.getInt("world_timer");

        ListTag chunkListTag = compoundTag.getList("chunk_pos_list", 10); // 10 = CompoundTag
        for (int i = 0; i < chunkListTag.size(); i++) {
            CompoundTag chunkTag = chunkListTag.getCompound(i);
            int x = chunkTag.getInt("x");
            int z = chunkTag.getInt("z");
            chunkPosList.add(new ChunkPos(x, z));
        }

        LOGGER.info("Loaded {} chunk positions successfully.", chunkPosList.size());
    }



    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putInt("world_timer", timeLeft);

        ListTag chunkListTag = new ListTag();
        for (ChunkPos chunkPos : chunkPosList) {
            CompoundTag chunkTag = new CompoundTag();
            chunkTag.putInt("x", chunkPos.x);
            chunkTag.putInt("z", chunkPos.z);
            chunkListTag.add(chunkTag);
        }

        compoundTag.put("chunk_pos_list", chunkListTag);
        LOGGER.info("Saved {} chunk positions successfully.", chunkPosList.size());

        return compoundTag;
    }





}


