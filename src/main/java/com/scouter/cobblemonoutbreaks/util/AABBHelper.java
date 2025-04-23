package com.scouter.cobblemonoutbreaks.util;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.phys.AABB;

public class AABBHelper {
    public static AABB getAABB(double pX, double pY, double pZ, PokemonEntity pokemon) {
        float f = pokemon.getBbWidth() / 2.0F;
        return new AABB(pX - (double)f, pY, pZ - (double)f, pX + (double)f, pY + (double)pokemon.getBbHeight(), pZ + (double)f);
    }
}
