package titaninus.warofclans.commands.totems;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import titaninus.warofclans.commands.FormattableTimeArgumentType;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.FormattableTime;
import titaninus.warofclans.server.WarOfClansServer;

import static net.minecraft.server.command.CommandManager.literal;

public class SetTimeForTotemActivationInEnemy implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("setTimeForCaptureTotemInEnemy")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.argument("time", FormattableTimeArgumentType.create()).executes(
                                (context) -> execute(context.getSource(), FormattableTimeArgumentType.getTime(context, "time")))
                        ));
    }

    private int execute(ServerCommandSource source, FormattableTime time) {
        WarOfClansServer.WOC_CONFIG.TimeToCaptureTotemInEnemy(time);
        WarOfClansServer.WOC_CONFIG.save();
        source.sendMessage(Text.literal("Successfully chaged time to activate time on neutral to " + time.toString()));
        return 1;
    }

}
