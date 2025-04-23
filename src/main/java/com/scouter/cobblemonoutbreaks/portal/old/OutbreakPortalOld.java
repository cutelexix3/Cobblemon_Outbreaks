package com.scouter.cobblemonoutbreaks.portal.old;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scouter.cobblemonoutbreaks.data.*;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortal;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortalSpawnBiomes;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortalSpawnSettings;
import com.scouter.cobblemonoutbreaks.portal.OutbreakSpecies;
import com.scouter.cobblemonoutbreaks.reward.ItemStackRewards;
import com.scouter.cobblemonoutbreaks.reward.OutbreakRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OutbreakPortalOld {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static Codec<OutbreakPortalOld> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    OutbreakSpeciesOld.CODEC.fieldOf("species_data").forGetter(s -> s.speciesData),
                    OutbreakRewardsOld.CODEC.optionalFieldOf("rewards", OutbreakRewardsOld.getDefaultRewards()).forGetter(r -> r.outbreakRewards),
                    OutbreakAlgorithmsOld.CODEC.optionalFieldOf("algorithms", OutbreakAlgorithmsOld.getDefaultAlgoritms()).forGetter(a -> a.outbreakAlgorithms),
                    Codec.INT.optionalFieldOf("gate_timer", 36000).forGetter(t -> t.gateTimer),
                    Codec.intRange(-63, 255).optionalFieldOf("outbreak_min_y", -63).forGetter(r -> r.minOutbreakY),
                    Codec.intRange(-63, 255).optionalFieldOf("outbreak_max_y", 255).forGetter(r -> r.maxOutbreakY),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("biome", Collections.emptyList()).forGetter(t -> t.biomeTags)
            )
            .apply(inst, OutbreakPortalOld::new)
    );


    private OutbreakSpeciesOld speciesData;
    private OutbreakRewardsOld outbreakRewards;
    protected int gateTimer;
    private OutbreakAlgorithmsOld outbreakAlgorithms;
    protected final List<ResourceLocation> spawnBiomeTags;
    protected final List<ExtraCodecs.TagOrElementLocation> biomeTags;

    protected final List<ResourceLocation> spawnBiome;
    private ResourceLocation jsonLocation;
    private int minOutbreakY;
    private int maxOutbreakY;
    private boolean isOld;

    /**
     * Creates an OutbreakPortalOld instance.
     *
     * @param rewards            The list of rewards that will be granted at the end of this wave.
     * @param outbreakAlgorithms The algorithms used.
     * @param gateTimer          The time limit for completing this wave.
     * @param spawnBiome         The biomes the entity can spawn in.
     */
    public OutbreakPortalOld(OutbreakSpeciesOld speciesData, OutbreakRewardsOld rewards,
                             OutbreakAlgorithmsOld outbreakAlgorithms,
                             int gateTimer, int minOutbreakY, int maxOutbreakY, List<ExtraCodecs.TagOrElementLocation> spawnBiome) {
        this.speciesData = speciesData;
        this.outbreakRewards = rewards;
        this.outbreakAlgorithms = outbreakAlgorithms;
        this.gateTimer = gateTimer;
        this.minOutbreakY = minOutbreakY;
        this.maxOutbreakY = maxOutbreakY;
        List<ResourceLocation> spawnBiomeTags = new ArrayList<>();
        List<ResourceLocation> spawnBiomes = new ArrayList<>();
        for (ExtraCodecs.TagOrElementLocation tagOrElementLocation : spawnBiome) {
            if (tagOrElementLocation.tag()) {
                spawnBiomeTags.add(tagOrElementLocation.id());
            } else {
                spawnBiomes.add(tagOrElementLocation.id());
            }
        }

        this.spawnBiome = spawnBiomes;
        this.spawnBiomeTags = spawnBiomeTags;
        this.biomeTags = spawnBiome;

    }

    public List<ExtraCodecs.TagOrElementLocation> getBiomeTags() {
        return biomeTags;
    }

    public void setJsonLocation(ResourceLocation location) {
        this.jsonLocation = location;
    }


    public ResourceLocation getJsonLocation() {
        try {
            return jsonLocation;
        } catch (Exception e) {
            LOGGER.error("Could not find jsonLocation due to {}", e);
        }
        return ResourceLocation.withDefaultNamespace("");
    }


    public OutbreakAlgorithmsOld getOutbreakAlgorithms() {
        return outbreakAlgorithms;
    }


    public double getMaxGateTime() {
        return this.gateTimer;
    }

    public List<ResourceLocation> getSpawnBiomeTags() {
        return this.spawnBiomeTags;
    }

    public List<ResourceLocation> getSpawnBiome() {
        return this.spawnBiome;
    }

    public OutbreakSpeciesOld getSpeciesData() {
        return speciesData;
    }

    public OutbreakRewardsOld getOutbreakRewards() {
        return outbreakRewards;
    }

    public int getMinOutbreakY() {
        return minOutbreakY;
    }

    public int getMaxOutbreakY() {
        return maxOutbreakY;
    }

    public void setOld(boolean old) {
        isOld = old;
    }

    public boolean isOld() {
        return isOld;
    }


    public OutbreakPortal getOutBreakPortalNew() {


        OutbreakWaveData waveData = new OutbreakWaveData(getSpeciesData().getWaves(), getSpeciesData().getSpawnCount());

        OutbreakSpecies species = new OutbreakSpecies(getSpeciesData().getSpecies(), waveData, SpeciesShinyData.DEFAULT, getSpeciesData().getRarity());

        List<ItemStackRewards> stackRewards = new ArrayList<>();

        for(Item item : getOutbreakRewards().getRewards()) {
            if(item.getDefaultInstance().is(Items.AIR)) {
                LOGGER.info("Skipped AIR for {}", getJsonLocation());
                continue;
            };
            stackRewards.add(new ItemStackRewards(item.getDefaultInstance(), 1));
        }

        OutbreakRewards rewards = new OutbreakRewards(stackRewards, getOutbreakRewards().getExperience());


        OutbreakPortalSpawnBiomes spawnBiomes = new OutbreakPortalSpawnBiomes(getBiomeTags(), List.of());

        OutbreakPortalSpawnSettings portalSpawnSettings = new OutbreakPortalSpawnSettings(getMinOutbreakY(), getMaxOutbreakY(), spawnBiomes);


        OutbreakPortal outbreakPortal = new OutbreakPortal(species, rewards, OutbreakAlgorithmsData.DEFAULT,portalSpawnSettings, OutbreakSoundsData.DEFAULT,OutbreakMessageData.DEFAULT, gateTimer);
        outbreakPortal.setOld(true);
        return outbreakPortal;
    }

}
