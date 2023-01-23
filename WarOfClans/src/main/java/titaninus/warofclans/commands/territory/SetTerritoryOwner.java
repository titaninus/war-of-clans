package titaninus.warofclans.commands.territory;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.*;
import titaninus.warofclans.server.WarOfClansServer;

import static net.minecraft.server.command.CommandManager.literal;

public class SetTerritoryOwner implements WOCCommandManager.RegistrableCommand {
    private enum SpawnOnTerritoryMode {
        Start,
        Middle,
        End,
        Spawn
    }

    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("setTerritoryOwner")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.argument("location", Vec3ArgumentType.vec3())
                                .then(literal("Red").executes((ctx) -> execute(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"), TeamColor.Red)))
                                .then(literal("Blue").executes((ctx) -> execute(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"), TeamColor.Blue)))
                                .then(literal("Green").executes((ctx) -> execute(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"), TeamColor.Green)))
                                .then(literal("Yellow").executes((ctx) -> execute(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"), TeamColor.Yellow)))
                                .then(literal("None").executes((ctx) -> execute(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"), null))))
                        .then(CommandManager.argument("id", IntegerArgumentType.integer(0, WarOfClansServer.WOC_CONFIG.MapWidth() * WarOfClansServer.WOC_CONFIG.MapHeight() - 1))
                                .then(literal("Red").executes((ctx) -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"), TeamColor.Red)))
                                .then(literal("Blue").executes((ctx) -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"), TeamColor.Blue)))
                                .then(literal("Green").executes((ctx) -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"), TeamColor.Green)))
                                .then(literal("Yellow").executes((ctx) -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"), TeamColor.Yellow)))
                                .then(literal("None").executes((ctx) -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"), null))))
                        .then(literal("Red").executes((ctx) -> execute(ctx.getSource(), TeamColor.Red)))
                        .then(literal("Blue").executes((ctx) -> execute(ctx.getSource(), TeamColor.Blue)))
                        .then(literal("Green").executes((ctx) -> execute(ctx.getSource(), TeamColor.Green)))
                        .then(literal("Yellow").executes((ctx) -> execute(ctx.getSource(), TeamColor.Yellow)))
                        .then(literal("None").executes((ctx) -> execute(ctx.getSource(), null))));
    }

    private int execute(ServerCommandSource source, int id, TeamColor color) {
        if (color == null) {
            var territory = WOCMap.Instance().Territories.get(id);
            if (territory == null) {
                source.sendMessage(Text.literal(String.format("Provided id doesn't belongs to any of territory (%s)", id)));
                return 1;
            }
            if (territory.IsBased) {
                source.sendMessage(Text.literal(String.format("Can't set owner for base territory (%s)", id)));
                return 1;
            }
            territory.BecomeNeutral();
            source.sendMessage(Text.literal(String.format("Succesfully change territory (%s) owner to Neutral", territory)));
            return 1;
        }
        var team = WOCTeam.GetTeamByColor(color);
        var territory = WOCMap.Instance().Territories.get(id);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided id doesn't belongs to any of territory (%s)", id)));
            return 1;
        }
        if (territory.IsBased) {
            source.sendMessage(Text.literal(String.format("Can't set owner for base territory (%s)", id)));
            return 1;
        }
        territory.CaptureBy(team);
        source.sendMessage(Text.literal(String.format("Succesfully change territory (%s) owner to %s", territory, color)));
        return 0;
    }

    private int execute(ServerCommandSource source, TeamColor color) {
        if (color == null) {
            var player = source.getPlayer();
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
            territory.BecomeNeutral();
            source.sendMessage(Text.literal(String.format("Succesfully change territory (%s) owner to Neutral", territory)));
            return 1;
        }
        var team = WOCTeam.GetTeamByColor(color);
        var player = source.getPlayer();
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
        territory.CaptureBy(team);
        source.sendMessage(Text.literal(String.format("Succesfully change territory (%s) owner to %s", territory, color)));
        return 0;
    }

    private int execute(ServerCommandSource source, PosArgument location, TeamColor color) {
        if (color == null) {
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
            territory.BecomeNeutral();
            source.sendMessage(Text.literal(String.format("Succesfully change territory (%s) owner to Neutral", territory)));
            return 1;
        }
        var team = WOCTeam.GetTeamByColor(color);
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
        territory.CaptureBy(team);
        source.sendMessage(Text.literal(String.format("Succesfully change territory (%s) owner to %s", territory, color)));
        return 1;
    }

}
