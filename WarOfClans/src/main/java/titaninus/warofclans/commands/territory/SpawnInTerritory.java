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
import titaninus.warofclans.core.Territory;
import titaninus.warofclans.core.Utils;
import titaninus.warofclans.core.WOCMap;

import static net.minecraft.server.command.CommandManager.literal;

public class SpawnInTerritory implements WOCCommandManager.RegistrableCommand {
    private enum SpawnOnTerritoryMode {
        Start,
        Middle,
        End,
        Spawn
    }

    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("spawnInTerritory")
                        .requires(source -> source.hasPermissionLevel(4))
                            .then(CommandManager.argument("location", Vec3ArgumentType.vec3()).executes((context) -> {
                                return execute((ServerCommandSource) context.getSource(), Vec3ArgumentType.getPosArgument(context, "location"), SpawnOnTerritoryMode.Spawn);
                            })
                                .then(literal("Start").executes((ctx) -> execute(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"), SpawnOnTerritoryMode.Start)))
                                .then(literal("Middle").executes((ctx) -> execute(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"), SpawnOnTerritoryMode.Middle)))
                                .then(literal("End").executes((ctx) -> execute(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"), SpawnOnTerritoryMode.End)))
                                .then(literal("Spawn").executes((ctx) -> execute(ctx.getSource(), Vec3ArgumentType.getPosArgument(ctx, "location"), SpawnOnTerritoryMode.Spawn))))
                            .then(CommandManager.argument("id", IntegerArgumentType.integer(0, 24)).executes((context) -> {
                                return execute((ServerCommandSource) context.getSource(), IntegerArgumentType.getInteger(context, "id"), SpawnOnTerritoryMode.Spawn);
                            })
                                .then(literal("Start").executes((ctx) -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"), SpawnOnTerritoryMode.Start)))
                                .then(literal("Middle").executes((ctx) -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"), SpawnOnTerritoryMode.Middle)))
                                .then(literal("End").executes((ctx) -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"), SpawnOnTerritoryMode.End)))
                                .then(literal("Spawn").executes((ctx) -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "id"), SpawnOnTerritoryMode.Spawn))))
                            .then(literal("Start").executes((ctx) -> execute(ctx.getSource(), SpawnOnTerritoryMode.Start)))
                            .then(literal("Middle").executes((ctx) -> execute(ctx.getSource(), SpawnOnTerritoryMode.Middle)))
                            .then(literal("End").executes((ctx) -> execute(ctx.getSource(), SpawnOnTerritoryMode.End)))
                            .then(literal("Spawn").executes((ctx) -> execute(ctx.getSource(), SpawnOnTerritoryMode.Spawn)))
                        .executes((ctx) -> execute(ctx.getSource(), SpawnOnTerritoryMode.Spawn)));
    }

    private int execute(ServerCommandSource source, int id, SpawnOnTerritoryMode mode) {
        var territory = WOCMap.Instance().Territories.get(id);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided id doesn't belongs to any of territory (%s)", id)));
            return 1;
        }
        TeleportPlayerToTerritory(source, mode, territory);
        return 0;
    }

    private int execute(ServerCommandSource source, SpawnOnTerritoryMode mode) {
        var player = source.getPlayer();
        var loc = player.getBlockPos();
        var territory = WOCMap.Instance().GetTerritoryByPos(loc);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided position doesn't exists in any of territory (%s; %s; %s)", loc.getX(), loc.getY(), loc.getZ())));
            return 1;
        }
        TeleportPlayerToTerritory(source, mode, territory);
        return 0;
    }

    private int execute(ServerCommandSource source, PosArgument location, SpawnOnTerritoryMode mode) {
        var absLoc = location.toAbsolutePos(source);
        var castedPos = Utils.FloatToInt(absLoc);
        var territory = WOCMap.Instance().GetTerritoryByPos(castedPos);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided position doesn't exists in any of territory (%s; %s; %s)", absLoc.x, absLoc.y, absLoc.z)));
            return 1;
        }

        TeleportPlayerToTerritory(source, mode, territory);
        return 1;
    }

    private void TeleportPlayerToTerritory(ServerCommandSource source, SpawnOnTerritoryMode mode, Territory territory) {
        BlockPos pos = territory.GetMiddlePoint();
        switch (mode) {
            case Start -> {
                pos = new BlockPos(territory.startX, 140, territory.startZ);
                pos = pos.withY(Utils.FindFirstGroundYPos(pos));
            }
            case Middle -> {
                pos = territory.GetMiddlePoint();
                pos = pos.withY(Utils.FindFirstGroundYPos(pos));
            }
            case End -> {
                pos = new BlockPos(territory.endX, 140, territory.endZ);
                pos = pos.withY(Utils.FindFirstGroundYPos(pos));
            }
            case Spawn -> {
                pos = new BlockPos(territory.SpawnPosition);
            }
        }
        Utils.TeleportPlayer(source.getPlayer(), pos, 0, 0);
    }

}
