package titaninus.warofclans.commands.territory;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.*;
import titaninus.warofclans.gamelogic.GameMaster;
import titaninus.warofclans.server.WarOfClansServer;

import static net.minecraft.server.command.CommandManager.literal;

public class BuyTerritory implements WOCCommandManager.RegistrableCommand {
    private enum SpawnOnTerritoryMode {
        Start,
        Middle,
        End,
        Spawn
    }

    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("buyTerritory")
                        .then(CommandManager.argument("location", Vec3ArgumentType.vec3()).executes((ctx) -> execute(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"))))
                        .then(CommandManager.argument("id", IntegerArgumentType.integer(0, WarOfClansServer.WOC_CONFIG.MapWidth() * WarOfClansServer.WOC_CONFIG.MapHeight() - 1)).executes((ctx) -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"))))
                        .then(literal("None").executes((ctx) -> execute(ctx.getSource()))));
    }

    private int execute(ServerCommandSource source, int id) {
        var player = source.getPlayer();
        var team = WOCTeam.FindPlayerTeam(player);
        if (team == null) {
            source.sendMessage(Text.literal(String.format("Player %s not participated in any of team", player.getName().getString())));
            return 1;

        }
        var territory = WOCMap.Instance().Territories.get(id);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided id doesn't belongs to any of territory (%s)", id)));
            return 1;
        }
        if (territory.IsBased) {
            source.sendMessage(Text.literal(String.format("Can't buy base territory (%s)", id)));
            return 1;
        }

        return TryToBuyTerritory(source, team, territory);
    }

    private int execute(ServerCommandSource source) {
        var player = source.getPlayer();
        var team = WOCTeam.FindPlayerTeam(player);
        if (team == null) {
            source.sendMessage(Text.literal(String.format("Player %s not participated in any of team", player.getName().getString())));
            return 1;

        }
        var loc = player.getBlockPos();
        var territory = WOCMap.Instance().GetTerritoryByPos(loc);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided position doesn't exists in any of territory (%s; %s; %s)", loc.getX(), loc.getY(), loc.getZ())));
            return 1;
        }
        if (territory.IsBased) {
            source.sendMessage(Text.literal(String.format("Can't set owner for base territory (%s) (%s; %s; %s)", territory.Id, loc.getX(), loc.getY(), loc.getZ())));
            return 1;
        }

        return TryToBuyTerritory(source, team, territory);
    }

    private int TryToBuyTerritory(ServerCommandSource source, WOCTeam team, Territory territory) {
        var price = territory.ActualPrice();
        if (!team.CanWithdrawPoints(price)) {
            source.sendMessage(Text.literal(String.format("Not enough points (have %s need %s) to buy territory (%s) for %s", team.AvailablePoints, price, territory, team.Color)));
            return 1;
        }
        if (GameMaster.Instance().BuyTerritory(team, territory)) {
            source.sendMessage(Text.literal(String.format("Succesfully bought territory (%s) to %s", territory, team.Color)));
        } else {
            source.sendMessage(Text.literal(String.format("Can't buy (%s) to %s because it's not a neighbour for any team territory", territory, team.Color)));
        }
        return 0;
    }

    private int execute(ServerCommandSource source, PosArgument location) {
        var player = source.getPlayer();
        var team = WOCTeam.FindPlayerTeam(player);
        if (team == null) {
            source.sendMessage(Text.literal(String.format("Player %s not participated in any of team", player.getName().getString())));
            return 1;

        }
        var absLoc = location.toAbsolutePos(source);
        var castedPos = Utils.FloatToInt(absLoc);
        var territory = WOCMap.Instance().GetTerritoryByPos(castedPos);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided position doesn't exists in any of territory (%s; %s; %s)", absLoc.x, absLoc.y, absLoc.z)));
            return 1;
        }
        if (territory.IsBased) {
            source.sendMessage(Text.literal(String.format("Can't set owner for base territory (%s) (%s; %s; %s)", territory.Id, absLoc.x, absLoc.y, absLoc.z)));
            return 1;
        }

        TryToBuyTerritory(source, team, territory);
        return 1;
    }

}
