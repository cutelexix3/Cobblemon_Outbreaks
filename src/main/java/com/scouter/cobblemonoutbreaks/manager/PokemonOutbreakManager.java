package com.scouter.cobblemonoutbreaks.manager;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
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

public class PokemonOutbreakManager extends SavedData {

    private static final Logger LOGGER = LogUtils.getLogger();
    // Map to store the ownership information of Pokemon
    private final Map<UUID, UUID> pokemonOwnershipMap = new ConcurrentHashMap<>();

    private final Map<UUID, UUID> pokemonOwnerShipMapTemp = new ConcurrentHashMap<>();
    public static final Factory<PokemonOutbreakManager> POKEMON_OUTBREAK_MANAGER_FACTORY = new Factory<>(PokemonOutbreakManager::new, PokemonOutbreakManager::new, null);

    private static final Codec<Map<UUID, UUID>> MAPPER = Codec.unboundedMap(
            UUIDUtil.STRING_CODEC,
            UUIDUtil.STRING_CODEC
    );


    public static PokemonOutbreakManager get(Level level){
        if (level.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }

        ServerLevel serverLevel = level.getServer().overworld();
        // Get the vanilla storage manager from the level
        DimensionDataStorage storage = serverLevel.getDataStorage();
        // Get the PokemonOutbreakManager if it already exists. Otherwise, create a new one.
        return storage.computeIfAbsent(POKEMON_OUTBREAK_MANAGER_FACTORY, "pokemonoutbreakmanager");
    }

    public void clearMap(){
        pokemonOwnershipMap.clear();
        setDirty();
        return;
    }

    public boolean containsUUID(UUID pokemon){
        return pokemonOwnershipMap.containsKey(pokemon);
    }

    public UUID getOwnerUUID(UUID pokemonUUID){
        return pokemonOwnershipMap.get(pokemonUUID);
    }

    public void addPokemonWOwner(UUID pokemonUUID, UUID ownerUUID){
        pokemonOwnershipMap.put(pokemonUUID, ownerUUID);
        setDirty();
    }

    public void removePokemonUUID(UUID pokemonUUID){
        pokemonOwnershipMap.remove(pokemonUUID);
        setDirty();
    }

    public void clearTempMap(){
        pokemonOwnerShipMapTemp.clear();
        setDirty();
        return;
    }

    public boolean containsUUIDTemp(UUID pokemon){
        return pokemonOwnerShipMapTemp.containsKey(pokemon);
    }

    public UUID getOwnerUUIDTemp(UUID pokemonUUID){
        return pokemonOwnerShipMapTemp.get(pokemonUUID);
    }

    public void addPokemonWOwnerTemp(UUID pokemonUUID, UUID ownerUUID){
        pokemonOwnerShipMapTemp.put(pokemonUUID, ownerUUID);
        setDirty();
    }

    public void removePokemonUUIDTemp(UUID pokemonUUID){
        pokemonOwnerShipMapTemp.remove(pokemonUUID);
        setDirty();
    }

    public PokemonOutbreakManager(){
    }
    public PokemonOutbreakManager(CompoundTag compoundTag, HolderLookup.Provider provider) {
        CompoundTag tag = compoundTag.getCompound("pokemon_ownership_map");

        MAPPER.parse(NbtOps.INSTANCE, tag)
                .ifSuccess(pokemonOwnershipMap::putAll)
                .ifError(partial -> LOGGER.error("Failed to load pokemon ownership map due to {}", partial));


        CompoundTag tag1 = compoundTag.getCompound("pokemon_ownership_temp_map");

        MAPPER.parse(NbtOps.INSTANCE, tag1)
                .ifSuccess(pokemonOwnerShipMapTemp::putAll)
                .ifError(partial -> LOGGER.error("Failed to load pokemon temp ownership map due to {}", partial));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider pRegistries) {
        MAPPER.encodeStart(NbtOps.INSTANCE, pokemonOwnershipMap)
                .ifSuccess(e -> compoundTag.put("pokemon_ownership_map", e))
                .ifError(partial -> LOGGER.error("Failed to save pokemon ownership map due to {}", partial));

        MAPPER.encodeStart(NbtOps.INSTANCE, pokemonOwnerShipMapTemp)
                .ifSuccess(e -> compoundTag.put("pokemon_ownership_temp_map", e))
                .ifError(partial -> LOGGER.error("Failed to save pokemon temp ownership map due to {}", partial))
                .result().orElse(compoundTag);

        return  compoundTag;
    }




}
