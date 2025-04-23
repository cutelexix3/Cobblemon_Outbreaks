package com.scouter.cobblemonoutbreaks.datagen;

import com.google.common.collect.Sets;
import com.mojang.serialization.JsonOps;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortal;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.scouter.cobblemonoutbreaks.CobblemonOutbreaks.prefix;

public abstract class OutbreakPortalProvider implements DataProvider {
    protected final PackOutput.PathProvider puppetPathProvider;

    public OutbreakPortalProvider(PackOutput pOutput) {
        this.puppetPathProvider = pOutput.createPathProvider(PackOutput.Target.DATA_PACK, prefix("outbreaks").getPath());
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        Set<ResourceLocation> set = Sets.newHashSet();
        Set<ResourceLocation> taskSet = Sets.newHashSet();
        List<CompletableFuture<?>> list = new ArrayList<>();
        this.buildPortal((puppet -> {
            if (!set.add(puppet.getLocation())) {
                throw new IllegalStateException("Duplicate portal " + puppet.getLocation());
            } else {

                OutbreakPortal.CODEC.encodeStart(JsonOps.INSTANCE, puppet.getPortal())
                        .ifError(partial -> LOGGER.error("Failed to create outbreak portal {}, due to {}", puppet.getLocation(), partial))
                        .ifSuccess(e -> list.add(DataProvider.saveStable(pOutput, e, this.puppetPathProvider.json(puppet.getLocation()))));
            }
        }));
        return CompletableFuture.allOf(list.toArray((p_253414_) -> {
            return new CompletableFuture[p_253414_];
        }));
    }


    protected abstract void buildPortal(Consumer<OutbreakPortalConsumer> pWriter);

    @Override
    public String getName() {
        return "outbreak portal";
    }
}
