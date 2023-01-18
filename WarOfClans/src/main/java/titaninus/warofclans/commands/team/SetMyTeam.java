package titaninus.warofclans.commands.team;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import titaninus.warofclans.WarOfClans;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.TeamColor;
import titaninus.warofclans.core.WOCTeam;
import titaninus.warofclans.gamelogic.GameMaster;
import titaninus.warofclans.server.WarOfClansServer;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetMyTeam implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("setMyTeam")
                        .requires(ctx -> WarOfClansServer.WOC_CONFIG.AllowChangeTeamAtStage0())
                        .then(literal("Red").executes(ctx -> execute(ctx.getSource(), TeamColor.Red)))
                        .then(literal("Green").executes(ctx -> execute(ctx.getSource(), TeamColor.Green)))
                        .then(literal("Yellow").executes(ctx -> execute(ctx.getSource(), TeamColor.Yellow)))
                        .then(literal("Blue").executes(ctx -> execute(ctx.getSource(), TeamColor.Blue)))
                        .then(literal("None").executes(ctx -> execute(ctx.getSource(), null))));
    }

    private int execute(ServerCommandSource source, @Nullable TeamColor color) {
        var player = source.getPlayer();
        if (!WarOfClansServer.WOC_CONFIG.AllowChangeTeamAtStage0()) {
            source.sendMessage(Text.literal("You cannot change team by game rules"));
            return 1;
        }
        if (GameMaster.Instance().GetCurrentStage() > 0) {
            source.sendMessage(Text.literal("You cannot change team when game is started"));
            return 1;
        }

        if (color == null) {
            var team = WOCTeam.FindPlayerTeam(player);
            if (team == null) {

                source.sendMessage(Text.literal("You dont have a team to leave"));
                return 1;
            } else {
                team.RemovePlayer(player);
                source.sendMessage(Text.literal(String.format("Successfully remove player %s from team %s", player.getName(), team.Color)));
                return 1;
            }
        }
        var result = WOCTeam.GetTeamByColor(color).AddPlayer(player);
        if (result) {
            source.sendMessage(Text.literal(String.format("Successfully add player %s to team %s", player.getName(), color)));
        } else {
            source.sendMessage(Text.literal(String.format("Too many players in team %s. %s was not added to the requested team", color, player.getName())));
        }

        return 1;
    }

}
