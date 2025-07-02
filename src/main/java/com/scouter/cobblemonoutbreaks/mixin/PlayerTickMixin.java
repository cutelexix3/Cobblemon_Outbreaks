package com.scouter.cobblemonoutbreaks.mixin;

import com.scouter.cobblemonoutbreaks.config.OutbreakConfigManager;
import com.scouter.cobblemonoutbreaks.manager.OutbreakPlayerManager;
import com.scouter.cobblemonoutbreaks.util.PortalUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Player.class)
public class PlayerTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void outbreakPortal$playerTick(CallbackInfo ci) {

        Player player = (Player) (Object) this;
        Level level = player.level();
        if (!OutbreakConfigManager.getConfig().getSpawningConfig().isPerPlayer() ||
                !(player instanceof ServerPlayer serverPlayer) ||
                serverPlayer.level().isClientSide)
            return;
        ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
        OutbreakPlayerManager playerManager = OutbreakPlayerManager.get(serverLevel);
        UUID playerUUID = serverPlayer.getUUID();

        if (!playerManager.containsUUID(playerUUID)) {
            playerManager.setTimeLeft(playerUUID, OutbreakConfigManager.getConfig().getSpawningConfig().getPerPlayerOutbreakTimer());
        }
        int timeLeft = playerManager.getTimeLeft(playerUUID);
        if (timeLeft-- > 0) {
            playerManager.setTimeLeft(playerUUID, timeLeft);
            return;
        }
        PortalUtils.processOutbreaks(serverPlayer, OutbreakConfigManager.getConfig().getSpawningConfig().getOutbreakSpawnCount());

        playerManager.setTimeLeft(playerUUID, OutbreakConfigManager.getConfig().getSpawningConfig().getPerPlayerOutbreakTimer());
    }
}

