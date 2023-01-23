package titaninus.warofclans;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.command.argument.serialize.StringArgumentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import titaninus.warofclans.commands.FormattableTimeArgumentType;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.ServerMessageSender;
import titaninus.warofclans.core.WarOfClansConfig;
import titaninus.warofclans.gamelogic.GameMaster;
import titaninus.warofclans.screens.ScreensEntryPoint;

public class WarOfClans implements ModInitializer {


    @Override
    public void onInitialize() {
        ArgumentTypeRegistry.registerArgumentType(new Identifier("warofclans", "formattabletime"), FormattableTimeArgumentType.class, ConstantArgumentSerializer.of(FormattableTimeArgumentType::create));
    }
}
