package titaninus.warofclans.commands.territory;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import titaninus.warofclans.commands.FormattableTimeArgumentType;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.FormattableTime;
import titaninus.warofclans.server.WarOfClansServer;

import static net.minecraft.server.command.CommandManager.literal;

public class SetCostMultiplier implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("setCostMultiplier")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(CommandManager.argument("multiplier", FloatArgumentType.floatArg(0.5f, 1000)).executes(
                                (context) -> execute(context.getSource(), FloatArgumentType.getFloat(context, "multiplier")))
                        ));
    }

    private int execute(ServerCommandSource source, float multiplier) {
        WarOfClansServer.WOC_CONFIG.CostMultiplier(multiplier);
        WarOfClansServer.WOC_CONFIG.save();
        source.sendMessage(Text.literal("Successfully chaged Cost Multiplier to " + multiplier));
        return 1;
    }

}
