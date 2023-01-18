package titaninus.warofclans.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import titaninus.warofclans.core.Utils;

// getString(ctx, "string")
// word()
// literal("foo")
import static net.minecraft.server.command.CommandManager.literal;
// argument("bar", word())
// Импортировать всё
import static net.minecraft.server.command.CommandManager.*;

public class TestCommand implements WOCCommandManager.RegistrableCommand {

    public static int giveDiamond(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final ServerCommandSource source = ctx.getSource();

        final PlayerEntity self = source.getPlayer(); // Если это не игрок, то команда заканчивается
        if(!self.getInventory().insertStack(new ItemStack(Items.DIAMOND))){
            throw new SimpleCommandExceptionType(Text.translatable("inventory.isfull")).create();
        }

        return 1;
    }

    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("foo")
                .then(CommandManager.literal("bar"))
                .executes(context -> {
                    System.out.println("foo and bar");
                    return 1;
                }));

        dispatcher
                .register(literal("giveMeDiamond")
                        .executes(TestCommand::giveDiamond));


        dispatcher
                .register(literal("testRespawnMeAtNextLogon")
                        .executes(TestCommand::respawnPlayer));
    }

    private static int respawnPlayer(CommandContext<ServerCommandSource> context) {
        var player = context.getSource().getPlayer();
        Utils.AddToPlayerQueue(player.getName().getString(), pair -> {
            context.getSource().getServer().getPlayerManager().respawnPlayer(pair.getA(), true);
            return true;
        }, true);
        return 0;
    }
}
