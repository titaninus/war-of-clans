package titaninus.warofclans.core;

import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import titaninus.warofclans.server.WarOfClansServer;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

public class WOCMap {
    private static WOCMap _instance;

    public static WOCMap Instance() {
        return _instance;
    }

    public static Identifier INIT_FROM_SERVER = new Identifier("warofclans:woc_map_init");
    public static Identifier UPDATE_FROM_SERVER = new Identifier("warofclans:woc_map_update");

    public static void Initialize() {
        if (_instance == null) {
            _instance = new WOCMap();
            _instance.ActualInitialize();

            ServerPlayNetworking.registerGlobalReceiver(INIT_FROM_SERVER, (server, player, handler, buf, responseSender) -> {
                SendMapToPlayer(player);
            });
        }
    }

    public static void InitializeForce() {
        _instance = new WOCMap();
        _instance.ActualInitialize();
    }

    public static void SendMapToPlayer(ServerPlayerEntity player) {
        var newBuf = PacketByteBufs.create();
        var serializedInstance = _instance.GetSerialized();
        newBuf.writeString(serializedInstance);

        ServerPlayNetworking.send(player, UPDATE_FROM_SERVER, newBuf);
    }

    @Transient
    private String GetSerialized() {
        return ObjectMapper.create().writeValueAsString(this);
    }

    public static WOCMap Deserialize(String mapRaw) {
        return ObjectMapper.create().readValue(mapRaw, WOCMap.class);
    }

    public static void InitializeWithReadyMap(WOCMap map) {
        _instance = map;
    }

    public static WOCMap New() {
        return new WOCMap();
    }

    public static List<Territory> GetNeighbourTerritoriesFor(Territory target) {
        var result = new ArrayList<Territory>();
        var width = WarOfClansServer.WOC_CONFIG.MapWidth();
        var height = WarOfClansServer.WOC_CONFIG.MapHeight();
        var x = target.Id / width;
        var z = target.Id % height;
        // add x - 1
        if (x > 0) {
            // add z - 1
            if (z > 0) {
                var id = (x - 1) * height + (z - 1);
                result.add(_instance.Territories.get(id));
            }
            // add z + 1
            if (z < height) {
                var id = (x - 1) * height + (z + 1);
                result.add(_instance.Territories.get(id));
            }
        }
        // x + 1
        if (x < width) {
            // add z - 1
            if (z > 0) {
                var id = (x + 1) * height + (z - 1);
                result.add(_instance.Territories.get(id));
            }
            // add z + 1
            if (z < height) {
                var id = (x + 1) * height + (z + 1);
                result.add(_instance.Territories.get(id));
            }
        }
        return result;
    }


    public ArrayList<Territory> Territories = new ArrayList<>();
    public int startX;
    public int startZ;
    public int endX;
    public int endZ;

    public void InitializeBySerializing() {
        for (var t: Territories) {
            t.ReloadFromSerialize();
        }
    }

    public void PrepareForSerialize() {

        for (var t: Territories) {
            t.BeforeSerialize();
        }
    }

    private void ActualInitialize() {
        var config = WarOfClansServer.WOC_CONFIG;
        startX = config.startXOffset();
        endX = startX + config.MapWidth() * config.territorySize();
        startZ = config.startZOffset();
        endZ = startZ + config.MapHeight() * config.territorySize();
        var mapsPool = WarOfClansServer.WOC_CONFIG.MinesMap();
        if (mapsPool.size() < config.MapHeight()) {
            mapsPool = new ArrayList<>(List.of(
                    List.of(List.of(-1), List.of(1, 2), List.of(1, 3), List.of(1, 2), List.of(-1)),
                    List.of(List.of(1, 2), List.of(4, 3), List.of(4, 5), List.of(4, 3), List.of(1, 2)),
                    List.of(List.of(1, 3), List.of(5, 2), List.of(6), List.of(5, 2), List.of(1, 3)),
                    List.of(List.of(1, 2), List.of(4, 3), List.of(4, 5), List.of(4, 3), List.of(1, 2)),
                    List.of(List.of(-1), List.of(1, 2), List.of(1, 3), List.of(1, 2), List.of(-1))
            ));
            WarOfClansServer.WOC_CONFIG.MinesMap(mapsPool);
            WarOfClansServer.WOC_CONFIG.save();
        }
        for (int i = 0; i < config.MapWidth(); ++i) {
            for (int j = 0; j < config.MapHeight(); ++j) {
                var mapPool = mapsPool.get(j).get(i);
                var territory = new Territory();
                Territories.add(territory);
                var x = startX + i * config.territorySize();
                var z = startZ + j * config.territorySize();
                if (i == 0 && j == 0) {
                    // Red team
                    territory.Initialize(i * config.MapHeight() + j, x, z, true, WOCTeam.RedTeam, mapPool);
                } else if (i == 0 && j == config.MapHeight() - 1) {
                    // Yellow
                    territory.Initialize(i * config.MapHeight() + j, x, z, true, WOCTeam.YellowTeam, mapPool);
                } else if (i == config.MapWidth() - 1 && j == 0) {
                    // Blue
                    territory.Initialize(i * config.MapHeight() + j, x, z, true, WOCTeam.BlueTeam, mapPool);
                } else if (i == config.MapWidth() - 1 && j == config.MapHeight() - 1) {
                    // Green
                    territory.Initialize(i * config.MapHeight() + j, x, z, true, WOCTeam.GreenTeam, mapPool);
                } else {
                    // Neutral
                    territory.Initialize(i * config.MapHeight() + j, x, z, false, null, mapPool);
                }
                territory.BeforeSerialize();
            }
        }
    }
    public Territory GetBaseTerritoryOfTeam(WOCTeam team) {
        for (var territory: Territories) {
            if (territory.IsBased && territory.OwnerTeam == team) {
                return territory;
            }
        }
        return null;
    }

    public Territory GetTerritoryByPos(Vec3i pos) {
        if (pos.getX() < startX || pos.getX() > endX || pos.getZ() < startZ || pos.getZ() > endZ) {
            return null;
        }
        for (var t : Territories) {
            if (t.IsInsideTerritory(pos)) {
                return t;
            }
        }
        return null;
    }
}
