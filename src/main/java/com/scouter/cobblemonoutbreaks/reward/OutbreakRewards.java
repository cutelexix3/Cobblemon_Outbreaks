package com.scouter.cobblemonoutbreaks.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OutbreakRewards {
    public static final OutbreakRewards DEFAULT = new OutbreakRewards(Collections.emptyList(), 0);
    public static final OutbreakRewards WITH_STACK = new OutbreakRewards(
            List.of(new ItemStackRewards(new ItemStack(Items.DIAMOND, 5), 0.5F)),
            0
    );
;

    public static Codec<OutbreakRewards> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ItemStackRewards.CODEC.listOf().fieldOf("item_rewards").forGetter(OutbreakRewards::getRewards),
                    Codec.INT.fieldOf("experience_reward").forGetter(OutbreakRewards::getExperience)
            )
            .apply(inst, OutbreakRewards::new)
    );
    private final List<ItemStackRewards> itemRewards;
    private final int experienceReward;
    public OutbreakRewards(List<ItemStackRewards> itemRewards, int experienceReward){
        this.itemRewards = itemRewards;
        this.experienceReward = experienceReward;
    }

    public List<ItemStackRewards> getRewards() {
        return itemRewards;
    }

    public int getExperience() {
        return experienceReward;
    }

    public double spawnExperienceRewards(ServerLevel level, BlockPos pos) {
        double completionXp = getExperience();
        while (completionXp > 0) {
            int i = 20;
            completionXp -= i;
            level.addFreshEntity(new ExperienceOrb(level, pos.getX(), pos.getY(), pos.getZ(), i));
        }
        return completionXp;
    }

    public List<ItemStack> spawnItemRewards(ServerLevel level, BlockPos pos) {
        List<ItemStack> stacks = new ArrayList<>();
        List<ItemStackRewards> rewards = getRewards();
        for(ItemStackRewards stackRewards : rewards) {
            float randomNr = level.getRandom().nextFloat();
            if(randomNr <= stackRewards.getChance()) {
                stacks.add(stackRewards.getItemReward());
            }
        }

        stacks.forEach(s -> level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), s)));

        return stacks;
    }

}
