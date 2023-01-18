package titaninus.warofclans.commands.general;

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
import titaninus.warofclans.gamelogic.GameMaster;

import static net.minecraft.server.command.CommandManager.literal;

public class SaveWOCServerSettings implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("saveWOCServerSettings")
                        .requires(source -> source.hasPermissionLevel(4))
                        .executes((ctx) -> execute(ctx.getSource())));
    }

    private int execute(ServerCommandSource source) {
        GameMaster.Instance().PrepareAndSave();
        source.sendMessage(Text.literal("Successfully save woc-server-data"));
        return 0;
    }

}
