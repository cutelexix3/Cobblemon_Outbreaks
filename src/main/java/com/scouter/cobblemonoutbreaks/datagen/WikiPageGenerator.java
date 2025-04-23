package com.scouter.cobblemonoutbreaks.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scouter.cobblemonoutbreaks.CobblemonOutbreaks;
import com.scouter.cobblemonoutbreaks.algorithms.level.RandomAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.particle.CustomParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.particle.MovingCustomParticleAlgorithm;
import com.scouter.cobblemonoutbreaks.algorithms.spawning.OpenFieldAlgorithm;
import com.scouter.cobblemonoutbreaks.data.*;
import com.scouter.cobblemonoutbreaks.lang.OutbreakComponentMessages;
import com.scouter.cobblemonoutbreaks.portal.*;
import com.scouter.cobblemonoutbreaks.registries.LevelAlgorithmRegistry;
import com.scouter.cobblemonoutbreaks.registries.ParticleSpawningAlgorithmRegistry;
import com.scouter.cobblemonoutbreaks.registries.SpawnAlgorithmRegistry;
import com.scouter.cobblemonoutbreaks.reward.ItemStackRewards;
import com.scouter.cobblemonoutbreaks.reward.OutbreakRewards;
import net.minecraft.ChatFormatting;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class WikiPageGenerator extends WikiPageBuilderProvider{
    public WikiPageGenerator(PackOutput output) {
        super(output, "wiki", CobblemonOutbreaks.MODID);
    }
    private final List<WikiPageBuilder> pageBuilderList = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    @Override
    protected void generateWikiPages(BiConsumer<String, Supplier<String>> consumer) {
        createWikiPage("home", homePage(), consumer);
        createWikiPage("outbreak_portal", outbreakPortalPage(), consumer);
        createWikiPage("outbreak_species", outbreakSpeciesPage(), consumer);
        createWikiPage("wave_data", waveDataPage(), consumer);
        createWikiPage("shiny_mechanics", shinyMechanicsPage(), consumer);
        createWikiPage("pokemon_rarity", pokemonRarityPage(), consumer);
        createWikiPage("outbreak_rewards", outbreakRewardsPage(), consumer);
        createWikiPage("outbreak_algorithms", outbreakAlgorithmsPage(), consumer);
        createWikiPage("outbreak_spawn_settings", outbreakSpawnSettingsPage(), consumer);
        createWikiPage("outbreak_biomes", outbreakBiomesPage(), consumer);
        createWikiPage("outbreak_sounds", outbreakSoundsPage(), consumer);
        createWikiPage("outbreak_messages", outbreakMessagesPage(), consumer);
        createWikiPage("outbreak_component_messages", outbreakComponentMessagesPage(), consumer);
    }

    private WikiPageBuilder homePage() {
        WikiPageBuilder builder = new WikiPageBuilder("Home");

        builder.addParagraph("Welcome to the **Cobblemon Outbreaks Wiki**!");
        builder.addParagraph("Cobblemon Outbreaks is a mod for Minecraft that introduces dynamic Pokémon outbreaks. This mod enhances gameplay by adding temporary Pokémon portals that spawn Pokémon waves, creating immersive world events.");

        builder.addParagraph("**Core Features:**");
        builder.addList(new String[]{
                "Random Pokémon outbreaks with unique spawn settings.",
                "Configurable shiny rates and rarity tiers.",
                "Custom sounds and messages for outbreaks.",
                "Multiple waves of Pokémon spawns.",
                "Dynamic rewards for participating in outbreaks."
        });

        builder.addHeading("📌 Getting Started", 2);
        builder.addParagraph("Learn how the mod works and how to configure its settings.");
        builder.addLink("Outbreak Portals", "wiki/outbreak_portal");
        builder.addLink("Species Data", "wiki/outbreak_species");
        builder.addLink("Wave Data", "wiki/wave_data");
        builder.addLink("Shiny Mechanics", "wiki/shiny_mechanics");
        builder.addLink("Pokémon Rarity", "wiki/pokemon_rarity");

        builder.addHeading("⚙️ Advanced Configuration", 2);
        builder.addParagraph("Explore the deeper mechanics of the mod, including spawn settings, algorithms, and messages.");
        builder.addLink("Outbreak Rewards", "wiki/outbreak_rewards");
        builder.addLink("Outbreak Algorithms", "wiki/outbreak_algorithms");
        builder.addLink("Outbreak Spawn Settings", "wiki/outbreak_spawn_settings");
        builder.addLink("Outbreak Biomes", "wiki/outbreak_biomes");

        builder.addHeading("🔊 Sounds & Messages", 2);
        builder.addParagraph("Customize the sounds and messages for outbreaks.");
        builder.addLink("Outbreak Sounds", "wiki/outbreak_sounds");
        builder.addLink("Outbreak Messages", "wiki/outbreak_messages");
        builder.addLink("Component Messages", "wiki/outbreak_component_messages");

        builder.addHeading("❓ Additional Information", 2);
        builder.addParagraph("More in-depth guides and configuration options.");
        builder.addParagraph("If you need further support, check the [Cobblemon Outbreaks GitHub](https://github.com/Scouter456/CobblemonOutbreaks) for updates and discussions.");

        return builder;
    }


    private WikiPageBuilder outbreakPortalPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Outbreak Portal");

        builder.addHeading("What is an Outbreak Portal?", 2);
        builder.addParagraph("An Outbreak Portal is the core feature of Cobblemon Outbreaks. These portals act as gateways for Pokémon outbreaks, spawning waves of Pokémon in an area.");

        builder.addParagraph("Each portal has several configurable properties:");
        builder.addList(new String[]{
                "**Species Data:** Determines which Pokémon spawn in the outbreak.",
                "**Rewards:** Defines the items or bonuses given after an outbreak.",
                "**Algorithms:** Controls the spawn behavior and conditions.",
                "**Spawn Settings:** Specifies the biome, location, and timing of the outbreak.",
                "**Sounds & Messages:** Custom notifications and effects for player immersion.",
                "**Gate Timer:** Controls how long the portal remains active."
        });

        // Example JSON representation
        builder.addHeading("Example JSON", 2);
        OutbreakPortal examplePortal = new OutbreakPortal(
                new OutbreakSpecies("Pikachu", new OutbreakWaveData(3, 5), new SpeciesShinyData(SpeciesShinyData.ShinyChoice.CUSTOM, 0.05), PokemonRarity.RARE),
                OutbreakRewards.WITH_STACK,
                OutbreakAlgorithmsData.DEFAULT,
                OutbreakPortalSpawnSettings.DEFAULT,
                OutbreakSoundsData.DEFAULT,
                OutbreakMessageData.DEFAULT,
                36000
        );
        builder.addCodeBlock(encodeDataToJsonString(OutbreakPortal.CODEC, examplePortal));

        builder.addParagraph("This example portal spawns Pikachu in 3 waves with 5 Pokémon per wave, has a 5% custom shiny chance, and is classified as `RARE`.");

        return builder;
    }

    private WikiPageBuilder outbreakSpeciesPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Outbreak Species");

        builder.addHeading("Outbreak Species", 2);
        builder.addParagraph("OutbreakSpecies defines which Pokémon spawn in an outbreak and contains additional data such as wave behavior and shiny chances.");

        builder.addHeading("Properties", 2);
        builder.addList(new String[]{
                "**Pokémon Name:** Defines the species that will appear in the outbreak.",
                "**Wave Data:** Controls the number of waves and spawns per wave.",
                "**Shiny Data:** Configurable shiny rates for the Pokémon.",
                "**Rarity:** Determines how common or rare this outbreak is."
        });

        builder.addHeading("Example JSON", 2);
        OutbreakSpecies exampleSpecies = new OutbreakSpecies(
                "Charizard",
                new OutbreakWaveData(2, 4),
                new SpeciesShinyData(SpeciesShinyData.ShinyChoice.CONFIG, 0.01),
                PokemonRarity.EPIC
        );
        builder.addCodeBlock(encodeDataToJsonString(OutbreakSpecies.CODEC, exampleSpecies));

        builder.addParagraph("This example defines an outbreak for **Charizard**, with **2 waves of 4 Pokémon each**, using the **default shiny chance from config**, and classified as `EPIC` rarity.");

        return builder;
    }


    private WikiPageBuilder waveDataPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Wave Data");

        builder.addHeading("Wave Data", 2);
        builder.addParagraph("Wave Data controls how many Pokémon spawn during an outbreak and how many waves occur.");

        builder.addHeading("Properties", 2);
        builder.addList(new String[]{
                "**Waves:** Number of Pokémon waves in an outbreak.",
                "**Spawns per Wave:** Number of Pokémon that spawn per wave."
        });

        builder.addHeading("Example JSON", 2);
        OutbreakWaveData exampleWave = new OutbreakWaveData(3, 6);
        builder.addCodeBlock(encodeDataToJsonString(OutbreakWaveData.CODEC, exampleWave));

        builder.addParagraph("This example creates an outbreak with **3 waves** and **6 Pokémon per wave**.");

        return builder;
    }


    private WikiPageBuilder shinyMechanicsPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Shiny Mechanics");

        builder.addHeading("Shiny Mechanics", 2);
        builder.addParagraph("Shiny Pokémon in Cobblemon Outbreaks can be customized per species, allowing for different shiny rates based on config settings or custom values.");

        builder.addHeading("Shiny Choice Types", 2);
        builder.addList(new String[]{
                "**CONFIG:** Uses the default shiny chance from the configuration file.",
                "**CUSTOM:** Uses a manually defined shiny rate for this species."
        });

        builder.addHeading("Example JSON", 2);
        SpeciesShinyData exampleShiny = new SpeciesShinyData(SpeciesShinyData.ShinyChoice.CUSTOM, 0.02);
        builder.addCodeBlock(encodeDataToJsonString(SpeciesShinyData.CODEC, exampleShiny));

        builder.addParagraph("This example sets a **2% shiny chance** for a specific Pokémon using `CUSTOM` mode.");

        return builder;
    }

    private WikiPageBuilder pokemonRarityPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Pokémon Rarity");

        builder.addHeading("Pokémon Rarity", 2);
        builder.addParagraph("Each outbreak is assigned a rarity, which determines how common or rare it is.");

        builder.addHeading("Available Rarities", 2);
        builder.addList(new String[]{
                "**COMMON:** Frequently occurring outbreaks.",
                "**UNCOMMON:** Slightly rarer than common.",
                "**RARE:** Occurs less frequently.",
                "**EPIC:** Very rare outbreaks.",
                "**LEGENDARY:** Extremely rare outbreaks."
        });

        builder.addHeading("Example JSON", 2);
        PokemonRarity exampleRarity = PokemonRarity.RARE;
        builder.addCodeBlock(encodeDataToJsonString(PokemonRarity.CODEC, exampleRarity));

        builder.addParagraph("This example defines an outbreak with the `RARE` classification.");

        return builder;
    }

    private WikiPageBuilder outbreakRewardsPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Outbreak Rewards");

        builder.addHeading("Outbreak Rewards", 2);
        builder.addParagraph("OutbreakRewards defines the rewards given to players after successfully completing an outbreak. This includes experience and item rewards.");

        builder.addHeading("Properties", 2);
        builder.addList(new String[]{
                "**Item Rewards:** A list of potential item drops, each with its own drop chance.",
                "**Experience Reward:** The amount of experience players receive for completing the outbreak."
        });

        builder.addHeading("Item Stack Rewards", 2);
        builder.addParagraph("Each item in `ItemStackRewards` has a specific drop chance and can be configured using JSON.");

        builder.addHeading("Example JSON", 2);
        ItemStackRewards itemReward = new ItemStackRewards(Items.DIAMOND.getDefaultInstance(), 0.5f);
        OutbreakRewards exampleRewards = new OutbreakRewards(List.of(itemReward), 100);
        builder.addCodeBlock(encodeDataToJsonString(OutbreakRewards.CODEC, exampleRewards));

        builder.addParagraph("This example gives players a 50% chance of receiving an item and awards 100 experience points.");

        return builder;
    }

    private WikiPageBuilder outbreakAlgorithmsPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Outbreak Algorithms");

        builder.addHeading("Outbreak Algorithms", 2);
        builder.addParagraph("OutbreakAlgorithmsData controls how Pokémon levels are determined, how they spawn, and the particles used during outbreaks.");

        builder.addHeading("Algorithm Types", 2);
        builder.addList(new String[]{
                "**Level Algorithm:** Determines the level of spawned Pokémon.",
                "**Spawn Algorithm:** Controls how Pokémon appear in the outbreak area.",
                "**Particle Spawning Algorithm:** Handles particle effects when an outbreak occurs."
        });

        builder.addHeading("Available Level Algorithms", 2);
        builder.startCollapsibleSection("Level Algorithms");
        builder.addList(new String[]{
                "**Random Algorithm:** Generates Pokémon with completely random levels.",
                "**Scaled Algorithm:** Adjusts Pokémon levels based on the player's level or world difficulty.",
                "**Min-Max Algorithm:** Uses a predefined level range."
        });
        builder.endCollapsibleSection();

        builder.addHeading("Available Spawn Algorithms", 2);
        builder.startCollapsibleSection("Spawn Algorithms");
        builder.addList(new String[]{
                "**Inward Spiral:** Pokémon spawn in a circular pattern moving inward.",
                "**Open Field:** Spawns Pokémon randomly in an open space.",
                "**Clustered:** Groups Pokémon into close-knit spawns."
        });
        builder.endCollapsibleSection();

        builder.addHeading("Available Particle Spawning Algorithms", 2);
        builder.startCollapsibleSection("Particle Spawning Algorithms");
        builder.addList(new String[]{
                "**Custom Particle:** Uses custom-defined particle effects.",
                "**Moving Custom Particle:** Particles follow a moving animation.",
                "**Pokeball Particle:** Displays Pokéball effects during spawns.",
                "**Debug Particle:** Used for testing and debugging outbreaks."
        });
        builder.endCollapsibleSection();

        builder.addHeading("Example JSON", 2);
        OutbreakAlgorithmsData exampleAlgorithms = new OutbreakAlgorithmsData(
                RandomAlgorithm.ALGO,
                OpenFieldAlgorithm.ALGO,
                CustomParticleAlgorithm.DEBUG_PARTICLE
        );
        builder.addCodeBlock(encodeDataToJsonString(OutbreakAlgorithmsData.CODEC, exampleAlgorithms));

        builder.addParagraph("This example sets the Pokémon level to random, uses an open-field spawn pattern, and debug particles.");

        return builder;
    }

    private WikiPageBuilder outbreakSpawnSettingsPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Outbreak Spawn Settings");

        builder.addHeading("Outbreak Portal Spawn Settings", 2);
        builder.addParagraph("OutbreakPortalSpawnSettings controls where and how outbreaks spawn in the world. This includes height levels and biome-specific settings.");

        builder.addHeading("Properties", 2);
        builder.addList(new String[]{
                "**Outbreak Min Y:** The minimum height (Y-level) at which an outbreak can spawn.",
                "**Outbreak Max Y:** The maximum height (Y-level) at which an outbreak can spawn.",
                "**Biome Settings:** Defines valid biomes where outbreaks can appear."
        });

        builder.addHeading("Biome Settings", 2);
        builder.addParagraph("OutbreakPortalSpawnBiomes determines which biomes allow or restrict outbreaks.");

        builder.addHeading("Example JSON", 2);
        OutbreakPortalSpawnSettings exampleSettings = new OutbreakPortalSpawnSettings(-10, 200, OutbreakPortalSpawnBiomes.DEFAULT);
        builder.addCodeBlock(encodeDataToJsonString(OutbreakPortalSpawnSettings.CODEC, exampleSettings));

        builder.addParagraph("This example allows outbreaks to spawn between Y-level -10 and 200.");

        return builder;
    }

    private WikiPageBuilder outbreakBiomesPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Outbreak Biomes");

        builder.addHeading("Outbreak Portal Biomes", 2);
        builder.addParagraph("OutbreakPortalSpawnBiomes determines where Pokémon outbreaks occur by defining allowed and blacklisted biomes.");

        builder.addHeading("Properties", 2);
        builder.addList(new String[]{
                "**Allowed Biomes:** List of biomes where outbreaks can spawn.",
                "**Biome Blacklist:** Biomes where outbreaks are prevented."
        });

        builder.addHeading("Example JSON", 2);
        OutbreakPortalSpawnBiomes exampleBiomes = OutbreakPortalSpawnBiomes.DEFAULT;
        builder.addCodeBlock(encodeDataToJsonString(OutbreakPortalSpawnBiomes.CODEC, exampleBiomes));

        builder.addParagraph("This example allows outbreaks in **Plains** biomes but blocks them in **Deserts**.");

        return builder;
    }

    private WikiPageBuilder outbreakSoundsPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Outbreak Sounds");

        builder.addHeading("Outbreak Sounds", 2);
        builder.addParagraph("OutbreakSoundsData configures the sounds that play during various outbreak events.");
        builder.addParagraph("This is an optional field and can be left blank, it will then default to the json file in this section");
        builder.addHeading("Sound Events", 2);
        builder.addList(new String[]{
                "**Portal Spawn Sound:** Plays when an outbreak portal appears.",
                "**Pokémon Spawn Sound:** Plays when Pokémon emerge from the portal.",
                "**Shiny Spawn Sound:** Plays when a shiny Pokémon spawns."
        });

        builder.addHeading("Sound Configuration", 2);
        builder.addParagraph("Each sound has configurable options including volume, pitch, and playback source.");

        builder.addHeading("Example JSON", 2);
        OutbreakSoundsData exampleSounds = OutbreakSoundsData.DEFAULT;
        builder.addCodeBlock(encodeDataToJsonString(OutbreakSoundsData.CODEC, exampleSounds));

        builder.addParagraph("This example uses the default outbreaks sounds for the spawning");

        return builder;
    }

    private WikiPageBuilder outbreakMessagesPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Outbreak Messages");

        builder.addHeading("Outbreak Messages", 2);
        builder.addParagraph("OutbreakMessageData defines various messages that are displayed to players during an outbreak event. These messages provide feedback on events such as portal spawning, gate failures, and outbreak completion.");
        builder.addParagraph("This is an optional field and can be left blank, it will then default to the json file in this section");

        builder.addHeading("Message Types", 2);
        builder.addList(new String[]{
                "**Unlucky Spawn:** Displayed when an outbreak fails to spawn Pokémon.",
                "**Biome-Specific Debug:** Debug message for biome-specific portal spawning.",
                "**Portal Spawn Near BlockPos:** Message shown when an outbreak portal spawns near a block position.",
                "**Portal Spawn Near Player:** Message shown when an outbreak portal spawns near a player.",
                "**Gate Failed Spawning:** Shown when an outbreak fails to generate a portal or Pokémon.",
                "**Gate Finished:** Displayed when an outbreak successfully completes.",
                "**Gate Time Finished:** Message shown when an outbreak portal expires."
        });

        builder.addHeading("Example JSON", 2);
        OutbreakMessageData exampleMessages = new OutbreakMessageData(
                new OutbreakComponentMessages("message.outbreak.unlucky", List.of(ChatFormatting.RED), List.of(ChatFormatting.BOLD)),
                new OutbreakComponentMessages("message.outbreak.debug.biome", List.of(ChatFormatting.GRAY), List.of(ChatFormatting.ITALIC)),
                new OutbreakComponentMessages("message.outbreak.portal.nearpos", List.of(ChatFormatting.GREEN), List.of(ChatFormatting.UNDERLINE)),
                new OutbreakComponentMessages("message.outbreak.portal.near", List.of(ChatFormatting.YELLOW), List.of(ChatFormatting.ITALIC)),
                new OutbreakComponentMessages("message.outbreak.gate.failed", List.of(ChatFormatting.DARK_RED), List.of(ChatFormatting.BOLD)),
                new OutbreakComponentMessages("message.outbreak.gate.finished", List.of(ChatFormatting.BLUE), List.of(ChatFormatting.BOLD)),
                new OutbreakComponentMessages("message.outbreak.gate.timefinished", List.of(ChatFormatting.DARK_PURPLE), List.of(ChatFormatting.ITALIC))
        );
        builder.addCodeBlock(encodeDataToJsonString(OutbreakMessageData.CODEC, exampleMessages));

        builder.addParagraph("This example defines outbreak messages with various colors and styles to distinguish different types of notifications.");

        return builder;
    }

    private WikiPageBuilder outbreakComponentMessagesPage() {
        WikiPageBuilder builder = new WikiPageBuilder("Outbreak Component Messages");

        builder.addHeading("Outbreak Component Messages", 2);
        builder.addParagraph("OutbreakComponentMessages provides a structure for formatting and sending localized outbreak messages to players. It includes translatable messages, message formatting, and argument formatting.");

        builder.addHeading("Properties", 2);
        builder.addList(new String[]{
                "**Translatable Key:** The key for the localized message (e.g., `message.outbreak.unlucky`).",
                "**Message Formatting:** Defines the text style for the entire message (e.g., bold, italic, colored).",
                "**Argument Formatting:** Defines the text style for any dynamic arguments included in the message."
        });

        builder.addHeading("Message Formatting Options", 2);
        builder.addParagraph("Messages can be styled using `ChatFormatting`, which supports colors and text styles.");

        builder.startCollapsibleSection("Available Formatting Options");
        builder.addList(new String[]{
                "**Colors:** `RED`, `GREEN`, `BLUE`, `YELLOW`, `DARK_PURPLE`.",
                "**Styles:** `BOLD`, `ITALIC`, `UNDERLINE`, `STRIKETHROUGH`."
        });
        builder.endCollapsibleSection();

        builder.addHeading("Sending Messages", 2);
        builder.addParagraph("Messages can be sent to a specific player or to all players in a level.");

        builder.addCodeBlock("""
        // Send a formatted message to a specific player
        outbreakMessage.sendPlayerMessage(player, Component.literal("Outbreak has started!"));
        
        // Send a formatted message to a player by UUID
        outbreakMessage.sendPlayerMessage(level, playerUUID, "Outbreak has ended.");
    """);

        builder.addHeading("Example JSON", 2);
        OutbreakComponentMessages exampleComponentMessage = new OutbreakComponentMessages(
                "message.outbreak.portal.spawn",
                List.of(ChatFormatting.GOLD),
                List.of(ChatFormatting.BOLD, ChatFormatting.UNDERLINE)
        );
        builder.addCodeBlock(encodeDataToJsonString(OutbreakComponentMessages.CODEC, exampleComponentMessage));

        builder.addParagraph("This example configures a message that appears in **gold, bold, and underlined** when an outbreak portal spawns.");

        return builder;
    }


    private void createWikiPage(String filename, WikiPageBuilder builder, BiConsumer<String, Supplier<String>> consumer){
        consumer.accept(filename, builder::getContent);
    }


}
