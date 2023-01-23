package titaninus.warofclans.commands.team;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.TeamColor;
import titaninus.warofclans.core.WOCTeam;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RemovePoints implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("removePointsFrom")
                        .requires(source -> source.hasPermissionLevel(4))
                            .then(literal("Red")
                                .then(argument("amount", IntegerArgumentType.integer(0, 10000000)).executes(
                                        ctx -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount"), TeamColor.Red)
                                )))
                        .then(literal("Green")
                                .then(argument("amount", IntegerArgumentType.integer(0, 10000000)).executes(
                                        ctx -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount"), TeamColor.Green)
                                )))
                        .then(literal("Blue")
                                .then(argument("amount", IntegerArgumentType.integer(0, 10000000)).executes(
                                        ctx -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount"), TeamColor.Blue)
                                )))
                        .then(literal("Yellow")
                                .then(argument("amount", IntegerArgumentType.integer(0, 10000000)).executes(
                                        ctx -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "amount"), TeamColor.Yellow)
                                )))
                );
    }

    private int execute(ServerCommandSource source, int amount, TeamColor color) {
        var team = WOCTeam.GetTeamByColor(color);
        if (team.CanWithdrawPoints(amount)) {
            team.WithdrawPoints(amount);
            source.sendMessage(Text.literal(String.format("Successfully withdraw %s points from team %s; Current available: %s", amount, color, team.AvailablePoints)));
        } else {
            source.sendMessage(Text.literal(String.format("Not Enough points (%s) in team %s; Current available: %s", amount, color, team.AvailablePoints)));
        }
        return 1;
    }
}
