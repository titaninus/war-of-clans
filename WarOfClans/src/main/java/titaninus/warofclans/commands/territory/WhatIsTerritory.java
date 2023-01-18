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
import titaninus.warofclans.core.Utils;
import titaninus.warofclans.core.WOCMap;

import static net.minecraft.server.command.CommandManager.literal;

public class WhatIsTerritory implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("whatIsTerritory")
                        .then(CommandManager.argument("location", Vec3ArgumentType.vec3()).executes((context) -> {
                            return execute((ServerCommandSource)context.getSource(), Vec3ArgumentType.getPosArgument(context, "location"));
                        }))
                        .then(CommandManager.argument("id", IntegerArgumentType.integer()).executes((context) -> {
                            return execute((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "id"));
                        }))
                        .executes((ctx) -> execute(ctx.getSource())));
    }

    private int execute(ServerCommandSource source, int id) {
        var territory = WOCMap.Instance().Territories.get(id);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided id doesn't belongs to any of territory (%s)", id)));
            return 1;
        }
        source.sendMessage(Text.literal(String.format("%s", territory)));
        return 0;
    }

    private int execute(ServerCommandSource source) {
        var player = source.getPlayer();
        var loc = player.getBlockPos();
        var territory = WOCMap.Instance().GetTerritoryByPos(loc);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided position doesn't exists in any of territory (%s; %s; %s)", loc.getX(), loc.getY(), loc.getZ())));
            return 1;
        }
        source.sendMessage(Text.literal(String.format("%s", territory)));
        return 0;
    }

    private int execute(ServerCommandSource source, PosArgument location) {
        var absLoc = location.toAbsolutePos(source);
        var castedPos = Utils.FloatToInt(absLoc);
        var territory = WOCMap.Instance().GetTerritoryByPos(castedPos);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided position doesn't exists in any of territory (%s; %s; %s)", absLoc.x, absLoc.y, absLoc.z)));
            return 1;
        }
        source.sendMessage(Text.literal(String.format("%s", territory)));
        return 1;
    }

}
