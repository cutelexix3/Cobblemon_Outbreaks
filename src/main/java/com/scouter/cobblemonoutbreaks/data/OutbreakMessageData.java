package com.scouter.cobblemonoutbreaks.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.lang.OutbreakComponentMessages;
import com.scouter.cobblemonoutbreaks.lang.OutbreakTranslatables;
import net.minecraft.ChatFormatting;

import java.util.List;

public class OutbreakMessageData {

    public static final OutbreakMessageData DEFAULT = new OutbreakMessageData(
            DefaultOutbreakMessages.UNLUCKY_SPAWN,
            DefaultOutbreakMessages.PORTAL_BIOME_SPECIFIC_SPAWN_DEBUG,
            DefaultOutbreakMessages.PORTAL_SPAWN_NEAR_BLOCKPOS,
            DefaultOutbreakMessages.PORTAL_SPAWN_NEAR,
            DefaultOutbreakMessages.GATE_FAILED_SPAWNING,
            DefaultOutbreakMessages.GATE_FINISHED,
            DefaultOutbreakMessages.GATE_TIME_FINISHED
            );

    public static final Codec<OutbreakMessageData> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    OutbreakComponentMessages.CODEC.fieldOf("unlucky_spawn").forGetter(OutbreakMessageData::getUnluckySpawn),
                    OutbreakComponentMessages.CODEC.fieldOf("portal_biome_specific_spawn_debug").forGetter(OutbreakMessageData::getPortalBiomeSpecificSpawnDebug),
                    OutbreakComponentMessages.CODEC.fieldOf("portal_spawn_near_blockpos").forGetter(OutbreakMessageData::getPortalSpawnNearBlockPos),
                    OutbreakComponentMessages.CODEC.fieldOf("portal_spawn_near").forGetter(OutbreakMessageData::getPortalSpawnNear),
                    OutbreakComponentMessages.CODEC.fieldOf("gate_failed_spawning").forGetter(OutbreakMessageData::getGateFailedSpawning),
                    OutbreakComponentMessages.CODEC.fieldOf("gate_finished").forGetter(OutbreakMessageData::getGateFinished),
                    OutbreakComponentMessages.CODEC.fieldOf("gate_time_finished").forGetter(OutbreakMessageData::getGateTimeFinished)
            )
            .apply(instance, OutbreakMessageData::new)
    );


    private final OutbreakComponentMessages unluckySpawn;
    private final OutbreakComponentMessages portalBiomeSpecificSpawnDebug;
    private final OutbreakComponentMessages portalSpawnNearBlockPos;
    private final OutbreakComponentMessages portalSpawnNear;
    private final OutbreakComponentMessages gateFailedSpawning;
    private final OutbreakComponentMessages gateFinished;
    private final OutbreakComponentMessages gateTimeFinished;

    public OutbreakMessageData(
            OutbreakComponentMessages unluckySpawn,
            OutbreakComponentMessages portalBiomeSpecificSpawnDebug,
            OutbreakComponentMessages portalSpawnNearBlockPos,
            OutbreakComponentMessages portalSpawnNear,
            OutbreakComponentMessages gateFailedSpawning,
            OutbreakComponentMessages gateFinished,
            OutbreakComponentMessages gateTimeFinished
    ) {
        this.unluckySpawn = unluckySpawn;
        this.portalBiomeSpecificSpawnDebug = portalBiomeSpecificSpawnDebug;
        this.portalSpawnNearBlockPos = portalSpawnNearBlockPos;
        this.portalSpawnNear = portalSpawnNear;
        this.gateFailedSpawning = gateFailedSpawning;
        this.gateFinished = gateFinished;
        this.gateTimeFinished = gateTimeFinished;
    }

    public OutbreakComponentMessages getUnluckySpawn() {
        return unluckySpawn;
    }

    public OutbreakComponentMessages getPortalBiomeSpecificSpawnDebug() {
        return portalBiomeSpecificSpawnDebug;
    }

    public OutbreakComponentMessages getPortalSpawnNearBlockPos() {
        return portalSpawnNearBlockPos;
    }

    public OutbreakComponentMessages getPortalSpawnNear() {
        return portalSpawnNear;
    }

    public OutbreakComponentMessages getGateFailedSpawning() {
        return gateFailedSpawning;
    }

    public OutbreakComponentMessages getGateFinished() {
        return gateFinished;
    }

    public OutbreakComponentMessages getGateTimeFinished() {
        return gateTimeFinished;
    }

    static class DefaultOutbreakMessages {
        public static final OutbreakComponentMessages UNLUCKY_SPAWN =
                new OutbreakComponentMessages(OutbreakTranslatables.UNLUCKY_SPAWN.getName(),
                        List.of(ChatFormatting.DARK_AQUA),
                        List.of());

        public static final OutbreakComponentMessages PORTAL_BIOME_SPECIFIC_SPAWN_DEBUG =
                new OutbreakComponentMessages(OutbreakTranslatables.PORTAL_BIOME_SPECIFIC_SPAWN_DEBUG.getName(),
                        List.of(ChatFormatting.GREEN),
                        List.of(ChatFormatting.GOLD, ChatFormatting.ITALIC));

        public static final OutbreakComponentMessages PORTAL_SPAWN_NEAR_BLOCKPOS =
                new OutbreakComponentMessages(OutbreakTranslatables.PORTAL_SPAWN_NEAR_BLOCKPOS.getName(),
                        List.of(ChatFormatting.GREEN),
                        List.of(ChatFormatting.GOLD, ChatFormatting.ITALIC));

        public static final OutbreakComponentMessages PORTAL_SPAWN_NEAR =
                new OutbreakComponentMessages(OutbreakTranslatables.PORTAL_SPAWN_NEAR.getName(),
                        List.of(ChatFormatting.DARK_AQUA),
                        List.of(ChatFormatting.GOLD, ChatFormatting.ITALIC));

        public static final OutbreakComponentMessages GATE_FAILED_SPAWNING =
                new OutbreakComponentMessages(OutbreakTranslatables.GATE_FAILED_SPAWNING.getName(),
                        List.of(ChatFormatting.DARK_RED),
                        List.of(ChatFormatting.GOLD, ChatFormatting.ITALIC));

        public static final OutbreakComponentMessages GATE_FINISHED =
                new OutbreakComponentMessages(OutbreakTranslatables.GATE_FINISHED.getName(),
                        List.of(ChatFormatting.GREEN),
                        List.of(ChatFormatting.GOLD, ChatFormatting.ITALIC));

        public static final OutbreakComponentMessages GATE_TIME_FINISHED =
                new OutbreakComponentMessages(OutbreakTranslatables.GATE_TIME_FINISHED.getName(),
                        List.of(ChatFormatting.RED),
                        List.of(ChatFormatting.GOLD, ChatFormatting.ITALIC));
    }

}

