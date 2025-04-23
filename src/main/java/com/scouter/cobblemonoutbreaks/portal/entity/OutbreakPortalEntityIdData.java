package com.scouter.cobblemonoutbreaks.portal.entity;

import com.google.common.collect.ConcurrentHashMultiset;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class OutbreakPortalEntityIdData {

    public static final Codec<OutbreakPortalEntityIdData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.fieldOf("owner_uuid").forGetter(OutbreakPortalEntityIdData::getOwnerUUID),
                    UUIDUtil.CODEC.fieldOf("outbreak_uuid").forGetter(OutbreakPortalEntityIdData::getOutbreakUUID),
                    UUIDUtil.CODEC.listOf().fieldOf("current_entity_uuids")
                            .xmap(ConcurrentHashMultiset::create, List::copyOf)
                            .forGetter(OutbreakPortalEntityIdData::getCurrentOutbreakWaveEntities),
                    UUIDUtil.CODEC.listOf().fieldOf("total_entity_uuids")
                            .xmap(ConcurrentHashMultiset::create, List::copyOf)
                            .forGetter(OutbreakPortalEntityIdData::getTotalOutbreakEntityIds)
            ).apply(instance, OutbreakPortalEntityIdData::new)
    );

    private UUID ownerUUID;
    private UUID outbreakUUID;
    private ConcurrentHashMultiset<UUID> currentOutbreakWaveEntities;
    private ConcurrentHashMultiset<UUID> totalOutbreakEntityIds;

    public OutbreakPortalEntityIdData(UUID ownerUUID, UUID outbreakUUID) {
        this.ownerUUID = ownerUUID;
        this.outbreakUUID = outbreakUUID;
        this.currentOutbreakWaveEntities = ConcurrentHashMultiset.create();
        this.totalOutbreakEntityIds = ConcurrentHashMultiset.create();
    }

    public OutbreakPortalEntityIdData(UUID ownerUUID, UUID outbreakUUID, ConcurrentHashMultiset<UUID> currentOutbreakWaveEntities, ConcurrentHashMultiset<UUID> totalOutbreakEntityIds) {
        this.ownerUUID = ownerUUID;
        this.outbreakUUID = outbreakUUID;
        this.currentOutbreakWaveEntities = currentOutbreakWaveEntities;
        this.totalOutbreakEntityIds = totalOutbreakEntityIds;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public UUID getOutbreakUUID() {
        return outbreakUUID;
    }

    public ConcurrentHashMultiset<UUID> getCurrentOutbreakWaveEntities() {
        return currentOutbreakWaveEntities;
    }

    public ConcurrentHashMultiset<UUID> getTotalOutbreakEntityIds() {
        return totalOutbreakEntityIds;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public void setOutbreakUUID(UUID outbreakUUID) {
        this.outbreakUUID = outbreakUUID;
    }

    public void setCurrentOutbreakWaveEntities(ConcurrentHashMultiset<UUID> currentOutbreakWaveEntities) {
        this.currentOutbreakWaveEntities = currentOutbreakWaveEntities;
    }

    public void setTotalOutbreakEntityIds(ConcurrentHashMultiset<UUID> totalOutbreakEntityIds) {
        this.totalOutbreakEntityIds = totalOutbreakEntityIds;
    }

    @Override
    public String toString() {
        return "OutbreakPortalEntityIdData{" +
                "ownerUUID=" + ownerUUID +
                ", outbreakUUID=" + outbreakUUID +
                ", currentOutbreakWaveEntities=" + currentOutbreakWaveEntities +
                ", totalOutbreakEntityIds=" + totalOutbreakEntityIds +
                '}';
    }
}
