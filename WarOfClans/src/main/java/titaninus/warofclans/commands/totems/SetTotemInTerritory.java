package titaninus.warofclans.commands.totems;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.Utils;
import titaninus.warofclans.core.WOCMap;

import static net.minecraft.server.command.CommandManager.literal;

public class SetTotemInTerritory implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("setTotemInTerritory")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.argument("territoryId", IntegerArgumentType.integer(0, 24))
                            .then(CommandManager.argument("totemId", IntegerArgumentType.integer(0, 4))
                                .then(CommandManager.argument("location", Vec3ArgumentType.vec3()).executes((context) -> {
                                    return execute((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "territoryId"), IntegerArgumentType.getInteger(context, "totemId"), Vec3ArgumentType.getPosArgument(context, "location"));
                        })))));
    }

    private int execute(ServerCommandSource source,int terrId, int totemId, PosArgument location) {
        var absLoc = location.toAbsolutePos(source);
        var castedPos = Utils.FloatToInt(absLoc);
        var territory = WOCMap.Instance().Territories.get(terrId);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided territoryId doesn't belong to any of territory (%s)", terrId)));
            return 1;
        }
        territory.SetTotemPoint(totemId, castedPos);
        source.sendMessage(Text.literal(String.format("Successfully set totem point {%s} for territory %s (%s; %s; %s)",totemId, territory, absLoc.x, absLoc.y, absLoc.z)));
        return 1;
    }

}
