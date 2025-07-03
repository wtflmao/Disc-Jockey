package semmiedev.disc_jockey;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ServerDiscJockeyCommand {
    private static final SimpleCommandExceptionType PLAYER_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Target player not found or is not online."));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("dj")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("allowextra")
                        .then(argument("player", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                    if (player == null) throw PLAYER_NOT_FOUND_EXCEPTION.create();

                                    if (Permissions.ALLOWED_PLAYERS.add(player.getUuid())) {
                                        Permissions.save();
                                        context.getSource().sendFeedback(() -> Text.literal("Enabled advanced playback for ").append(player.getDisplayName()), true);
                                    } else {
                                        context.getSource().sendFeedback(() -> Text.literal("Advanced playback was already enabled for ").append(player.getDisplayName()), false);
                                    }
                                    return 1;
                                })
                        )
                )
                .then(literal("disallowextra")
                        .then(argument("player", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                    if (player == null) throw PLAYER_NOT_FOUND_EXCEPTION.create();

                                    if (Permissions.ALLOWED_PLAYERS.remove(player.getUuid())) {
                                        Permissions.save();
                                        context.getSource().sendFeedback(() -> Text.literal("Disabled advanced playback for ").append(player.getDisplayName()), true);
                                    } else {
                                        context.getSource().sendFeedback(() -> Text.literal("Advanced playback was not enabled for ").append(player.getDisplayName()), false);
                                    }
                                    return 1;
                                })
                        )
                );

        dispatcher.register(command);
    }
} 