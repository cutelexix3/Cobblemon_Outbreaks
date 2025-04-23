package com.scouter.cobblemonoutbreaks.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.scouter.cobblemonoutbreaks.config.OutbreakConfigManager;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortal;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortalSpawnSettings;
import com.scouter.cobblemonoutbreaks.portal.PokemonRarity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;

import static com.scouter.cobblemonoutbreaks.CobblemonOutbreaks.prefix;

public class OutbreaksJsonDataManager extends SimpleJsonResourceReloadListener {

    private static final Gson STANDARD_GSON = new Gson();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String folderName;
    protected static Map<ResourceLocation, OutbreakPortal> data = new HashMap<>();
    protected static Map<Holder<Biome>, Map<PokemonRarity, List<OutbreakPortal>>> biomeData = new HashMap<>();
    protected static Map<PokemonRarity, List<ResourceLocation>> listWithRarity = new HashMap<>();
    protected static Map<Holder<Biome>, List<ResourceLocation>> resourceLocationMap = new HashMap();
    protected static List<ResourceLocation> resourceLocationList = new ArrayList<>();

    public OutbreaksJsonDataManager() {
        this(STANDARD_GSON, prefix("outbreaks").getPath());
    }

    public OutbreaksJsonDataManager(Gson gson, String folderName) {
        super(gson, folderName);
        this.folderName = folderName;
    }



    public static OutbreakPortal getPortalFromResourceLocation(ResourceLocation location) {
        return data.getOrDefault(location, OutbreakPortal.DEFAULT);
    }

    public static OutbreakPortal getRandomPortalFromBiome(ServerLevel level, Holder<Biome> biome) {
        if(biomeData.isEmpty()) {
            populateBiomes(level);
        }

        Map<PokemonRarity, List<OutbreakPortal>> rarityMap = biomeData.get(biome);
        if (rarityMap == null || rarityMap.isEmpty()) {
            return getRandomPortal(level); // Fallback to any random portal
        }

        // Get a random rarity based on predefined rarity probabilities
        PokemonRarity rarity = OutbreakConfigManager.getConfig().getRarities().getRandomRarity(level.getRandom());
        List<OutbreakPortal> portals = rarityMap.getOrDefault(rarity, rarityMap.get(PokemonRarity.COMMON));

        if (portals == null || portals.isEmpty()) {
            return getRandomPortal(level); // Fallback if no portals exist for the rarity
        }

        // Return a random portal from the selected rarity list
        return portals.get(level.getRandom().nextInt(portals.size()));
    }

    public static OutbreakPortal getRandomPortal(Level level) {
        ResourceLocation rl = getRandomResourceLocation(level);
        return data.getOrDefault(rl, OutbreakPortal.DEFAULT);
    }

    private static ResourceLocation getRandomResourceLocation(Level level) {
        if (!listWithRarity.isEmpty()) {
            List<ResourceLocation> resourceLocations = listWithRarity.getOrDefault(PokemonRarity.COMMON, null);
            if(resourceLocations == null || resourceLocations.isEmpty()) return null;
            return resourceLocations.get(level.random.nextInt(resourceLocations.size()));
        }
        return null;
    }

    private static void populateBiomes(ServerLevel level) {
        Map<Holder<Biome>, Map<PokemonRarity, List<OutbreakPortal>>> newBiomeData = new HashMap<>();
        Map<Holder<Biome>, List<ResourceLocation>> resourceLocationBiomeMap = new HashMap<>();

        for (OutbreakPortal portal : getData().values()) {
            OutbreakPortalSpawnSettings settings = portal.getOutbreakPortalSpawnSettings();

            List<ResourceLocation> spawnTags = settings.getOutbreakPortalSpawnBiomes().getSpawnBiomeTags();
            List<ResourceLocation> directBiomes = settings.getOutbreakPortalSpawnBiomes().getSpawnBiomes();
            List<ResourceLocation> blacklistTags = settings.getOutbreakPortalSpawnBiomes().getBlacklistBiomeTags();
            List<ResourceLocation> blacklistBiomes = settings.getOutbreakPortalSpawnBiomes().getBlacklistBiomes();

            // Step 1: Collect valid biomes
            Set<Holder<Biome>> validBiomes = new HashSet<>();
            collectBiomesFromTags(level, spawnTags, validBiomes);
            validBiomes.addAll(convertToBiomeHolders(level, directBiomes));

            // Step 2: Collect blacklisted biomes
            Set<Holder<Biome>> blacklistedBiomes = new HashSet<>();
            collectBiomesFromTags(level, blacklistTags, blacklistedBiomes);
            blacklistedBiomes.addAll(convertToBiomeHolders(level, blacklistBiomes));

            // Step 3: Remove blacklisted biomes
            validBiomes.removeAll(blacklistedBiomes);

            // Step 4: Process final biomes
            for (Holder<Biome> biomeHolder : validBiomes) {
                addPortalToBiomeData(portal, biomeHolder, newBiomeData, resourceLocationBiomeMap);
            }
        }

        LOGGER.info("Registered {} biomes with Pokémon!", newBiomeData.keySet().size());
        biomeData.putAll(newBiomeData);
        resourceLocationMap.putAll(resourceLocationBiomeMap);
    }

