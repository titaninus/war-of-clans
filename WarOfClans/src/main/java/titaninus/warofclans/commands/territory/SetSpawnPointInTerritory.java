package titaninus.warofclans.commands.territory;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.TeamColor;
import titaninus.warofclans.core.Utils;
import titaninus.warofclans.core.WOCMap;
import titaninus.warofclans.core.WOCTeam;

import java.util.Collection;
import java.util.Collections;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetSpawnPointInTerritory implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("setSpawnPointForTerritory")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.argument("location", Vec3ArgumentType.vec3()).executes((context) -> {
                            return execute((ServerCommandSource)context.getSource(), Vec3ArgumentType.getPosArgument(context, "location"));
                        })));
    }

    private int execute(ServerCommandSource source, PosArgument location) {
        var absLoc = location.toAbsolutePos(source);
        var castedPos = Utils.FloatToInt(absLoc);
        var territory = WOCMap.Instance().GetTerritoryByPos(castedPos);
        if (territory == null) {
            source.sendMessage(Text.literal(String.format("Provided position doesn't exists in any of territory (%s; %s; %s)", absLoc.x, absLoc.y, absLoc.z)));
            return 1;
        }
        territory.SetSpawnPoint(castedPos);
        source.sendMessage(Text.literal(String.format("Successfully set spawn point for territory %s (%s; %s; %s)", territory, absLoc.x, absLoc.y, absLoc.z)));

        return 1;
    }

}
