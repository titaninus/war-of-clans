package titaninus.warofclans.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.ServerMessageSender;
import titaninus.warofclans.core.Utils;
import titaninus.warofclans.core.WarOfClansConfig;
import titaninus.warofclans.events.PlayerJoinCallback;
import titaninus.warofclans.gamelogic.GameMaster;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Environment(EnvType.SERVER)
public class WarOfClansServer implements DedicatedServerModInitializer {

    public static Event<Update> UPDATE = EventFactory.createArrayBacked(Update.class, (listeners) -> () -> {
        for (Update listener : listeners) {
            listener.update();
        }
    });
    public static MinecraftServer MinecraftServer;
    public static boolean IsLoaded = false;

    public static final WarOfClansConfig WOC_CONFIG = WarOfClansConfig.createAndLoad();

    public static WOCCommandManager CommandManager;
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(
                server -> {
                    MinecraftServer = server;
                    ServerMessageSender.Initialize(server);
                    GameMaster.Initialize();
                    IsLoaded = true;
                });
        PlayerJoinCallback.EVENT.register(new Utils.ResolveQueueOnPlayerJoin());
        //WarOfClansServer.UPDATE.register(new tickSecTest());

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Code to be executed
                WarOfClansServer.UPDATE.invoker().update();
            }
        });
        timer.setRepeats(true); // Only execute once
        timer.start(); // Go go go!

        CommandManager = new WOCCommandManager();
        CommandManager.Register();
        WOC_CONFIG.save();
    }

    private static class tickSecTest implements Update {
        @Override
        public void update() {
        System.out.println("[Server] Tick");
        }

    }

    public interface Update{
        void update();
    }
}
