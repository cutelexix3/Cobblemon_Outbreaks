package com.scouter.cobblemonoutbreaks.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class MessageConfig {
    private final boolean sendPortalSpawnMessage;
    private final boolean biomeSpecificSpawns;
    private final boolean biomeSpecificSpawnsDebug;

    public MessageConfig(boolean sendPortalSpawnMessage,
                         boolean biomeSpecificSpawns, boolean biomeSpecificSpawnsDebug) {
        this.sendPortalSpawnMessage = sendPortalSpawnMessage;
        this.biomeSpecificSpawns = biomeSpecificSpawns;
        this.biomeSpecificSpawnsDebug = biomeSpecificSpawnsDebug;
    }

    public boolean isSendPortalSpawnMessage() {
        return sendPortalSpawnMessage;
    }

    public boolean isBiomeSpecificSpawns() {
        return biomeSpecificSpawns;
    }

    public boolean isBiomeSpecificSpawnsDebug() {
        return biomeSpecificSpawnsDebug;
    }

    public static final Codec<MessageConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("send_portal_spawn_message").orElse(true).forGetter(MessageConfig::isSendPortalSpawnMessage),
                    Codec.BOOL.fieldOf("biome_specific_spawns").orElse(true).forGetter(MessageConfig::isBiomeSpecificSpawns),
                    Codec.BOOL.fieldOf("biome_specific_spawns_debug").orElse(false).forGetter(MessageConfig::isBiomeSpecificSpawnsDebug)
            ).apply(instance, MessageConfig::new)
    );

    public static MessageConfig defaultInstance() {
        return new MessageConfig(true, true, false);
    }
}