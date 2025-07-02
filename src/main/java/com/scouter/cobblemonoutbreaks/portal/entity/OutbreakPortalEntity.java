package com.scouter.cobblemonoutbreaks.portal.entity;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.HiddenAbilityProperty;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.algorithms.level.RandomAlgorithm;
import com.scouter.cobblemonoutbreaks.codec.NullableFieldCodec;
import com.scouter.cobblemonoutbreaks.config.OutbreakConfigManager;
import com.scouter.cobblemonoutbreaks.data.OutbreaksJsonDataManager;
import com.scouter.cobblemonoutbreaks.event.CobblemonOutbreaksEvent;
import com.scouter.cobblemonoutbreaks.manager.OutbreakManager;
import com.scouter.cobblemonoutbreaks.manager.PokemonOutbreakManager;
import com.scouter.cobblemonoutbreaks.portal.CustomDespawner;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortal;
import com.scouter.cobblemonoutbreaks.portal.PokemonRarity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

import static com.scouter.cobblemonoutbreaks.CobblemonOutbreaks.prefix;

public class OutbreakPortalEntity {


    private static final Logger LOGGER = LogUtils.getLogger();
    // Codec for persistent fields.
    public static final Codec<OutbreakPortalEntity> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NullableFieldCodec.makeDefaultableField("resource_location", ResourceLocation.CODEC, prefix("default")).forGetter(OutbreakPortalEntity::getResourceLocation),
                    OutbreakPortal.CODEC.fieldOf("portal").forGetter(OutbreakPortalEntity::getPortal),
                    OutbreakPortalEntityIdData.CODEC.fieldOf("id_data").forGetter(OutbreakPortalEntity::getEntityIdData),
                    BlockPos.CODEC.fieldOf("blockpos").forGetter(OutbreakPortalEntity::getBlockPosition),
                    OutbreakPortalEntityTickData.CODEC.fieldOf("tick_data").forGetter(OutbreakPortalEntity::getTickData),
                    OutbreakPortalEntityWaveData.CODEC.fieldOf("wave_data").forGetter(OutbreakPortalEntity::getWaveData),
                    Codec.BOOL.fieldOf("was_cleared").forGetter(OutbreakPortalEntity::isWasCleared)
            ).apply(instance, OutbreakPortalEntity::new)
    );

    // ----- Persistent fields (serialized) -----
    private final ResourceLocation resourceLocation;
    private final OutbreakPortal portal;
    private final OutbreakPortalEntityIdData entityIdData;
    private BlockPos blockPosition;
    private final OutbreakPortalEntityTickData tickData;
    private final OutbreakPortalEntityWaveData waveData;
    private boolean wasCleared;

    // ----- Runtime fields (transient, not serialized) -----
    private transient Level level;   // Level is set at runtime and not serialized.
    private transient boolean checkLevel = false;
    private PokemonOutbreakManager outbreakManager;
    private final CustomDespawner despawner;

    public OutbreakPortalEntity(Level level, Player player, ResourceLocation resourceLocation, BlockPos pos) {
        OutbreakPortal portal = OutbreaksJsonDataManager.getPortalFromResourceLocation(resourceLocation);
        OutbreakPortalEntityIdData idData = new OutbreakPortalEntityIdData(player.getUUID(), UUID.randomUUID());
        OutbreakPortalEntityTickData tickData = new OutbreakPortalEntityTickData(0,0);
        OutbreakPortalEntityWaveData waveData = new OutbreakPortalEntityWaveData(0, false);
        this.portal = portal;
        this.entityIdData = idData;
        this.tickData = tickData;
        this.waveData = waveData;
        this.blockPosition = pos;
        this.resourceLocation = resourceLocation;
        this.level = level;
        this.despawner = new CustomDespawner();

        addPortalToOutbreakManager(level);
        setPokemonOutbreakManager(level);
        sendMessageToPlayer(player);
    }

    public OutbreakPortalEntity(Level level, Player player, BlockPos pos) {


        Holder<Biome> biomeHolder = level.getBiome(pos);
        OutbreakPortal portal = OutbreaksJsonDataManager.getRandomPortalFromBiome((ServerLevel) level, biomeHolder);
        OutbreakPortalEntityIdData idData = new OutbreakPortalEntityIdData(player.getUUID(), UUID.randomUUID());
        OutbreakPortalEntityTickData tickData = new OutbreakPortalEntityTickData(0,0);
        OutbreakPortalEntityWaveData waveData = new OutbreakPortalEntityWaveData(0, false);
        this.portal = portal;
        this.entityIdData = idData;
        this.tickData = tickData;
        this.waveData = waveData;
        this.blockPosition = pos;
        this.resourceLocation = portal.getId();
        this.level = level;
        this.despawner = new CustomDespawner();

        checkValidY(player);
        addPortalToOutbreakManager(level);
        setPokemonOutbreakManager(level);
        sendMessageToPlayer(player);
        outbreakSpawnSound();

    }


    public OutbreakPortalEntity(
            ResourceLocation resourceLocation,
            OutbreakPortal portal,
            OutbreakPortalEntityIdData entityIdData,
            BlockPos blockPosition,
            OutbreakPortalEntityTickData tickData,
            OutbreakPortalEntityWaveData waveData,
            boolean wasCleared
    ) {
        this.resourceLocation = resourceLocation;
        this.portal = portal;
        this.entityIdData = entityIdData;
        this.blockPosition = blockPosition;
        this.tickData = tickData;
        this.waveData = waveData;
        this.despawner = new CustomDespawner();
        this.wasCleared = wasCleared;
    }


    public void addPortalToOutbreakManager(Level level) {
        OutbreakManager manager = OutbreakManager.get(level);
        manager.addPortal(getEntityIdData().getOutbreakUUID(), this);
    }

    public void setPokemonOutbreakManager(Level level) {
        PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(level);
        this.outbreakManager = outbreakManager;
    }

    public void checkValidY(Player player) {
        int maxY = getPortal().getOutbreakPortalSpawnSettings().getMaxOutbreakY();
        int minY = getPortal().getOutbreakPortalSpawnSettings().getMinOutbreakY();
        int y = getBlockPosition().getY();
        if (y < minY || y > maxY) {
            getPortal().getOutbreakMessageData().getUnluckySpawn().sendPlayerMessage(player);
            completeOutbreak(false);
        }
    }


    public void sendMessageToPlayer(Player player) {
        if (OutbreakConfigManager.getConfig().getMessages().isSendPortalSpawnMessage()) {
            if (OutbreakConfigManager.getConfig().getMessages().isBiomeSpecificSpawnsDebug()) {
                MutableComponent pokemonMessage = Component.literal(getPortal().getSpeciesData().getSpecies());
                MutableComponent biomeMessage = Component.literal(level.getBiome(this.blockPosition).unwrapKey().get().location().toString().split(":")[1]);
                MutableComponent blockPos = Component.literal(this.blockPosition.toString());

                getPortal().getOutbreakMessageData().getPortalBiomeSpecificSpawnDebug().sendPlayerMessage(player, biomeMessage, blockPos, pokemonMessage);

            } else if (!OutbreakConfigManager.getConfig().getMessages().isBiomeSpecificSpawnsDebug()) {
                MutableComponent pokemonMessage = Component.literal(getPortal().getSpeciesData().getSpecies());
                MutableComponent blockPos = Component.literal(String.valueOf(this.blockPosition));
                getPortal().getOutbreakMessageData().getPortalSpawnNearBlockPos().sendPlayerMessage(player, blockPos, pokemonMessage);
            } else {
                MutableComponent pokemonMessage = Component.literal(getPortal().getSpeciesData().getSpecies());
                getPortal().getOutbreakMessageData().getPortalSpawnNear().sendPlayerMessage(player, pokemonMessage);
            }

        }
    }


    public void outbreakSpawnSound() {
        if (OutbreakConfigManager.getConfig().getSound().isOutbreakPortalSpawnSound()) {
            if (level instanceof ServerLevel serverLevel) {
                getPortal().getOutbreakSounds().getPortalSpawnSound().playSound(serverLevel, getBlockPosition());
            }
        }
    }

    public void tick() {
        getTickData().increaseTick();
        if (isCheckLevel()) {
            pokemonStillValidFirstTime();
        }

        if (!level.isClientSide) {
            boolean containsPokemon = pokemonStillValid();


            if (getWaveData().getWave() >= getPortal().getSpeciesData().getWaveData().getWaves() && !containsPokemon) {
                if (!getWaveData().isHasSpawnedOne()) {
                    if (OutbreakConfigManager.getConfig().getMessages().isSendPortalSpawnMessage())
                        getPortal().getOutbreakMessageData().getGateFailedSpawning().sendPlayerMessage(level, getEntityIdData().getOwnerUUID(), getPortal().getSpeciesData().getSpecies());
                } else if (OutbreakConfigManager.getConfig().getMessages().isSendPortalSpawnMessage()) {
                    getPortal().getOutbreakMessageData().getGateFinished().sendPlayerMessage(level, getEntityIdData().getOwnerUUID(), getPortal().getSpeciesData().getSpecies());
                }

                completeOutbreak(true);
            }


            if (getTickData().getTickCount() % 100 == 0 && getWaveData().getWave() < getPortal().getSpeciesData().getWaveData().getWaves() && !containsPokemon) {
                spawnWave();
                if(!level.isClientSide) {
                    CobblemonOutbreaksEvent.WaveEnd waveEnd = new CobblemonOutbreaksEvent.WaveEnd((ServerLevel) level, this);
                    NeoForge.EVENT_BUS.post(waveEnd);
                }

                OutbreakManager outbreakManager1 = OutbreakManager.get(level);
                outbreakManager1.setDirty();

                getWaveData().increaseWave();

            }


            if (getTickData().getTicksActive() >= getPortal().getGateTimer() && getWaveData().isHasSpawnedOne()) {
                if (OutbreakConfigManager.getConfig().getMessages().isSendPortalSpawnMessage()) {
                    getPortal().getOutbreakMessageData().getGateTimeFinished().sendPlayerMessage(level, getEntityIdData().getOwnerUUID(), getPortal().getSpeciesData().getSpecies());
                }
                completeOutbreak(false);
            }
            getTickData().increaseTicksActive();
        }

        if (level instanceof ServerLevel serverLevel) {
            if (OutbreakConfigManager.getConfig().getGeneral().isOutbreakParticles()) {
                getPortal().getOutbreakAlgorithms().getParticleSpawningAlgorithm().spawnParticle(serverLevel, getBlockPosition(), getTickData().getTickCount());
            }
        }
    }

    /**
     * An extra check every ten minutes to check if the world still has the pokemon and then remove it from the map
     * Just an extra insurance to ensure that the portals wont be duds
     */
    public boolean pokemonStillValid() {
        if (level.isClientSide) return false;
        ServerLevel serverLevel = (ServerLevel) level;
        boolean containsPokemon = getEntityIdData().getCurrentOutbreakWaveEntities().stream().anyMatch(uuid -> getOutbreakManager().containsUUID(uuid));
        boolean worldHasPokemon = false;

        if (getTickData().getTickCount() % 12000 != 0 && containsPokemon) return containsPokemon;
        if (getTickData().getTickCount() % 150 == 0) {
            Set<UUID> toRemove = new HashSet<>();
            for (UUID uuid1 : getEntityIdData().getCurrentOutbreakWaveEntities()) {
                PokemonEntity entity = (PokemonEntity) serverLevel.getEntity(uuid1);
                if (entity == null) {
                    PokemonOutbreakManager pokemonOutbreakManager = PokemonOutbreakManager.get(serverLevel);
                    if (pokemonOutbreakManager.containsUUID(uuid1)) {
                        pokemonOutbreakManager.removePokemonUUID(uuid1);
                    }
                    toRemove.add(uuid1);
                } else {
                    worldHasPokemon = true;
                }
            }

            getEntityIdData().getCurrentOutbreakWaveEntities().removeAll(toRemove);

            return containsPokemon && worldHasPokemon;
        }
        return true;
    }

    public void pokemonStillValidFirstTime() {
        if (level.isClientSide || getTickData().getTickCount() % 150 != 0) return;
        ServerLevel serverLevel = (ServerLevel) level;
        Set<UUID> toRemove = new HashSet<>();
        for (UUID uuid1 : getEntityIdData().getCurrentOutbreakWaveEntities()) {
            PokemonEntity entity = (PokemonEntity) serverLevel.getEntity(uuid1);
            PokemonOutbreakManager pokemonOutbreakManager = PokemonOutbreakManager.get(serverLevel);
            if (entity == null) {
//
                if (pokemonOutbreakManager.containsUUID(uuid1)) {
                    pokemonOutbreakManager.removePokemonUUID(uuid1);
                }
                toRemove.add(uuid1);
            } else {
                pokemonOutbreakManager.removePokemonUUIDTemp(uuid1);
                pokemonOutbreakManager.addPokemonWOwner(uuid1, this.getEntityIdData().getOutbreakUUID());
            }
        }
//
        getEntityIdData().getCurrentOutbreakWaveEntities().removeAll(toRemove);
        checkLevel = true;
    }


    public void spawnWave() {
        Vec3 pos = new Vec3(getX(), getY(), getZ());
        if (level instanceof ServerLevel serverLevel) {

            List<PokemonEntity> spawned = spawnWave(serverLevel, pos, getPortal().getSpeciesData().getSpecies());
            for (PokemonEntity e : spawned) {
                if (e.getUUID().equals(null)) continue;
                getEntityIdData().getCurrentOutbreakWaveEntities().add(e.getPokemon().getUuid());
                getEntityIdData().getTotalOutbreakEntityIds().add(e.getUUID());
                getOutbreakManager().addPokemonWOwner(e.getPokemon().getUuid(), getEntityIdData().getOutbreakUUID());

            }

            if (!spawned.isEmpty()) {
                getWaveData().setHasSpawnedOne(true);
            }
        }
    }


    public List<PokemonEntity> spawnWave(ServerLevel serverLevel, Vec3 pos, String species) {
        List<PokemonEntity> spawned = new ArrayList<>();


        int spawnCount = getPortal().getSpeciesData().getWaveData().getSpawnsPerWave();

        for (int i = 0; i < spawnCount; i++) {
            PokemonProperties properties = PokemonProperties.Companion.parse("species=" + species, " ", "=");
            int spawnLevel = 0;
            Player player = level.getPlayerByUUID(getEntityIdData().getOwnerUUID());
            if (player != null) {
                spawnLevel = getPortal().getOutbreakAlgorithms().getLevelAlgorithm().getLevel(serverLevel, player);
            } else {
                spawnLevel = RandomAlgorithm.ALGO.getLevel(serverLevel, null);
            }

            properties.setLevel(spawnLevel);

            if (properties.getSpecies() == null) {
                if (getPortal().getSpeciesData().getSpecies().equals("default")) {
                    for (int j = 0; j < 4; j++) {
                        LOGGER.error("Species from {} is null, the species: {} is probably spelled incorrectly", getResourceLocation(), species);
                        LOGGER.error("This is a default setting, it means that you do not have any correct json files in your datapack and no outbreaks can spawn!");
                        LOGGER.error("If you think this is an error please report it to the developer");
                    }
                } else {
                    for (int j = 0; j < 4; j++) {
                        LOGGER.error("Species from {} is null, the species: {} is probably spelled incorrectly", getResourceLocation(), species);
                    }
                }

                completeOutbreak(false);
                return Collections.emptyList();
            }



            PokemonEntity pokemonEntity = properties.createEntity(level);


            pokemonEntity.setDespawner(getDespawner());

            Vec3 spawnPos = getPortal().getOutbreakAlgorithms().getSpawnAlgorithm().spawnPosition(serverLevel, pos, this, pokemonEntity);
            if (spawnPos == null) {
                //Decided not to add this since it will spawn the logs otherwise.
                //LOGGER.info("Spawning for Pokémon {} failed, due to spawnPos {} or Pokémon {}", pokemon1.getSpecies(), spawnPos, pokemon1.getSpecies());
                continue;
            }

            pokemonEntity.setPos(spawnPos);
            Pokemon pokemon1 = pokemonEntity.getPokemon();
            if (pokemon1 == null) {
                //Decided not to add this since it will spawn the logs otherwise.
                //LOGGER.info("Spawning for Pokémon {} failed, due to spawnPos {} or Pokémon {}", pokemon1.getSpecies(), spawnPos, pokemon1.getSpecies());
                continue;
            }

            double shinyChance = 1 / getPortal().getSpeciesData().getSpeciesShinyData().getShinyChance(pokemon1);
            if (level.random.nextDouble() < shinyChance) {
                pokemon1.setShiny(true);
                getPortal().getOutbreakSounds().getPokemonShinySound().playSound(serverLevel, getBlockPosition());

            }


            //HiddenAbilityProperty property = new HiddenAbilityProperty(true);
            //property.apply(pokemonEntity);
            level.addFreshEntity(pokemonEntity);
            //if (CobblemonOutbreaksConfig.OUTBREAK_PORTAL_SPAWN_SOUND.get()) {
            if (OutbreakConfigManager.getConfig().getSound().isOutbreakPortalSpawnSound()) {

                getPortal().getOutbreakSounds().getPortalSpawnSound().playSound(serverLevel, getBlockPosition());
            }
            //}

            spawned.add(pokemonEntity);
        }

        return spawned;
    }

    public CustomDespawner getDespawner() {
        return despawner == null ? new CustomDespawner() : despawner;
    }

    public void completeOutbreak(boolean rewards) {
        if (level instanceof ServerLevel serverLevel) {
            if (rewards) {
                if (OutbreakConfigManager.getConfig().getGeneral().isOutbreakSpawnRewards()) {
                    getPortal().getRewards().spawnExperienceRewards(serverLevel, getBlockPosition());
                    getPortal().getRewards().spawnItemRewards(serverLevel, getBlockPosition());
                }
            }

            setWasCleared(true);

            kill(level);

            for (UUID uuid : getEntityIdData().getCurrentOutbreakWaveEntities()) {
                getOutbreakManager().removePokemonUUID(uuid);
                getOutbreakManager().removePokemonUUIDTemp(uuid);
            }

            getEntityIdData().getCurrentOutbreakWaveEntities().clear();
            getEntityIdData().getTotalOutbreakEntityIds().clear();
            OutbreakManager outbreakManager1 = OutbreakManager.get(serverLevel);
            outbreakManager1.setDirty();
            outbreakManager1.removePortal(getEntityIdData().getOutbreakUUID());
            CobblemonOutbreaksEvent.PortalClose portalClose = new CobblemonOutbreaksEvent.PortalClose(serverLevel, this);
            NeoForge.EVENT_BUS.post(portalClose);
        }
    }


    public void kill(Level level) {

        for (UUID e : getEntityIdData().getTotalOutbreakEntityIds()) {
            if (level instanceof ServerLevel level1) {

                Entity entity = level1.getEntity(e);
                if(entity != null) {
                    entity.kill();
                }
                PokemonOutbreakManager.get(level).removePokemonUUID(e);
                PokemonOutbreakManager.get(level).removePokemonUUIDTemp(e);
            }
        }
    }


    // ----- Getters for persistent fields -----
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public OutbreakPortal getPortal() {
        return portal;
    }

    public OutbreakPortalEntityIdData getEntityIdData() {
        return entityIdData;
    }

    public BlockPos getBlockPosition() {
        return blockPosition;
    }

    public OutbreakPortalEntityTickData getTickData() {
        return tickData;
    }

    public OutbreakPortalEntityWaveData getWaveData() {
        return waveData;
    }


    // ----- Getters and setters for runtime fields -----
    @Nullable
    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public boolean isCheckLevel() {
        return checkLevel;
    }

    public void setCheckLevel(boolean checkLevel) {
        this.checkLevel = checkLevel;
    }

    // ----- Convenience Methods -----
    public double getX() {
        return blockPosition.getX();
    }

    public double getY() {
        return blockPosition.getY();
    }

    public double getZ() {
        return blockPosition.getZ();
    }

    public PokemonOutbreakManager getOutbreakManager() {
        if(outbreakManager == null) {
            setPokemonOutbreakManager(level);
        }
        return outbreakManager;
    }

    public void setOutbreakManager(PokemonOutbreakManager outbreakManager) {
        this.outbreakManager = outbreakManager;
    }

    public double distanceToSqr(double x, double y, double z) {
        double dx = getX() - x;
        double dy = getY() - y;
        double dz = getZ() - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public void setBlockPosition(Vec3 blockPosition) {
        BlockPos pos = BlockPos.containing(blockPosition.x(), blockPosition.y(), blockPosition.z());
        this.blockPosition = pos;
    }

    public void removeFromSet(UUID pokemon) {
        this.getEntityIdData().getCurrentOutbreakWaveEntities().remove(pokemon);
        //this.getEntityIdData().getTotalOutbreakEntityIds().remove(entityID);
    }

    public boolean isWasCleared() {
        return wasCleared;
    }

    public void setWasCleared(boolean wasCleared) {
        this.wasCleared = wasCleared;
    }

    @Override
    public String toString() {
        return "OutbreakPortalEntity{" +
                "resourceLocation=" + resourceLocation +
                ", portal=" + portal +
                ", idData=" + entityIdData +
                ", blockPosition=" + blockPosition +
                ", tickData=" + tickData +
                ", waveData=" + waveData +
                ", level=" + level +
                ", checkLevel=" + checkLevel +
                '}';
    }
}
