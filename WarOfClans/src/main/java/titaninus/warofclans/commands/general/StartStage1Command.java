package titaninus.warofclans.commands.general;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.gamelogic.GameMaster;

import static net.minecraft.server.command.CommandManager.literal;

public class StartStage1Command implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("startStage1")
                        .requires(source -> source.hasPermissionLevel(4))
                        .executes(StartStage1Command::execute));
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        GameMaster.Instance().StartStage1();

        context.getSource().sendMessage(Text.literal("Stage1 started!"));
        return 1;
    }
}
