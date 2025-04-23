package com.scouter.cobblemonoutbreaks.util;

import com.scouter.cobblemonoutbreaks.config.OutbreakConfigManager;
import com.scouter.cobblemonoutbreaks.event.CobblemonOutbreaksEvent;
import com.scouter.cobblemonoutbreaks.manager.OutbreakWorldManager;
import com.scouter.cobblemonoutbreaks.portal.entity.OutbreakPortalEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

public class PortalUtils {





    public static BlockPos findSuitableSpawnPoint(Player player) {
        int maxRange = OutbreakConfigManager.getConfig().getSpawningConfig().getMaxSpawnRadius();
        int minRange = OutbreakConfigManager.getConfig().getSpawningConfig().getMinSpawnRadius();

        int randomX = generateRandomOffset(player.level(), minRange,maxRange);
        int randomZ = generateRandomOffset(player.level(), minRange,maxRange);

        int spawnY = findSpawnYLevel(player, randomX, randomZ);

        return new BlockPos(player.getBlockX() + randomX, spawnY, player.getBlockZ() + randomZ);
    }

    /**
     * Generates a random offset within a given range and applies a random directional modifier.
     */
    private static int generateRandomOffset(Level level, int range, int range2) {
        int offset = level.random.nextInt((range2 - range)) + (level.random.nextBoolean() ? 5 : -5);
        return level.random.nextBoolean() ? -offset : offset;
    }

    /**
     * Finds the most suitable Y-coordinate for spawning based on block conditions.
     */
    private static int findSpawnYLevel(Player player, int offsetX, int offsetZ) {
        int y = (int) player.getY() + 10;
        Level level = player.level();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(player.getBlockX() + offsetX, y, player.getBlockZ() + offsetZ);

        while (shouldContinueSearching(level, pos)) {
            if (y < -64 || !level.getBlockState(pos).getFluidState().isEmpty()) {
                break;
            }
            pos.setY(--y);
        }
        return y;
    }

    /**
     * Checks whether the loop should continue searching for a valid spawn position.
     */
    private static boolean shouldContinueSearching(Level level, BlockPos pos) {
        return (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isAir()) ||
                (!level.getBlockState(pos).isAir() && !level.getBlockState(pos.below()).isAir()) ||
                (!level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isAir());
    }



    public static void sendMessageToPlayer(Player player, int y){
        if(OutbreakConfigManager.getConfig().getMessages().isSendPortalSpawnMessage()) {
            if(OutbreakConfigManager.getConfig().getMessages().isBiomeSpecificSpawnsDebug()) {
                MutableComponent yLevel = Component.literal(String.valueOf(y)).withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC);
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.unlucky_spawn_debug", yLevel).withStyle(ChatFormatting.DARK_AQUA);
                player.sendSystemMessage(outBreakMessage);
            } else{
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.unlucky_spawn").withStyle(ChatFormatting.DARK_AQUA);
                player.sendSystemMessage(outBreakMessage);
            }
        }
    }


    public static boolean isValidSpawnY(ServerPlayer serverPlayer, int y) {
        ResourceKey<Level> dimension = serverPlayer.level().dimension();

        if ((dimension == Level.NETHER && y <= 0) ||
                (dimension == Level.END && y <= 0) ||
                (dimension == Level.OVERWORLD && y <= -64)) {
            return false;
        }
        return true;
    }

    public static boolean chunkIsProtected(ServerLevel level, BlockPos pos) {
        OutbreakWorldManager manager = OutbreakWorldManager.get(level);
        return manager.containsChunk(pos);
    }

    public static void handleInvalidSpawnY(ServerPlayer serverPlayer, int y) {
        sendMessageToPlayerY(serverPlayer, y);
    }

    private static void sendMessageToPlayerY(Player player, int y) {
        if (OutbreakConfigManager.getConfig().getMessages().isSendPortalSpawnMessage()) {
            if (OutbreakConfigManager.getConfig().getMessages().isBiomeSpecificSpawnsDebug()) {
                MutableComponent yLevel = Component.literal(String.valueOf(y))
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC);
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.unlucky_spawn_debug", yLevel)
                        .withStyle(ChatFormatting.DARK_AQUA);
                player.sendSystemMessage(outBreakMessage);
            } else {
                MutableComponent outBreakMessage = Component.translatable("cobblemonoutbreaks.unlucky_spawn")
                        .withStyle(ChatFormatting.DARK_AQUA);
                player.sendSystemMessage(outBreakMessage);
            }
        }
    }

    public static void handleChunkProtection(ServerPlayer serverPlayer, BlockPos pos) {
        if (OutbreakConfigManager.getConfig().getMessages().isSendPortalSpawnMessage()) {
            MutableComponent message = Component.translatable("cobblemonoutbreaks.chunk_protected")
                    .withStyle(ChatFormatting.RED);
            serverPlayer.sendSystemMessage(message);
        }
    }


    public static void spawnPortal(ServerPlayer serverPlayer, BlockPos pos) {
        OutbreakPortalEntity outbreakPortal = new OutbreakPortalEntity(serverPlayer.level(), serverPlayer, pos);
        CobblemonOutbreaksEvent.PortalSpawn portalSpawn = new CobblemonOutbreaksEvent.PortalSpawn((ServerLevel) serverPlayer.level(), outbreakPortal);
        NeoForge.EVENT_BUS.post(portalSpawn);
    }



}
