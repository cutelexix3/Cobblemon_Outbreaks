package com.scouter.cobblemonoutbreaks.datagen;

import com.scouter.cobblemonoutbreaks.portal.OutbreakPortal;
import net.minecraft.resources.ResourceLocation;

public class OutbreakPortalConsumer {

    private ResourceLocation location;
    private OutbreakPortal portal;

    public OutbreakPortalConsumer(ResourceLocation loc, OutbreakPortal portal) {
        this.location = loc;
        this.portal = portal;
    }

    // Getter for location
    public ResourceLocation getLocation() {
        return location;
    }

    // Setter for location
    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    // Getter for puppetData
    public OutbreakPortal getPortal() {
        return portal;
    }

}
