package titaninus.warofclans.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import titaninus.warofclans.gamelogic.GameMaster;
import titaninus.warofclans.server.WarOfClansServer;

import java.util.ArrayList;
import java.util.List;

public class Territory {
    public boolean IsCaptured;
    public boolean IsBased;
    public transient WOCTeam OwnerTeam;
    public TeamColor OwnerTeamColor;

    public int startX;
    public int startZ;
    public int endX;
    public int endZ;

    public int Id;

    public boolean isInitialized;

    public transient ArrayList<Vec3i> TotemPoints;
    public ArrayList<ArrayList<Integer>> TotemPointsRaw;

    public transient Vec3i SpawnPosition = GetMiddlePoint();
    public ArrayList<Integer> SpawnPositionRaw;
    public int ActualPriceModifier = 1;

    public ArrayList<Mine> Mines = new ArrayList<>();
    public List<InstalledCaptureTotem> CapturingTotems = new ArrayList<>();

    public int BasePrice() {
        return (IsBased? 1000000000 : (IsCaptured? WarOfClansServer.WOC_CONFIG.EnemyTerritoryPrice(): WarOfClansServer.WOC_CONFIG.NeutralTerritoryPrice()))
                + Mines.stream().mapToInt(m -> m.Cost).sum();
    }

    public int ActualPrice() {
        return BasePrice() * ActualPriceModifier;
    }

    public void Buy(WOCTeam newOwner) {
        if (IsCaptured) {
            ActualPriceModifier *= WarOfClansServer.WOC_CONFIG.CostMultiplier();
        }
        IsCaptured = true;
        OwnerTeam = newOwner;
        OwnerTeamColor = OwnerTeam.Color;
        WriteTeam();
        GameMaster.Instance().PrepareAndSave();
    }

    public void SetTotemPoint(int id, Vec3i pos) {
        if (id < 0 || id >= 5 ) {
            return;
        }
        if (IsInsideTerritory(pos)) {
            TotemPoints.set(id, pos);
            WriteTotems();
            GameMaster.Instance().PrepareAndSave();
        }
    }
    public void SetSpawnPoint(Vec3i pos) {
        if (IsInsideTerritory(pos)) {
            SpawnPosition = pos;
            WriteSpawn();
            GameMaster.Instance().PrepareAndSave();
        }
    }

    public boolean CaptureBy(WOCTeam team) {
        if (IsBased) {
            return false;
        }
        IsCaptured = true;
        OwnerTeam = team;
        OwnerTeamColor = OwnerTeam.Color;
        WriteTeam();
        GameMaster.Instance().PrepareAndSave();
        return true;
    }

    public boolean CaptureBaseBy(WOCTeam team) {
        if (!IsBased) {
            return false;
        }
        IsBased = false;
        IsCaptured = true;
        var prevTeam = OwnerTeam;
        OwnerTeam = team;
        OwnerTeamColor = OwnerTeam.Color;
        WriteTeam();
        GameMaster.Instance().PrepareAndSave();
        prevTeam.EndGameForTeam();
        return true;
    }

    public void Initialize(int id, int x, int z, boolean isBased, WOCTeam ownerTeam, List<Integer> MinePool) {
        if (!isInitialized) {
            Id = id;
            startX = x;
            startZ = z;
            endX = x + WarOfClansServer.WOC_CONFIG.territorySize() - 1;
            endZ = z + WarOfClansServer.WOC_CONFIG.territorySize() - 1;
            if (isBased) {
                IsBased = true;
                OwnerTeam = ownerTeam;
                IsCaptured = true;
            } else {
                IsCaptured = false;
            }
            isInitialized = true;
            TotemPoints = new ArrayList<>();
            TotemPoints.add(new BlockPos(startX + 10, 70, startZ + 10));
            TotemPoints.add(new BlockPos(startX + 10, 70, endZ - 10));
            TotemPoints.add(new BlockPos(endX - 10, 70, endZ - 10));
            TotemPoints.add(new BlockPos(endX - 10, 70, startZ + 10));
            TotemPoints.add(new BlockPos((startX + endX) / 2, 70, (startZ + endZ) / 2));
            for (var i = 0; i < TotemPoints.size(); ++i) {
                var point = TotemPoints.get(i);
                var y = Utils.FindFirstGroundYPos(new BlockPos(point));
                TotemPoints.set(i, new BlockPos(point.getX(), y, point.getZ()));
            }
            SpawnPosition = new BlockPos(GetMiddlePoint().getX(), Utils.FindFirstGroundYPos(GetMiddlePoint()), GetMiddlePoint().getZ());
            Mines = new ArrayList<>();
            if (!MinePool.contains(-1)) {
                for (var m: MinePool) {
                    var mine = Mine.Create(m);
                    Mines.add(mine);
                }
            }
        }
    }

    public void ReloadFromSerialize() {
        ReloadTotems();
        ReloadTeam();
        ReloadSpawn();
        ReloadMines();
    }

    private void ReloadMines() {
        for (var m: Mines) {
            m.Init();
        }
    }

    private void ReloadTeam() {
        if (OwnerTeamColor != null) {
            OwnerTeam = WOCTeam.GetTeamByColor(OwnerTeamColor);
        }
    }


    public void ReloadTotems() {
        TotemPoints = new ArrayList<>();
        for (var t: TotemPointsRaw) {
            TotemPoints.add(Utils.ConvertFrom3IntArray(t));
        }
    }
    private void ReloadSpawn() {
        if (SpawnPositionRaw != null) {
            SpawnPosition = Utils.ConvertFrom3IntArray(SpawnPositionRaw);
        }
    }

    public void BeforeSerialize() {
        WriteTeam();
        WriteTotems();
        WriteSpawn();
    }

    public void WriteTeam() {
        if (OwnerTeam != null) {
            OwnerTeamColor = OwnerTeam.Color;
        }
    }

    public void WriteTotems() {
        TotemPointsRaw = new ArrayList<>();
        for (var t: TotemPoints) {
            TotemPointsRaw.add(new ArrayList<>(List.of(t.getX(), t.getY(), t.getZ())));
        }
    }


    public void WriteSpawn() {
        SpawnPositionRaw = new ArrayList<>(List.of(SpawnPosition.getX(), SpawnPosition.getY(), SpawnPosition.getZ()));
    }


    public BlockPos GetMiddlePoint() {
        return new BlockPos((startX + endX) / 2, 70, (startZ + endZ) / 2);
    }

    public boolean IsInsideTerritory(Vec3i pos) {
        return pos.getX() >= startX && pos.getX() <= endX && pos.getZ() >= startZ && pos.getZ() <= endZ;
    }

    @Override
    public String toString() {
        return String.format("%sTerritory %s owned by %s, from (%s; %s) to (%s; %s)", IsBased? "Based ": "", Id, IsCaptured? OwnerTeamColor: "No One", startX, startZ, endX, endZ);
    }

    public void BecomeNeutral() {
        if (!IsBased) {
            IsCaptured = false;
            OwnerTeam = null;
            OwnerTeamColor = null;
        }
    }

    public void DeleteCaptureTotems() {
        for (var c: CapturingTotems) {
            c.DeleteTotem();
        }
        CapturingTotems.clear();
    }
}
