package com.scouter.cobblemonoutbreaks.portal;

import com.cobblemon.mod.common.api.entity.Despawner;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.scouter.cobblemonoutbreaks.manager.PokemonOutbreakManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CustomDespawner implements Despawner<PokemonEntity> {

    private int maxAgeTicks = 20 * 60 * 3;

    @Override
    public boolean shouldDespawn(@NotNull PokemonEntity entity) {
        Level level = entity.level();
        if(!level.isClientSide && entity instanceof PokemonEntity pokemon){
            PokemonOutbreakManager outbreakManager = PokemonOutbreakManager.get(level);
            UUID uuid = pokemon.getPokemon().getUuid();
            boolean containsPokenon = outbreakManager.containsUUID(uuid) || outbreakManager.containsUUIDTemp(uuid);
            int age = pokemon.getTicksLived();
            boolean isMinAge = age > maxAgeTicks;
            return !containsPokenon && isMinAge;
        }
        return false;
    }

    @Override
    public void beginTracking(@NotNull PokemonEntity entity) {
    }
}
