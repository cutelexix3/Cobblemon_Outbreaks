package com.scouter.cobblemonoutbreaks;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.logging.LogUtils;
import com.scouter.cobblemonoutbreaks.command.OutbreakPortalCommand;
import com.scouter.cobblemonoutbreaks.event.CobblemonOutbreaksEvent;
import com.scouter.cobblemonoutbreaks.manager.OutbreakManager;
import com.scouter.cobblemonoutbreaks.manager.PokemonOutbreakManager;
import com.scouter.cobblemonoutbreaks.portal.entity.OutbreakPortalEntity;
import com.scouter.cobblemonoutbreaks.setup.ModSetup;
import com.scouter.cobblemonoutbreaks.setup.Registration;
import kotlin.Unit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.UUID;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CobblemonOutbreaks.MODID)
public class CobblemonOutbreaks
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cobblemonoutbreaks";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ServerLevel serverlevel;
    public static boolean serverStarted = false;

    public CobblemonOutbreaks(IEventBus modEventBus, ModContainer modContainer)
    {

        // Register the commonSetup method for modloading
        Registration.init();

        ModSetup.setup();
        NeoForge.EVENT_BUS.addListener(this::commands);
        modEventBus.addListener(ModSetup::init);
        CobblemonOutbreaks.pokemonCapture();
        CobblemonOutbreaks.pokemonKO();

    }
    public static ResourceLocation prefix(String name) {
        return  ResourceLocation.fromNamespaceAndPath(MODID, name.toLowerCase(Locale.ROOT));
    }


    public void commands(RegisterCommandsEvent e) {
        OutbreakPortalCommand.register(e.getDispatcher());
    }


    /**
     * Subscribes to the POKEMON_CAPTURED event and performs actions when a Pokémon is captured.
     * Checks if the captured Pokémon UUID is present in the outbreak manager's map.
     * If present, retrieves the owner UUID and removes the Pokémon from the set in the outbreak portal entity.
     * Finally, removes the Pokémon UUID from the outbreak manager.
     */
    public static void pokemonCapture() {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.HIGH, event -> {
            if (!(event.getPlayer().level() instanceof ServerLevel serverLevel)) return Unit.INSTANCE;
            PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);
            Pokemon pokemon = event.getPokemon();
            UUID pokemonUUID = event.getPokemon().getUuid();
            //UUID entityUUID = event.getPokemon().getEntity().getUUID();
            if (!outbreakManager.containsUUID(pokemonUUID)) return Unit.INSTANCE;
            UUID ownerUUID = outbreakManager.getOwnerUUID(pokemonUUID);
            ServerPlayer player = event.getPlayer();
            OutbreakManager outbreakManager1 = OutbreakManager.get(serverLevel);
            OutbreakPortalEntity outbreakPortal = outbreakManager1.getOutbreakEntity(ownerUUID);

            CobblemonOutbreaksEvent.OutbreakPokemonCapture pokemonCapture = new CobblemonOutbreaksEvent.OutbreakPokemonCapture(serverLevel,player , outbreakPortal, pokemon);
            NeoForge.EVENT_BUS.post(pokemonCapture);
            if (outbreakPortal != null) {
                outbreakPortal.removeFromSet(pokemonUUID);
            }
            outbreakManager.removePokemonUUID(pokemonUUID);
            //LOGGER.info("This one was from a portal and captured!");
            return Unit.INSTANCE;
        });
    }

    /**
     * Subscribes to the POKEMON_FAINTED event and performs actions when a Pokémon faints.
     * Checks if the fainted Pokémon UUID is present in the outbreak manager's map.
     * If present, retrieves the owner UUID and removes the Pokémon from the set in the outbreak portal entity.
     * Finally, removes the Pokémon UUID from the outbreak manager.
     */
    public static void pokemonKO() {
        CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.HIGH, event -> {
            if (event.getPokemon().getOwnerUUID() != null || event.getPokemon() == null || serverlevel == null) return Unit.INSTANCE;
            ServerLevel serverLevel = serverlevel;
            Pokemon pokemon = event.getPokemon();
            PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(serverLevel);

            UUID pokemonUUID = event.getPokemon().getUuid();
            //UUID entityUUID = event.getPokemon().getEntity().getUUID();
            if (!outbreakManager.containsUUID(pokemonUUID)) return Unit.INSTANCE;
            UUID ownerUUID = outbreakManager.getOwnerUUID(pokemonUUID);
            OutbreakManager outbreakManager1 = OutbreakManager.get(serverLevel);
            OutbreakPortalEntity outbreakPortal = outbreakManager1.getOutbreakEntity(ownerUUID);
            CobblemonOutbreaksEvent.OutbreakPokemonKilled pokemonKO = new CobblemonOutbreaksEvent.OutbreakPokemonKilled(serverLevel , outbreakPortal, pokemon);
            NeoForge.EVENT_BUS.post(pokemonKO);
            if (outbreakPortal != null) {
                outbreakPortal.removeFromSet(pokemonUUID);
            }
            outbreakManager.removePokemonUUID(pokemonUUID);
            //LOGGER.info("This one fainted!");
            return Unit.INSTANCE;
        });
    }
}
