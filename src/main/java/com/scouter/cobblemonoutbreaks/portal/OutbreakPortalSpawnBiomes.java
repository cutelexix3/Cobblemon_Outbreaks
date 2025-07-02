package com.scouter.cobblemonoutbreaks.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ExtraCodecs;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OutbreakPortalSpawnBiomes {

    public static final OutbreakPortalSpawnBiomes DEFAULT = new OutbreakPortalSpawnBiomes(List.of(new ExtraCodecs.TagOrElementLocation(
            BiomeTags.IS_JUNGLE.location(), true
    )), List.of(new ExtraCodecs.TagOrElementLocation(
            BiomeTags.IS_BADLANDS.location(), true
    )));

    public static Codec<OutbreakPortalSpawnBiomes> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("biome", Collections.emptyList()).forGetter(OutbreakPortalSpawnBiomes::getSpawnBiomeList),
                    ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().optionalFieldOf("biome_blacklist", Collections.emptyList()).forGetter(OutbreakPortalSpawnBiomes::getSpawnBiomeBlackList)
            )
            .apply(inst, OutbreakPortalSpawnBiomes::new)
    );
    private final List<ExtraCodecs.TagOrElementLocation> spawnBiomeList;
    private final List<ExtraCodecs.TagOrElementLocation> spawnBiomeBlackList;

    private final List<ResourceLocation> spawnBiomeTags;
    private final List<ResourceLocation> spawnBiomes;
    private final List<ResourceLocation> blacklistBiomeTags;
    private final List<ResourceLocation> blacklistBiomes;

    public OutbreakPortalSpawnBiomes(List<ExtraCodecs.TagOrElementLocation> spawnBiomeList,
                                     List<ExtraCodecs.TagOrElementLocation> spawnBiomeBlackList) {

        this.spawnBiomeList = spawnBiomeList;
        this.spawnBiomeBlackList = spawnBiomeBlackList;

        Map<Boolean, List<ResourceLocation>> whitelistPartition = partitionBiomeList(spawnBiomeList);
        this.spawnBiomeTags = whitelistPartition.getOrDefault(true, List.of());
        this.spawnBiomes = whitelistPartition.getOrDefault(false, List.of());

        // Partition the blacklist
        Map<Boolean, List<ResourceLocation>> blacklistPartition = partitionBiomeList(spawnBiomeBlackList);
        this.blacklistBiomeTags = blacklistPartition.getOrDefault(true, List.of());
        this.blacklistBiomes = blacklistPartition.getOrDefault(false, List.of());
    }

    private static Map<Boolean, List<ResourceLocation>> partitionBiomeList(List<ExtraCodecs.TagOrElementLocation> biomeList) {
        return biomeList.stream()
                .collect(Collectors.partitioningBy(
                        ExtraCodecs.TagOrElementLocation::tag,
                        Collectors.mapping(ExtraCodecs.TagOrElementLocation::id, Collectors.toList())
                ));
    }


    public List<ExtraCodecs.TagOrElementLocation> getSpawnBiomeList() {
        return spawnBiomeList;
    }

    public List<ExtraCodecs.TagOrElementLocation> getSpawnBiomeBlackList() {
        return spawnBiomeBlackList;
    }

    public List<ResourceLocation> getSpawnBiomeTags() {
        return spawnBiomeTags;
    }

    public List<ResourceLocation> getSpawnBiomes() {
        return spawnBiomes;
    }

    public List<ResourceLocation> getBlacklistBiomeTags() {
        return blacklistBiomeTags;
    }

    public List<ResourceLocation> getBlacklistBiomes() {
        return blacklistBiomes;
    }
}
