package com.scouter.cobblemonoutbreaks.manager;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.scouter.cobblemonoutbreaks.config.OutbreakConfigManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OutbreakPlayerManager extends SavedData {
    // Map to store the remaining time for each player

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Factory<OutbreakPlayerManager> OUTBREAK_PLAYER_MANAGER_FACTORY = new Factory<>(OutbreakPlayerManager::new, OutbreakPlayerManager::new, null);

    private final Map<UUID, Integer> timeLeftMap = new ConcurrentHashMap<>();
    private static final Codec<Map<UUID, Integer>> MAPPER = Codec.unboundedMap(
            UUIDUtil.STRING_CODEC,
            Codec.INT
    );
    public static OutbreakPlayerManager get(Level level){
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        ServerLevel serverLevel = level.getServer().overworld();
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = serverLevel.getDataStorage();
        // Get the OutbreakPlayerManager if it already exists. Otherwise, create a new one.
        return storage.computeIfAbsent(OUTBREAK_PLAYER_MANAGER_FACTORY, "outbreakplayermanager");
    }

    public boolean containsUUID(UUID player){
        return timeLeftMap.containsKey(player);
    }

    public int getTimeLeft(UUID player){
        return timeLeftMap.get(player);
    }

    public void setTimeLeft(UUID player, int time){
        timeLeftMap.put(player, time);
        setDirty();
    }

    public void clearTimeLeft(){
        for(Map.Entry<UUID,Integer> key : timeLeftMap.entrySet() ){
            timeLeftMap.put(key.getKey(), 0);
        }
        setDirty();
    }

    public void setTimeLeftToNewConfig(){
        for(Map.Entry<UUID,Integer> key : timeLeftMap.entrySet() ){
            int time = OutbreakConfigManager.getConfig().getSpawningConfig().getPerPlayerOutbreakTimer();
            if(key.getValue() > time) {
                timeLeftMap.put(key.getKey(), time);
            }
        }
        setDirty();
    }


    public OutbreakPlayerManager(){
    }


    public OutbreakPlayerManager(CompoundTag compoundTag, HolderLookup.Provider provider) {
        CompoundTag tag = compoundTag.getCompound("outbreak_player_map");
        MAPPER.parse(NbtOps.INSTANCE, tag)
                .ifSuccess(timeLeftMap::putAll)
                .ifError(partial -> LOGGER.error("Failed to load outbreak portal map due to {}", partial));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider pRegistries) {

        MAPPER.encodeStart(NbtOps.INSTANCE, timeLeftMap)
                .ifSuccess(e -> compoundTag.put("outbreak_player_map", e))
                .ifError(partial -> LOGGER.error("Failed to save outbreak portal map due to {}", partial))
                .result().orElse(compoundTag);
        return compoundTag;
    }



}


