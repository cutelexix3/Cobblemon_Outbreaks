package com.scouter.cobblemonoutbreaks.event;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.logging.LogUtils;
import com.scouter.cobblemonoutbreaks.CobblemonOutbreaks;
import com.scouter.cobblemonoutbreaks.config.OutbreakConfig;
import com.scouter.cobblemonoutbreaks.config.OutbreakConfigManager;
import com.scouter.cobblemonoutbreaks.data.OutbreaksJsonDataManager;
import com.scouter.cobblemonoutbreaks.manager.OutbreakManager;
import com.scouter.cobblemonoutbreaks.manager.OutbreakPlayerManager;
import com.scouter.cobblemonoutbreaks.manager.OutbreakWorldManager;
import com.scouter.cobblemonoutbreaks.manager.PokemonOutbreakManager;
import com.scouter.cobblemonoutbreaks.portal.entity.OutbreakPortalEntity;
import com.scouter.cobblemonoutbreaks.util.PortalUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import java.util.*;

@EventBusSubscriber(modid = CobblemonOutbreaks.MODID, bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Random RANDOM = new Random(); // Single instance for efficiency

    @SubscribeEvent
    public static void createRandomOutbreaks(ServerTickEvent.Post event) {
        if(OutbreakConfigManager.getConfig().getSpawningConfig().isPerPlayer() || event.getServer().overworld().isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) event.getServer().overworld();
        OutbreakWorldManager outbreakWorldManager = OutbreakWorldManager.get(serverLevel);
        if(outbreakWorldManager.getTimeLeft() <= 0) {
            outbreakWorldManager.setTimeLeft(OutbreakConfigManager.getConfig().getSpawningConfig().getRandomOutbreakTimer());
        }
        if(outbreakWorldManager.decreaseAndGetTime() > 0) {
            return;
        }
        List<ServerPlayer> allPlayers = event.getServer().getPlayerList().getPlayers();
        int maxPlayers = OutbreakConfigManager.getConfig().getSpawningConfig().getRandomPlayerCount();
        List<ServerPlayer> selectedPlayers = getRandomPlayers(allPlayers, maxPlayers);

        for (ServerPlayer player : selectedPlayers) {
            processOutbreaks(player, OutbreakConfigManager.getConfig().getSpawningConfig().getOutbreakSpawnCount());
        }


    }

    private static List<ServerPlayer> getRandomPlayers(List<ServerPlayer> players, int maxPlayers) {
        if (players.isEmpty()) return Collections.emptyList();
        int numToSelect = Math.min(maxPlayers, players.size());

        List<ServerPlayer> shuffledPlayers = new ArrayList<>(players);
        Collections.shuffle(shuffledPlayers, RANDOM);

        return shuffledPlayers.subList(0, numToSelect);
    }


    @SubscribeEvent
    public static void createPlayerOutbreaks(PlayerTickEvent.Post event) {
        if (!OutbreakConfigManager.getConfig().getSpawningConfig().isPerPlayer() ||
                !(event.getEntity() instanceof ServerPlayer serverPlayer) ||
                serverPlayer.level().isClientSide)
            return;

        ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
        OutbreakPlayerManager playerManager = OutbreakPlayerManager.get(serverLevel);
        UUID playerUUID = serverPlayer.getUUID();

        if (!playerManager.containsUUID(playerUUID)) {
            playerManager.setTimeLeft(playerUUID, OutbreakConfigManager.getConfig().getSpawningConfig().getPerPlayerOutbreakTimer());
        }
        int timeLeft = playerManager.getTimeLeft(playerUUID);
        if (timeLeft-- > 0) {
            playerManager.setTimeLeft(playerUUID, timeLeft);
            return;
        }
        processOutbreaks(serverPlayer, OutbreakConfigManager.getConfig().getSpawningConfig().getOutbreakSpawnCount());

        playerManager.setTimeLeft(playerUUID, OutbreakConfigManager.getConfig().getSpawningConfig().getPerPlayerOutbreakTimer());
    }

    public static void processOutbreaks(ServerPlayer serverPlayer, int outbreakCount) {
        if (serverPlayer.level() instanceof ServerLevel level) {
            for (int i = 0; i < outbreakCount; i++) {
                BlockPos pos = PortalUtils.findSuitableSpawnPoint(serverPlayer);
                int y = pos.getY();


                if (PortalUtils.chunkIsProtected(level, pos)) {
                    PortalUtils.handleChunkProtection(serverPlayer, pos);
                    continue;
                }

                if (!PortalUtils.isValidSpawnY(serverPlayer, y)) {
                    PortalUtils.handleInvalidSpawnY(serverPlayer, y);
                    continue;
                }

                PortalUtils.spawnPortal(serverPlayer, pos);
            }
        }
    }


    @SubscribeEvent
    public static void tickOutbreaks(LevelTickEvent.Pre event) {
        if (event.getLevel().isClientSide || !CobblemonOutbreaks.serverStarted) return;
        ServerLevel serverLevel = (ServerLevel) event.getLevel();
        OutbreakManager outbreakManager = OutbreakManager.get(serverLevel);
        outbreakManager.setLevel(serverLevel);
        Map<UUID, OutbreakPortalEntity> outbreaks = outbreakManager.getOutbreakPortalEntityMap();
        for(Map.Entry<UUID, OutbreakPortalEntity> entry : outbreaks.entrySet()){
            BlockPos pos = entry.getValue().getBlockPosition();
            ChunkPos chunkPos = new ChunkPos(pos);
            OutbreakPortalEntity outbreakPortal = entry.getValue();
            if(outbreakPortal.isWasCleared()) outbreakManager.removePortal(outbreakPortal.getEntityIdData().getOutbreakUUID());
            if(serverLevel.getChunkSource().hasChunk(chunkPos.x, chunkPos.z)) {
                if (outbreakPortal.getLevel() == null) outbreakPortal.setLevel(serverLevel);
                if (outbreakPortal.getOutbreakManager() == null) outbreakPortal.setOutbreakManager(PokemonOutbreakManager.get(serverLevel));
                outbreakPortal.tick();
            }
        }
    }

    @SubscribeEvent
    public static void checkDespawn(EntityLeaveLevelEvent event) {
        if(event.getLevel().isClientSide || !(event.getEntity() instanceof PokemonEntity pokemonEntity)) return;
        ServerLevel serverLevel = (ServerLevel) event.getLevel();
        PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
        UUID pokemonUUID = pokemonEntity.getUUID();
        if (!outbreakManager.containsUUID(pokemonUUID)) return;
        UUID ownerUUID = outbreakManager.getOwnerUUID(pokemonUUID);
        outbreakManager.removePokemonUUID(pokemonUUID);
        outbreakManager.addPokemonWOwnerTemp(pokemonUUID, ownerUUID);
        return;
    }

    @SubscribeEvent
    public static void checkSpawn(EntityJoinLevelEvent event) {
        if(event.getLevel().isClientSide || !(event.getEntity() instanceof PokemonEntity pokemonEntity) || !event.loadedFromDisk()) return;
        ServerLevel serverLevel = (ServerLevel) event.getLevel();
        PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
        UUID pokemonUUID = pokemonEntity.getUUID();
        if (!outbreakManager.containsUUIDTemp(pokemonUUID)) return;
        UUID ownerUUID = outbreakManager.getOwnerUUIDTemp(pokemonUUID);
        outbreakManager.removePokemonUUIDTemp(pokemonUUID);
        outbreakManager.addPokemonWOwner(pokemonUUID, ownerUUID);
        return;
    }

    private static int flushTimerTempMap = OutbreakConfigManager.getConfig().getGeneral().getTempOutbreaksFlushTimer();
    private static int flushTimerMap = OutbreakConfigManager.getConfig().getGeneral().getOutbreaksFlushTimer();

    @SubscribeEvent
    public static void flushOutbreakTempMap(LevelTickEvent.Post event) {
        if ((event.getLevel().isClientSide)) return;
        ServerLevel serverLevel = (ServerLevel) event.getLevel();
        tickTempFlushTimer(serverLevel);
        tickFlushTimer(serverLevel);
    }

    public static void tickTempFlushTimer(ServerLevel serverLevel){
        if (flushTimerTempMap-- > 0) return;
        PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
        outbreakManager.clearTempMap();
        flushTimerTempMap =  OutbreakConfigManager.getConfig().getGeneral().getTempOutbreaksFlushTimer();
    }

    public static void tickFlushTimer(ServerLevel serverLevel){
        if (flushTimerMap-- > 0) return;
        serverLevel.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("cobblemonoutbreaks.clearing_pokemon_outbreaks_map").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
        PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
        outbreakManager.clearTempMap();
        flushTimerMap =  OutbreakConfigManager.getConfig().getGeneral().getOutbreaksFlushTimer();
    }


    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event){
        CobblemonOutbreaks.serverlevel = event.getServer().getLevel(Level.OVERWORLD);
        OutbreakManager.get(event.getServer().getLevel(Level.OVERWORLD));
        PokemonOutbreakManager.get(event.getServer().getLevel(Level.OVERWORLD));
        OutbreakPlayerManager.get(event.getServer().getLevel(Level.OVERWORLD));
        OutbreakWorldManager.get(event.getServer().getLevel(Level.OVERWORLD));
    }

    @SubscribeEvent
    public static void levelLoaded(LevelEvent.Load event){
        CobblemonOutbreaks.serverStarted = true;
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(AddReloadListenerEvent event){
        event.addListener(new OutbreaksJsonDataManager());
    }
}

