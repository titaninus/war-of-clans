package titaninus.warofclans.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.sun.jna.IntegerType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import titaninus.warofclans.core.TeamColor;
import titaninus.warofclans.core.Utils;
import titaninus.warofclans.core.WOCMap;
import titaninus.warofclans.core.WOCTeam;
import titaninus.warofclans.server.WarOfClansServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class SetLobbySpawnPoint implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("setLobbySpawnPoint")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.argument("location", Vec3ArgumentType.vec3()).executes((context) -> {
                            return execute((ServerCommandSource)context.getSource(), Vec3ArgumentType.getPosArgument(context, "location"));
                        })));
    }

    private int execute(ServerCommandSource source, PosArgument location) {
        var absLoc = location.toAbsolutePos(source);
        var castedPos = Utils.FloatToInt(absLoc);
        var territory = WOCMap.Instance().GetTerritoryByPos(castedPos);
        if (territory != null) {
            source.sendMessage(Text.literal(String.format("Cannot set lobby in a territory (%s; %s; %s)", absLoc.x, absLoc.y, absLoc.z)));
            return 1;
        }
        WarOfClansServer.WOC_CONFIG.LobbySpawnPos(new ArrayList<Integer>(List.of(castedPos.getX(), castedPos.getY(), castedPos.getZ())));
        WarOfClansServer.WOC_CONFIG.save();
        Utils.SetWorldSpawnPosition(new BlockPos(castedPos), 0);
        source.sendMessage(Text.literal(String.format("Successfully set spawn point for lobby (%s; %s; %s)", absLoc.x, absLoc.y, absLoc.z)));
        return 1;
    }

}
