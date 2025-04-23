package com.scouter.cobblemonoutbreaks.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.config.OutbreakConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.Map;

public class OutbreakSounds {

    public static final Codec<OutbreakSounds> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    // Optional field: sound_choice, defaulting to CONFIG.
                    SoundChoice.CODEC.fieldOf("sound_choice")
                            .forGetter(OutbreakSounds::getSoundChoice),
                    // The sound event to play.
                    BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("sound_to_play")
                            .forGetter(OutbreakSounds::getSoundEvent),
                    // Optional field: source, defaulting to AMBIENT.
                    SoundSourceCodec.CODEC.fieldOf("source_source")
                            .forGetter(OutbreakSounds::getSource),
                    // Optional field: config_key, defaulting to an empty string.
                    Codec.STRING.fieldOf("config_key")
                            .forGetter(OutbreakSounds::getConfigKey),
                    // A fallback volume if not overridden by the config.
                    Codec.FLOAT.fieldOf("volume")
                            .forGetter(OutbreakSounds::getVolume),
                    // Optional field: pitch, defaulting to 1.
                    Codec.INT.fieldOf("pitch")
                            .forGetter(OutbreakSounds::getPitch)
            ).apply(inst, OutbreakSounds::new)
    );

    private final SoundChoice choice;
    private final SoundEvent event;
    private final SoundSource source;
    // New field: if the sound choice is CONFIG, this key is used to lookup the volume.
    private final String configKey;
    private final float volume;
    private final int pitch;

    public OutbreakSounds(SoundChoice choice, SoundEvent event, SoundSource source, String configKey, float volume, int pitch) {
        this.choice = choice;
        this.event = event;
        this.source = source;
        this.configKey = configKey;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundChoice getSoundChoice() {
        return choice;
    }

    public SoundEvent getSoundEvent() {
        return event;
    }

    public SoundSource getSource() {
        return source;
    }

    public String getConfigKey() {
        return configKey;
    }

    public float getVolume() {
        return volume;
    }

    public int getPitch() {
        return pitch;
    }

    /**
     * Plays the sound in the given ServerLevel at the specified BlockPos.
     * If the sound choice is CONFIG, it uses the volume defined in the config,
     * looked up by the configKey. Otherwise, it uses the provided volume.
     */
    public void playSound(ServerLevel level, BlockPos pos) {
        if (choice == SoundChoice.CONFIG) {
            Map<String, Float> volumes = OutbreakConfigManager.getConfig().getSound().getVolumes();
            // Use the volume from the config if the key exists; otherwise fallback to the volume field.
            float configVolume = volumes.getOrDefault(configKey, volume);
            level.playSound(null, pos, event, source, configVolume, pitch);
        } else {
            level.playSound(null, pos, event, source, volume, pitch);
        }
    }

    public enum SoundChoice implements StringRepresentable {
        CONFIG,
        CUSTOM;

        public static final Codec<SoundChoice> CODEC = StringRepresentable.fromEnum(SoundChoice::values);

        public static SoundChoice byName(String name) {
            return SoundChoice.valueOf(name.toUpperCase(Locale.ROOT));
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public static SoundChoice fromId(int id) {
            return SoundChoice.values()[id];
        }
    }
}
