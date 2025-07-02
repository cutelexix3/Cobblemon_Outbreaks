package com.scouter.cobblemonoutbreaks.lang;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class OutbreakComponentMessages {


    public static Codec<OutbreakComponentMessages> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                    Codec.STRING.fieldOf("translatable").forGetter(OutbreakComponentMessages::getTranslatable),
                    ChatFormatting.CODEC.listOf().fieldOf("message_formatting").forGetter(OutbreakComponentMessages::getMessageFormat),
                    ChatFormatting.CODEC.listOf().fieldOf("args_formatting").forGetter(OutbreakComponentMessages::getArgsFormat)
            )
            .apply(inst, OutbreakComponentMessages::new)
    );

    private final String translatable;
    private final List<ChatFormatting> messageFormat;
    private final List<ChatFormatting> argsFormat;

    public OutbreakComponentMessages(String translatable, List<ChatFormatting> messageFormat, List<ChatFormatting> argsFormat) {
        this.translatable = translatable;
        this.messageFormat = messageFormat;
        this.argsFormat = argsFormat;
    }




    /**
     * Sends a translatable message to a given player using multiple arguments.
     * Each argument is formatted with the configured argument formatting.
     * The final message is then formatted with the overall message formatting.
     *
     * @param player The player to send the message to.
     * @param args   One or more MutableComponent arguments to pass to the translation.
     */
    public void sendPlayerMessage(Player player, MutableComponent... args) {
        if (player == null) return;

        // Format each argument with the configured argument styles.
        Object[] formattedArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            MutableComponent arg = args[i];
            for (ChatFormatting fmt : argsFormat) {
                arg = arg.withStyle(fmt);
            }
            formattedArgs[i] = arg;
        }

        // Create the translatable message using all formatted arguments.
        MutableComponent message = Component.translatable(translatable, formattedArgs);
        // Apply the overall message formatting.
        for (ChatFormatting fmt : messageFormat) {
            message = message.withStyle(fmt);
        }
        player.sendSystemMessage(message);
    }

    // Convenience overloads

    public void sendPlayerMessage(@Nullable Level level, @Nullable UUID uuid, MutableComponent... args) {
        if (level == null || uuid == null) return;
        Player player = level.getPlayerByUUID(uuid);
        if (player != null) {
            sendPlayerMessage(player, args);
        }
    }

    public void sendPlayerMessage(@Nullable Level level, @Nullable UUID uuid, String arg) {
        sendPlayerMessage(level, uuid, Component.literal(arg));
    }


    public List<ChatFormatting> getArgsFormat() {
        return argsFormat;
    }

    public List<ChatFormatting> getMessageFormat() {
        return messageFormat;
    }

    public String getTranslatable() {
        return translatable;
    }
}
