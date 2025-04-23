package com.scouter.cobblemonoutbreaks.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public class ItemStackRewards {
    public static final ItemStackRewards DEFAULT = new ItemStackRewards(ItemStack.EMPTY, 0);

    public static Codec<ItemStackRewards> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    ItemStack.CODEC.fieldOf("item_reward").forGetter(ItemStackRewards::getItemReward),
                    Codec.FLOAT.fieldOf("chance").forGetter(ItemStackRewards::getChance)
            )
            .apply(inst, ItemStackRewards::new)
    );
    private final ItemStack itemReward;
    private final float chance;
    public ItemStackRewards(ItemStack itemReward, float chance){
        this.itemReward = itemReward;
        this.chance = chance;
    }

    public ItemStack getItemReward() {
        return itemReward;
    }

    public float getChance() {
        return chance;
    }


}
