package com.scouter.cobblemonoutbreaks.portal.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OutbreakPortalEntityTickData {
    public static final Codec<OutbreakPortalEntityTickData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("tick_count").forGetter(OutbreakPortalEntityTickData::getTickCount),
                    Codec.INT.fieldOf("ticks_active").forGetter(OutbreakPortalEntityTickData::getTicksActive)
            ).apply(instance, OutbreakPortalEntityTickData::new)
    );

    private int tickCount;
    private int ticksActive;

    public static OutbreakPortalEntityTickData DEFAULT = new OutbreakPortalEntityTickData(0,0);

    public OutbreakPortalEntityTickData(int tickCount, int ticksActive) {
        this.tickCount = tickCount;
        this.ticksActive = ticksActive;

    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }

    public void setTicksActive(int ticksActive) {
        this.ticksActive = ticksActive;
    }

    public int getTickCount() {
        return tickCount;
    }

    public int getTicksActive() {
        return ticksActive;
    }

    public void increaseTicksActive() {
        this.ticksActive++;
    }

    public void increaseTick() {
        this.tickCount++;
    }


    @Override
    public String toString() {
        return "OutbreakPortalEntityTickData{" +
                "tickCount=" + tickCount +
                ", ticksActive=" + ticksActive +
                '}';
    }
}
