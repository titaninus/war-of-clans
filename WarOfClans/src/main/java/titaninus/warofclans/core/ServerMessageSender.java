package titaninus.warofclans.core;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class ServerMessageSender {
    public static MinecraftServer Server;
    public static void SendMessageToAll(String text) {
        Server.getPlayerManager().broadcast(Text.literal(text), true);
    }
    public static void Initialize(MinecraftServer server) {
        Server = server;
    }
    public static List<ServerPlayerEntity> GetAllPlayers() {
        return Server.getPlayerManager().getPlayerList();
    }
}
