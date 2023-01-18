package titaninus.warofclans.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import titaninus.warofclans.core.WOCClientConfig;
import titaninus.warofclans.core.WOCMap;
import titaninus.warofclans.core.WOCTeam;


@Environment(EnvType.CLIENT)
public class WarOfClansClient implements ClientModInitializer {
    public static final WOCClientConfig WOC_CONFIG = WOCClientConfig.createAndLoad();

    public static WOCMap MapInstance;
    public static MinecraftClient Client;

    @Override
    public void onInitializeClient() {

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            Client = client;
            InitializeTeamsFromClient();
            FromServerInitializeMap();
            MapInstance = WOCMap.Instance();
        });
        //ScreensEntryPoint.Initialize();
    }


    public static void InitializeTeamsFromClient() {

        ClientPlayNetworking.registerGlobalReceiver(WOCTeam.UPDATE_FROM_SERVER, (client, handler, buf, responseSender) -> {
            var mapRaw = buf.readString();
            var teams = WOCTeam.DeserializeMultiple(mapRaw);
            WOCTeam.InitializeInClient(teams);
        });

        ClientPlayNetworking.send(WOCTeam.INIT_FROM_SERVER, PacketByteBufs.create());
    }


    public void FromServerInitializeMap() {
        ClientPlayNetworking.registerGlobalReceiver(WOCMap.UPDATE_FROM_SERVER, (client, handler, buf, responseSender) -> {
            var mapRaw = buf.readString();
            WOCMap.InitializeWithReadyMap(WOCMap.Deserialize(mapRaw));
        });

        ClientPlayNetworking.send(WOCMap.INIT_FROM_SERVER, PacketByteBufs.create());
    }
}
