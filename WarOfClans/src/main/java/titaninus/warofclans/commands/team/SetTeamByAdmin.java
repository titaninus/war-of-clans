package titaninus.warofclans.commands.team;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.TeamColor;
import titaninus.warofclans.core.WOCTeam;
import titaninus.warofclans.gamelogic.GameMaster;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class SetTeamByAdmin implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("setTeamByAdmin")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(argument("players", EntityArgumentType.players())
                            .then(literal("Red").executes(ctx -> execute(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "players"), TeamColor.Red)))
                            .then(literal("Green").executes(ctx -> execute(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "players"), TeamColor.Green)))
                            .then(literal("Yellow").executes(ctx -> execute(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "players"), TeamColor.Yellow)))
                            .then(literal("Blue").executes(ctx -> execute(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "players"), TeamColor.Blue)))));
    }

    private int execute(ServerCommandSource source, Collection<ServerPlayerEntity> players, TeamColor color) {
        for (var p: players) {
            var result = WOCTeam.GetTeamByColor(color).AddPlayer(p);
            if (result) {
                source.sendMessage(Text.literal(String.format("Successfully add player %s to team %s", p.getName(), color)));
            } else {
                source.sendMessage(Text.literal(String.format("Too many players in team %s. %s was not added to the requested team", color, p.getName())));
            }
        }
        return 1;
    }

}
