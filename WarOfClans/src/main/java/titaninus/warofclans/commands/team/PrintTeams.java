package titaninus.warofclans.commands.team;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.WOCTeam;

import static net.minecraft.server.command.CommandManager.literal;

public class PrintTeams implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("printTeams")
                        .requires(s -> s.hasPermissionLevel(4))
                        .executes(this::execute));
    }

    private int execute(CommandContext<ServerCommandSource> ctx) {
        for (var t: WOCTeam.Teams) {
            ctx.getSource().sendMessage(t.MakeColoredText("Team " + t.Color.toString()));
            for (var p: t.PlayerNames) {
                ctx.getSource().sendMessage(t.MakeColoredText(p));
            }
        }
        return 1;
    }

}
