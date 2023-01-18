package titaninus.warofclans.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import titaninus.warofclans.core.Utils;
import titaninus.warofclans.core.WOCMap;
import titaninus.warofclans.server.WarOfClansServer;

import static net.minecraft.server.command.CommandManager.literal;

public class SpawnInLobby implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("spawnInLobby")
                        .requires(source -> source.hasPermissionLevel(4))
                        .executes((ctx) -> execute(ctx.getSource())));
    }


    private int execute(ServerCommandSource source) {
        var player = source.getPlayer();
        var target = Utils.ConvertFrom3IntArray(WarOfClansServer.WOC_CONFIG.LobbySpawnPos());
        Utils.TeleportPlayer(player, new BlockPos(target), 0, 0);
        return 0;
    }

}