    private static void collectBiomesFromTags(ServerLevel level, List<ResourceLocation> tags, Set<Holder<Biome>> biomeSet) {
        for (ResourceLocation tag : tags) {
            TagKey<Biome> biomeTagKey = TagKey.create(Registries.BIOME, tag);

            level.registryAccess().registry(Registries.BIOME).ifPresent(reg -> {
                for (Holder<Biome> biome : reg.getTagOrEmpty(biomeTagKey)) {
                    biomeSet.add(biome);
                }
            });
        }
    }

    private static Set<Holder<Biome>> convertToBiomeHolders(ServerLevel level, List<ResourceLocation> biomeLocations) {
        Set<Holder<Biome>> biomeHolders = new HashSet<>();
        level.registryAccess().registry(Registries.BIOME).ifPresent(registry -> {
            for (ResourceLocation biomeLocation : biomeLocations) {
                Optional<Holder.Reference<Biome>> biomeHolder = registry.getHolder(ResourceKey.create(Registries.BIOME, biomeLocation));
                biomeHolder.ifPresent(biomeHolders::add);
            }
        });
        return biomeHolders;
    }


    private static void addPortalToBiomeData(OutbreakPortal portal, Holder<Biome> biomeHolder,
                                             Map<Holder<Biome>, Map<PokemonRarity, List<OutbreakPortal>>> biomeData,
                                             Map<Holder<Biome>, List<ResourceLocation>> resourceLocationBiomeMap) {
        PokemonRarity rarity = portal.getSpeciesData().getPokemonRarity();

        biomeData.computeIfAbsent(biomeHolder, k -> new HashMap<>())
                .computeIfAbsent(rarity, k -> new ArrayList<>())
                .add(portal);

        resourceLocationBiomeMap.computeIfAbsent(biomeHolder, k -> new ArrayList<>()).add(portal.getId());
    }


    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        LOGGER.info("Beginning loading of data for data loader: {}", this.getFolderName());
        this.listWithRarity.clear();
        this.resourceLocationList.clear();
        this.resourceLocationMap.clear();
        this.data.clear();
        this.biomeData.clear();
        Map<ResourceLocation, OutbreakPortal> newMap = new HashMap<>();

        Map<PokemonRarity, List<ResourceLocation>> newResourceLocationMap = new HashMap<>();

        List<ResourceLocation> newResourceLocationList = new ArrayList<>();


        for (Map.Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
            ResourceLocation key = entry.getKey();
            JsonElement element = entry.getValue();
            OutbreakPortal.EITHER.decode(JsonOps.INSTANCE, element)
                    .ifSuccess(success -> {
                        OutbreakPortal portal = success.getFirst();
                        portal.setId(key);
                        newMap.put(key, portal);


                        newResourceLocationList.add(key);
                        PokemonRarity rarity = portal.getSpeciesData().getPokemonRarity();
                        List<ResourceLocation> resourceLocations = newResourceLocationMap.computeIfAbsent(rarity, k -> new ArrayList<>());
                        resourceLocations.add(key);
                        newResourceLocationMap.put(rarity, resourceLocations);



                    })
                    .ifError(error -> LOGGER.error("Failed to parse data json for {} due to: {}", key, error.message()));

        }
        this.listWithRarity = newResourceLocationMap;
        this.resourceLocationList = newResourceLocationList;
        this.data = newMap;

        LOGGER.info("Data loader for {} loaded {} jsons", this.getFolderName(), this.getData().size());

    }

    public static Map<ResourceLocation, OutbreakPortal> getData() {
        return data;
    }

    public String getFolderName() {
        return folderName;
    }
}
