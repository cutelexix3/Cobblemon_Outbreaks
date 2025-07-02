package com.scouter.cobblemonoutbreaks.lang;

public enum OutbreakTranslatables {


    UNLUCKY_SPAWN("cobblemonoutbreaks.unlucky_spawn"),
    PORTAL_BIOME_SPECIFIC_SPAWN_DEBUG("cobblemonoutbreaks.portal_biome_specific_spawn_debug"),
    PORTAL_SPAWN_NEAR_BLOCKPOS("cobblemonoutbreaks.portal_spawn_near_blockpos"),
    PORTAL_SPAWN_NEAR("cobblemonoutbreaks.portal_spawn_near"),
    GATE_FAILED_SPAWNING("cobblemonoutbreaks.gate_failed_spawning"),
    GATE_FINISHED("cobblemonoutbreaks.gate_finished"),
    GATE_TIME_FINISHED("cobblemonoutbreaks.gate_time_finished"),
    GATE_NOT_ABLE_TO_LOAD("cobblemonoutbreaks.gate_not_able_to_load"),
    ;

    private final String name;

    OutbreakTranslatables(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
