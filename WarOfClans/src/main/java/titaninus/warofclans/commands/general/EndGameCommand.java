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

public class EndGameCommand implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("endGame")
                        .requires(source -> source.hasPermissionLevel(4))
                        .executes(EndGameCommand::execute));
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        GameMaster.Instance().EndGame();

        context.getSource().sendMessage(Text.literal("Game Ended! Calculating winners"));
        return 1;
    }
}
