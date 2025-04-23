package com.scouter.cobblemonoutbreaks.manager;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.scouter.cobblemonoutbreaks.portal.entity.OutbreakPortalEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OutbreakManager extends SavedData {

    private static final Logger LOGGER = LogUtils.getLogger();
    private Level level = null;
    private Map<UUID, OutbreakPortalEntity> outbreakPortalEntityMap = new ConcurrentHashMap<>();

    private static final Codec<Map<UUID, OutbreakPortalEntity>> MAPPER = Codec.unboundedMap(
            UUIDUtil.STRING_CODEC,
            OutbreakPortalEntity.CODEC
    );

    public static final SavedData.Factory<OutbreakManager> OUTBREAK_MANAGER_FACTORY = new SavedData.Factory<>(OutbreakManager::new, OutbreakManager::new);

    public static OutbreakManager get(Level level) {
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }

        ServerLevel serverLevel = level.getServer().overworld();
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = serverLevel.getDataStorage();
        // Get the PokemonOutbreakManager if it already exists. Otherwise, create a new one.
        return storage.computeIfAbsent(OUTBREAK_MANAGER_FACTORY, "outbreakmanager");
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void clearMap(Level level) {

        for (Map.Entry<UUID, OutbreakPortalEntity> entry : outbreakPortalEntityMap.entrySet()) {
            entry.getValue().kill(level);
        }

        outbreakPortalEntityMap.clear();
        setDirty();
    }

    public Map<UUID, OutbreakPortalEntity> getOutbreakPortalEntityMap() {
        return outbreakPortalEntityMap;
    }

    public boolean containsPortal(UUID pos) {
        return outbreakPortalEntityMap.containsKey(pos);
    }

    public OutbreakPortalEntity getOutbreakEntity(UUID pos) {
        return outbreakPortalEntityMap.get(pos);
    }

    public void addPortal(UUID uuid, OutbreakPortalEntity outbreakPortalEntity) {
        outbreakPortalEntityMap.put(uuid, outbreakPortalEntity);
        setDirty();
    }

    public void removePortal(UUID pokemonUUID) {
        outbreakPortalEntityMap.remove(pokemonUUID);
        setDirty();
    }

    public OutbreakManager() {
    }

    public OutbreakManager(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListTag outbreakList = compoundTag.getList("outbreakList", 10);
        for (int i = 0; i < outbreakList.size(); i++) {
            CompoundTag outbreakEntry = outbreakList.getCompound(i);
            UUID uuid = outbreakEntry.getUUID("uuid");
            CompoundTag outbreakData = outbreakEntry.getCompound("outbreak");

            DataResult<OutbreakPortalEntity> entityResult = OutbreakPortalEntity.CODEC.parse(NbtOps.INSTANCE, outbreakData);
            entityResult.ifSuccess(outbreak -> {
                outbreakPortalEntityMap.put(uuid, outbreak);
            }).ifError(error -> {
                LOGGER.error("Failed to load outbreak [{}] due to: {}", uuid, error.message());
            });
        }
        LOGGER.info("Finished loading {} outbreaks", outbreakPortalEntityMap.keySet().size());
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider pRegistries) {
        LOGGER.info("Saving outbreak portal map...");

        ListTag outbreakList = new ListTag();
        for (Map.Entry<UUID, OutbreakPortalEntity> entry : outbreakPortalEntityMap.entrySet()) {
            CompoundTag outbreakEntry = new CompoundTag();
            outbreakEntry.putUUID("uuid", entry.getKey());

            DataResult<Tag> encodedResult = OutbreakPortalEntity.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue());
            encodedResult.ifSuccess(res -> outbreakEntry.put( "outbreak",res))
                    .ifError(error -> LOGGER.error("Failed to save outbreak [{}] due to: {}", entry.getKey(), error.message()));

            outbreakList.add(outbreakEntry);
        }

        compoundTag.put("outbreakList", outbreakList);
        return compoundTag;
    }


}
