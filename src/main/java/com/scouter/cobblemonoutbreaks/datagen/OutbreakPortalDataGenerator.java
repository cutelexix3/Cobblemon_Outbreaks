package com.scouter.cobblemonoutbreaks.datagen;

import com.scouter.cobblemonoutbreaks.data.*;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortal;
import com.scouter.cobblemonoutbreaks.portal.OutbreakPortalSpawnSettings;
import com.scouter.cobblemonoutbreaks.portal.OutbreakSpecies;
import com.scouter.cobblemonoutbreaks.portal.PokemonRarity;
import com.scouter.cobblemonoutbreaks.reward.OutbreakRewards;
import net.minecraft.data.PackOutput;

import java.util.function.Consumer;

import static com.scouter.cobblemonoutbreaks.CobblemonOutbreaks.prefix;

public class OutbreakPortalDataGenerator extends OutbreakPortalProvider{
    public OutbreakPortalDataGenerator(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildPortal(Consumer<OutbreakPortalConsumer> pWriter) {


        OutbreakPortal portal = new OutbreakPortal(
                new OutbreakSpecies("pikachu",
                        new OutbreakWaveData(3, 6),
                        SpeciesShinyData.DEFAULT,
                        PokemonRarity.COMMON
                        ),
                OutbreakRewards.WITH_STACK,
                OutbreakAlgorithmsData.DEFAULT,
                OutbreakPortalSpawnSettings.DEFAULT,
                OutbreakSoundsData.DEFAULT,
                OutbreakMessageData.DEFAULT,
                36000
        );

        pWriter.accept(new OutbreakPortalConsumer(prefix("pikachu"), portal));
    }
}
