package com.scouter.cobblemonoutbreaks.portal;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.data.*;
import com.scouter.cobblemonoutbreaks.portal.old.OutbreakPortalOld;
import com.scouter.cobblemonoutbreaks.reward.OutbreakRewards;
import net.minecraft.resources.ResourceLocation;


public class OutbreakPortal {


    public static final OutbreakPortal DEFAULT = new OutbreakPortal(
            new OutbreakSpecies("pikachu",
                    new OutbreakWaveData(3, 6),
                    SpeciesShinyData.DEFAULT,
                    PokemonRarity.COMMON
            ),
            OutbreakRewards.WITH_STACK,
            OutbreakAlgorithmsData.DEFAULT,
            OutbreakPortalSpawnSettings.DEFAULT,
            OutbreakSoundsData.DEFAULT,
            OutbreakMessageData.DEFAULT,
            36000
    );


    public static final Codec<OutbreakPortal> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    OutbreakSpecies.CODEC.fieldOf("species_data").forGetter(OutbreakPortal::getSpeciesData),
                    OutbreakRewards.CODEC.fieldOf("rewards").forGetter(OutbreakPortal::getRewards),
                    OutbreakAlgorithmsData.CODEC.fieldOf("algorithms").forGetter(OutbreakPortal::getOutbreakAlgorithms),
                    OutbreakPortalSpawnSettings.CODEC.fieldOf("spawn_settings").forGetter(OutbreakPortal::getOutbreakPortalSpawnSettings),
                    OutbreakSoundsData.CODEC.optionalFieldOf("sounds",OutbreakSoundsData.DEFAULT).forGetter(OutbreakPortal::getOutbreakSounds),
                    OutbreakMessageData.CODEC.optionalFieldOf("messages", OutbreakMessageData.DEFAULT).forGetter(OutbreakPortal::getOutbreakMessageData),
                    Codec.INT.fieldOf("gate_timer").forGetter(OutbreakPortal::getGateTimer)
            ).apply(instance, OutbreakPortal::new)
    );

    public static Codec<OutbreakPortal> EITHER = Codec.either(OutbreakPortalOld.CODEC, OutbreakPortal.CODEC).xmap(either -> {
                if (either.left().isPresent()) {
                    OutbreakPortalOld outbreakPortalOld = either.left().get();
                    OutbreakPortal OutbreakPortalOld = outbreakPortalOld.getOutBreakPortalNew();
                    return OutbreakPortalOld;
                }



                OutbreakPortal outbreakPortal = either.right().get();
                return outbreakPortal;
            },
            Either::right
    );


    private final OutbreakSpecies species;
    private final OutbreakRewards rewards;
    private final OutbreakAlgorithmsData outbreakAlgorithms;
    private final OutbreakPortalSpawnSettings outbreakPortalSpawnSettings;
    private final OutbreakSoundsData outbreakSounds;
    private final OutbreakMessageData outbreakMessageData;
    private final int gateTimer;
    private ResourceLocation id;
    private boolean isOld;


    public OutbreakPortal(OutbreakSpecies species,
                          OutbreakRewards rewards,
                          OutbreakAlgorithmsData outbreakAlgorithms,
                          OutbreakPortalSpawnSettings outbreakPortalSpawnSettings,
                          OutbreakSoundsData outbreakSounds,
                          OutbreakMessageData outbreakMessageData,
                          int gateTimer) {
        this.species = species;
        this.rewards = rewards;
        this.outbreakAlgorithms = outbreakAlgorithms;
        this.outbreakPortalSpawnSettings = outbreakPortalSpawnSettings;
        this.outbreakSounds = outbreakSounds;
        this.outbreakMessageData = outbreakMessageData;
        this.gateTimer = gateTimer;
    }

    public OutbreakSpecies getSpeciesData() {
        return species;
    }

    public OutbreakRewards getRewards() {
        return rewards;
    }

    public OutbreakAlgorithmsData getOutbreakAlgorithms() {
        return outbreakAlgorithms;
    }

    public OutbreakPortalSpawnSettings getOutbreakPortalSpawnSettings() {
        return outbreakPortalSpawnSettings;
    }

    public OutbreakSoundsData getOutbreakSounds() {
        return outbreakSounds;
    }

    public OutbreakMessageData getOutbreakMessageData() {
        return outbreakMessageData;
    }

    public int getGateTimer() {
        return gateTimer;
    }

    public ResourceLocation getId() {
        return id;
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    public boolean isOld() {
        return isOld;
    }

    public void setOld(boolean old) {
        isOld = old;
    }

    @Override
    public String toString() {
        return "OutbreakPortal{" +
                "species=" + species +
                ", rewards=" + rewards +
                ", outbreakAlgorithms=" + outbreakAlgorithms +
                ", outbreakPortalSpawnSettings=" + outbreakPortalSpawnSettings +
                ", outbreakSounds=" + outbreakSounds +
                ", outbreakMessageData=" + outbreakMessageData +
                ", gateTimer=" + gateTimer +
                '}';
    }


}

