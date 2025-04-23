package com.scouter.cobblemonoutbreaks.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public final class OutbreakConfig {
    private final GeneralConfig general;
    private final SoundConfig sound;
    private final MessageConfig messages;
    private final RarityConfig rarities;
    private final PortalSpawningConfig spawningConfig;
    private final ShinyConfig shinyConfig;
    public OutbreakConfig(GeneralConfig general, SoundConfig sound, MessageConfig messages, RarityConfig rarities, PortalSpawningConfig spawningConfig, ShinyConfig shinyConfig) {
        this.general = general;
        this.sound = sound;
        this.messages = messages;
        this.rarities = rarities;
        this.spawningConfig = spawningConfig;
        this.shinyConfig = shinyConfig;
    }

    public GeneralConfig getGeneral() {
        return general;
    }

    public SoundConfig getSound() {
        return sound;
    }

    public MessageConfig getMessages() {
        return messages;
    }

    public RarityConfig getRarities() {
        return rarities;
    }

    public PortalSpawningConfig getSpawningConfig() {
        return spawningConfig;
    }

    public ShinyConfig getShinyConfig() {
        return shinyConfig;
    }

    public static final Codec<OutbreakConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    GeneralConfig.CODEC.fieldOf("general")
                            .orElseGet(GeneralConfig::defaultInstance)
                            .forGetter(OutbreakConfig::getGeneral),
                    SoundConfig.CODEC.fieldOf("sound")
                            .orElseGet(SoundConfig::defaultInstance)
                            .forGetter(OutbreakConfig::getSound),
                    MessageConfig.CODEC.fieldOf("messages")
                            .orElseGet(MessageConfig::defaultInstance)
                            .forGetter(OutbreakConfig::getMessages),
                    RarityConfig.CODEC.fieldOf("rarities")
                            .orElseGet(RarityConfig::defaultInstance)
                            .forGetter(OutbreakConfig::getRarities),
                    PortalSpawningConfig.CODEC.fieldOf("portal_spawning_config")
                            .orElseGet(()->PortalSpawningConfig.DEFAULT)
                            .forGetter(OutbreakConfig::getSpawningConfig),
                    ShinyConfig.CODEC.fieldOf("shiny_config")
                            .orElseGet(ShinyConfig::defaultInstance)
                            .forGetter(OutbreakConfig::getShinyConfig)
            ).apply(instance, OutbreakConfig::new)
    );

    public static final OutbreakConfig DEFAULT = new OutbreakConfig(GeneralConfig.defaultInstance(), SoundConfig.defaultInstance(), MessageConfig.defaultInstance(), RarityConfig.defaultInstance(), PortalSpawningConfig.DEFAULT, ShinyConfig.defaultInstance());
}