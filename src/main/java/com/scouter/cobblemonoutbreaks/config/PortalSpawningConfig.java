package com.scouter.cobblemonoutbreaks.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public class PortalSpawningConfig {

    public static final Codec<PortalSpawningConfig> CODEC = RecordCodecBuilder.<PortalSpawningConfig>create(instance ->
            instance.group(
                    SpawnChoice.CODEC.fieldOf("portal_spawn_choice").forGetter(PortalSpawningConfig::getChoice),
                    Codec.INT.fieldOf("per_player_outbreak_timer").forGetter(PortalSpawningConfig::getPerPlayerOutbreakTimer),
                    Codec.INT.fieldOf("random_outbreak_timer").forGetter(PortalSpawningConfig::getRandomOutbreakTimer),
                    Codec.INT.fieldOf("random_player_count").forGetter(PortalSpawningConfig::getRandomPlayerCount),
                    Codec.INT.fieldOf("outbreak_spawn_count").forGetter(PortalSpawningConfig::getOutbreakSpawnCount),
                    Codec.INT.fieldOf("min_spawn_radius").forGetter(PortalSpawningConfig::getMinSpawnRadius),
                    Codec.INT.fieldOf("max_spawn_radius").forGetter(PortalSpawningConfig::getMaxSpawnRadius)
            ).apply(instance, PortalSpawningConfig::new)
    ).validate(e -> {
        return (e.minSpawnRadius > e.maxSpawnRadius || e.maxSpawnRadius == e.minSpawnRadius || e.minSpawnRadius < 16 || e.maxSpawnRadius > 128) ? DataResult.error(() -> {
            return "min_spawn_radius must be smaller than max_spawn_radius and min_spawn_radius cant be smaller than 16 and max_spawn_radius cant be bigger than 128";
        }) : DataResult.success(e); });

    public static final PortalSpawningConfig DEFAULT = new PortalSpawningConfig(SpawnChoice.PER_PLAYER, 36000, 36000, 2, 3, 32, 64);

    private final SpawnChoice portalSpawnChoice;
    private final int perPlayerOutbreakTimer;
    private final int randomOutbreakTimer;
    private final int randomPlayerCount;
    private final int outbreakSpawnCount;
    private final int minSpawnRadius;
    private final int maxSpawnRadius;

    public PortalSpawningConfig(SpawnChoice choice, int perPlayerTimer, int randomTimer, int randomPlayers, int outbreakCount, int minRadius, int maxRadius) {
        this.portalSpawnChoice = choice;
        this.perPlayerOutbreakTimer = perPlayerTimer;
        this.randomOutbreakTimer = randomTimer;
        this.randomPlayerCount = randomPlayers;
        this.outbreakSpawnCount = outbreakCount;
        this.minSpawnRadius = minRadius;
        this.maxSpawnRadius = maxRadius;
    }



    public int getOutbreakSpawnCount() {
        return outbreakSpawnCount;
    }

    public int getPerPlayerOutbreakTimer() {
        return perPlayerOutbreakTimer;
    }

    public int getRandomOutbreakTimer() {
        return randomOutbreakTimer;
    }

    public int getRandomPlayerCount() {
        return randomPlayerCount;
    }

    public SpawnChoice getChoice() {
        return portalSpawnChoice;
    }

    public int getMinSpawnRadius() {
        return minSpawnRadius;
    }

    public int getMaxSpawnRadius() {
        return maxSpawnRadius;
    }

    public boolean isPerPlayer() {
        return portalSpawnChoice.equals(SpawnChoice.PER_PLAYER);
    }

    public enum SpawnChoice implements StringRepresentable {
        PER_PLAYER,
        RANDOM_PLAYER;

        public static final Codec<SpawnChoice> CODEC = StringRepresentable.fromEnum(SpawnChoice::values);

        public static SpawnChoice byName(String name) {
            return SpawnChoice.valueOf(name.toUpperCase(Locale.ROOT));
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public static SpawnChoice fromId(int id) {
            return SpawnChoice.values()[id];
        }
    }
}
