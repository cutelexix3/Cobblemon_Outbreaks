package com.scouter.cobblemonoutbreaks.portal.old;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.List;

public class OutbreakRewardsOld {
    public static OutbreakRewardsOld DEFAULT = new OutbreakRewardsOld(Collections.emptyList(), 0);

    public static Codec<OutbreakRewardsOld> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    BuiltInRegistries.ITEM.byNameCodec().listOf().optionalFieldOf("item_rewards", Collections.emptyList()).forGetter(i -> i.itemRewards),
                    Codec.INT.optionalFieldOf("experience_reward", 0).forGetter(e -> e.experienceReward)
            )
            .apply(inst, OutbreakRewardsOld::new)
    );
    private List<Item> itemRewards;
    private int experienceReward;
    public OutbreakRewardsOld(List<Item> itemRewards, int experienceReward){
        this.itemRewards = itemRewards;
        this.experienceReward = experienceReward;
    }

    public List<Item> getRewards() {
        return itemRewards;
    }

    public int getExperience() {
        return experienceReward;
    }

    public static OutbreakRewardsOld getDefaultRewards(){
        return new OutbreakRewardsOld(Collections.emptyList(), 0);
    }
}
