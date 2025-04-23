package com.scouter.cobblemonoutbreaks.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.scouter.cobblemonoutbreaks.data.OutbreaksJsonDataManager;
import com.scouter.cobblemonoutbreaks.manager.OutbreakManager;
import com.scouter.cobblemonoutbreaks.manager.OutbreakPlayerManager;
import com.scouter.cobblemonoutbreaks.manager.OutbreakWorldManager;
import com.scouter.cobblemonoutbreaks.manager.PokemonOutbreakManager;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortal;
import com.scouter.cobblemonoutbreaks.portal.entity.OutbreakPortalEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OutbreakPortalCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_TYPE = (ctx, builder) -> {
        return SharedSuggestionProvider.suggest(OutbreaksJsonDataManager.getData().keySet().stream().map(ResourceLocation::toString), builder);
    };


    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("outbreakportal").requires(s -> s.hasPermission(2));

        builder.then(Commands.literal("flush_pokemon_map").executes(c -> {
            return flushPokemonMap(c);
        }));

        builder.then(Commands.literal("clear_outbreaks").executes(c -> {
            return clearOutbreaks(c);
        }));

        builder.then(Commands.literal("clear_player_timer").executes(c -> {
            return clearTimers(c);
        }));

        builder.then(Commands.literal("set_time_to_config_value").executes(c -> {
            return setToConfigValue(c);
        }));

        builder.then(Commands.literal("update_files").executes(c -> {
            return updateFiles(c);
        }));

        builder.then(Commands.literal("prevent_outbreak_spawns")
                .then(Commands.argument("width", IntegerArgumentType.integer(1, 50))
                        .then(Commands.argument("height", IntegerArgumentType.integer(1, 50))
                                .executes(OutbreakPortalCommand::preventOutbreakSpawns))));

        builder.then(Commands.literal("remove_outbreak_spawns_prevention")
                .then(Commands.argument("width", IntegerArgumentType.integer(1, 50))
                        .then(Commands.argument("height", IntegerArgumentType.integer(1, 50))
                                .executes(OutbreakPortalCommand::removeOutbreakSpawns))));


        builder.then(Commands.argument("pos", Vec3Argument.vec3()).then(Commands.argument("type", ResourceLocationArgument.id()).suggests(SUGGEST_TYPE).executes(c -> {
            return openOutBreakPortal(c, Vec3Argument.getVec3(c, "pos"), ResourceLocationArgument.getId(c, "type"));
        })));
        pDispatcher.register(builder);
    }

    public static int openOutBreakPortal(CommandContext<CommandSourceStack> c, Vec3 pos, ResourceLocation type) {
        try {
            Entity nullableSummoner = c.getSource().getEntity();
            Player summoner = nullableSummoner instanceof Player ? (Player) nullableSummoner : c.getSource().getLevel().getNearestPlayer(pos.x(), pos.y(), pos.z(), 64, false);
            BlockPos blockPos = BlockPos.containing(pos.x(), pos.y(), pos.z());
            OutbreakPortalEntity entity = new OutbreakPortalEntity(c.getSource().getLevel(), summoner, type,blockPos);
        } catch (Exception ex) {
            c.getSource().sendFailure(Component.literal("Exception thrown - see log"));
            ex.printStackTrace();
        }
        return 0;
    }

    public static int flushPokemonMap(CommandContext<CommandSourceStack> c) {
        try {
            ServerLevel level = c.getSource().getLevel();
            level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("cobblemonoutbreaks.clearing_pokemon_outbreaks_map").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
            PokemonOutbreakManager pokemonOutbreakManager = PokemonOutbreakManager.get(level);
            pokemonOutbreakManager.clearMap();
            pokemonOutbreakManager.clearTempMap();
        } catch (Exception ex) {
            c.getSource().sendFailure(Component.literal("Exception thrown - see log"));
            ex.printStackTrace();
        }
        return 0;
    }

    public static int clearOutbreaks(CommandContext<CommandSourceStack> c) {
        try {
            ServerLevel level = c.getSource().getLevel();
            level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("cobblemonoutbreaks.clearing_outbreaks_map").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
            OutbreakManager pokemonOutbreakManager = OutbreakManager.get(level);
            pokemonOutbreakManager.clearMap(level);
        } catch (Exception ex) {
            c.getSource().sendFailure(Component.literal("Exception thrown - see log"));
            ex.printStackTrace();
        }
        return 0;
    }

    public static int clearTimers(CommandContext<CommandSourceStack> c) {
        try {
            ServerLevel level = c.getSource().getLevel();
            level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("cobblemonoutbreaks.clear_player_timers").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
            OutbreakPlayerManager outbreakPlayerManager = OutbreakPlayerManager.get(level);
            outbreakPlayerManager.clearTimeLeft();
        } catch (Exception ex) {
            c.getSource().sendFailure(Component.literal("Exception thrown - see log"));
            ex.printStackTrace();
        }
        return 0;
    }

    public static int setToConfigValue(CommandContext<CommandSourceStack> c) {
        try {
            ServerLevel level = c.getSource().getLevel();
            level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("cobblemonoutbreaks.set_to_config_value").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC), true);
            OutbreakPlayerManager outbreakPlayerManager = OutbreakPlayerManager.get(level);
            outbreakPlayerManager.setTimeLeftToNewConfig();

            OutbreakWorldManager outbreakWorldManager = OutbreakWorldManager.get(level);
            outbreakWorldManager.setTimeLeftToNewConfig();


        } catch (Exception ex) {
            c.getSource().sendFailure(Component.literal("Exception thrown - see log"));
            ex.printStackTrace();
        }
        return 0;
    }

    public static int updateFiles(CommandContext<CommandSourceStack> c) {
        AtomicInteger updatedFile = new AtomicInteger();
        Entity nullableSummoner = c.getSource().getEntity();
        Path PATH = FMLPaths.GAMEDIR.get().resolve("cobblemon_outbreaks_updated_json_files");
        try {
            Map<ResourceLocation, OutbreakPortal> portals = OutbreaksJsonDataManager.getData();
//
            try {
                Files.createDirectories(PATH); // Create the directory if it doesn't exist
            } catch (IOException e) {
                LOGGER.error("Error creating directory: ", e);
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            for(Map.Entry<ResourceLocation, OutbreakPortal> portalEntry : portals.entrySet()) {
                if(portalEntry.getValue().isOld()){
               OutbreakPortal.CODEC.encodeStart(JsonOps.INSTANCE, portalEntry.getValue())
                       .ifSuccess(element -> {
                           try (FileWriter writer = new FileWriter(PATH + "/" + portalEntry.getKey().getPath() + ".json")) {
                               gson.toJson(element, writer);
                               updatedFile.addAndGet(1);
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }).ifError(jsonElementError -> LOGGER.error("Failed to update file {} due to {}",portalEntry.getKey(),jsonElementError.error()));
//

            }
            }
        } catch (Exception ex) {
            c.getSource().sendFailure(Component.literal("Exception thrown - see log"));
            ex.printStackTrace();
        }
        if(nullableSummoner instanceof Player player){
            player.sendSystemMessage(Component.literal("A new directory has been created at: " + PATH).withStyle(ChatFormatting.GREEN));
            player.sendSystemMessage(Component.literal("Updated " + updatedFile + " files").withStyle(ChatFormatting.GREEN));
//
        }
        return 0;
    }

    public static int preventOutbreakSpawns(CommandContext<CommandSourceStack> c) {
        try {
            ServerLevel level = c.getSource().getLevel();
            Entity entity = c.getSource().getEntity();

            // Ensure the command is run by a player
            if (!(entity instanceof Player player)) {
                c.getSource().sendFailure(Component.literal("This command must be run by a player."));
                return 0;
            }

            // Get player's current position
            BlockPos bottomLeft = player.blockPosition();
            int width = IntegerArgumentType.getInteger(c, "width");
            int height = IntegerArgumentType.getInteger(c, "height");

            OutbreakWorldManager worldManager = OutbreakWorldManager.get(level);
            int addedChunks = addChunksToPreventionList(worldManager, bottomLeft, width, height);

            // Inform the player
            c.getSource().sendSuccess(() ->Component.literal("Added " + addedChunks + " chunks to the outbreak prevention list."), true);
        } catch (Exception ex) {
            c.getSource().sendFailure(Component.literal("Exception thrown - see log"));
            ex.printStackTrace();
        }
        return 0;
    }


    public static int removeOutbreakSpawns(CommandContext<CommandSourceStack> c) {
        try {
            ServerLevel level = c.getSource().getLevel();
            Entity entity = c.getSource().getEntity();

            // Ensure command is run by a player
            if (!(entity instanceof Player player)) {
                c.getSource().sendFailure(Component.literal("This command must be run by a player."));
                return 0;
            }

            // Get player's current position
            BlockPos bottomLeft = player.blockPosition();
            int width = IntegerArgumentType.getInteger(c, "width");
            int height = IntegerArgumentType.getInteger(c, "height");

            // Get outbreak manager and remove chunks
            OutbreakWorldManager worldManager = OutbreakWorldManager.get(level);
            int removedChunks = removeChunksFromPreventionList(worldManager, bottomLeft, width, height);

            // Inform the player
            c.getSource().sendSuccess(() -> Component.literal("Removed " + removedChunks + " chunks from the outbreak prevention list."), true);
        } catch (Exception ex) {
            c.getSource().sendFailure(Component.literal("Exception thrown - see log"));
            ex.printStackTrace();
        }
        return 0;
    }


    /**
     * Adds all chunks in a given XxX area to the OutbreakWorldManager prevention list.
     */
    private static int addChunksToPreventionList(OutbreakWorldManager worldManager, BlockPos bottomLeft, int width, int height) {
        int addedChunks = 0;

        // Iterate through the area and add all chunks
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                ChunkPos chunkPos = new ChunkPos((bottomLeft.getX() >> 4) + x, (bottomLeft.getZ() >> 4) + z);
                if (!worldManager.containsChunk(chunkPos)) {

                    worldManager.addChunkToList(chunkPos);
                    addedChunks++;
                }
            }
        }
        return addedChunks;
    }

    private static int removeChunksFromPreventionList(OutbreakWorldManager worldManager, BlockPos bottomLeft, int width, int height) {
        int removedChunks = 0;

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                ChunkPos chunkPos = new ChunkPos((bottomLeft.getX() >> 4) + x, (bottomLeft.getZ() >> 4) + z);
                if (worldManager.containsChunk(chunkPos)) {
                    worldManager.removeChunkFromList(chunkPos);
                    removedChunks++;
                }
            }
        }
        return removedChunks;
    }

}
