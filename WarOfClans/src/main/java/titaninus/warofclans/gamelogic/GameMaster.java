package titaninus.warofclans.gamelogic;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import oshi.util.tuples.Pair;
import titaninus.warofclans.core.*;
import titaninus.warofclans.core.WOCGameConfig;
import titaninus.warofclans.core.interfaces.Updatable;
import titaninus.warofclans.server.WarOfClansServer;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static titaninus.warofclans.core.WOCTeam.Teams;

@Environment(EnvType.SERVER)
public class GameMaster implements Updatable {
    private static GameMaster _instance;

    public static GameMaster Instance() {
        return _instance;
    };
    private WOCGameConfig GameConfig;
    public ProtectionHelper Protector;
    private Battle _currentBattle;

    public static void Initialize() {
        if (_instance == null) {
            _instance = new GameMaster();
            _instance.ReadGameState();
        }
    }

    public GameMaster() {
        _updatables = new ArrayList<>();
    }

    public boolean IsGameInitialized() {
        return GameConfig.IsGameInitialized();
    }

    public boolean IsGameStarted() {
        return GameConfig.IsGameStarted();
    }

    public int GetCurrentStage() {
        return GameConfig.runningStage();
    }

    public boolean IsEconomicWarAvailable() {return GameConfig.runningStage() == 1 || GameConfig.runningStage() == 2;}

    public boolean IsInBattle() {return _currentBattle != null;}

    public Battle GetCurrentBattle() {return _currentBattle;}

    private final List<Updatable> _updatables;

    private void ReadGameState() {
        Protector = new ProtectionHelper();
        GameConfig = WOCGameConfig.createAndLoad();
        if (GameConfig.IsGameInitialized()) {
            WOCTeam.InitializeFromTeams(GameConfig.Teams());
            WOCMap.InitializeWithReadyMap(GameConfig.Map());
            WOCMap.Instance().InitializeBySerializing();
            ReloadAfterLoadFromFile();
        } else {
            WOCTeam.InitializeFromScratch();
            WOCMap.Initialize();
            PrepareAndSave();
            SetPreStage();
        }
    }

    public void PrepareAndSave() {
        WOCMap.Instance().PrepareForSerialize();
        GameConfig.Map(WOCMap.Instance());
        GameConfig.Teams(Teams);
        GameConfig.save();
        SendMapToAllPlayers();
        SendTeamsToAllPlayers();
    }

    private void SendTeamsToAllPlayers() {
        for (var p: ServerMessageSender.GetAllPlayers()) {
            WOCTeam.SendTeamsToPlayer(p);
        }
    }

    private void SendMapToAllPlayers() {
        for (var p: ServerMessageSender.GetAllPlayers()) {
            WOCMap.SendMapToPlayer(p);
        }
    }

    private void SetPreStage() {
        Utils.SetWorldSpawnPosition(Utils.ConvertFrom3IntArray(WarOfClansServer.WOC_CONFIG.LobbySpawnPos()), 0);
    }

    public void StartStage0() {
        GameConfig.IsGameInitialized(true);
        GameConfig.runningStage(0);

        for (var t: WOCTeam.Teams) {
            t.AddPoints(WarOfClansServer.WOC_CONFIG.AmountOfStartPoints());
        }
        PrepareAndSave();

    }

    public void ReloadAfterLoadFromFile() {
        var stage = GetCurrentStage();
        switch (stage) {
            case 1: ReloadStage1();
            case 2: ReloadStage2();
            case 3: ReloadStage3();
            default: {
                if (GameConfig.IsGameFinished()) {
                    ReloadEndGame();
                }
            }
        }
    }

    public void StartStage1() {
        GameConfig.IsGameStarted(true);
        GameConfig.runningStage(1);
        PrepareAndSave();

        for (var team: Teams) {
            var pos = WOCMap.Instance().GetBaseTerritoryOfTeam(team).SpawnPosition;
            SetSpawnPointForPlayersInTeam(team, new BlockPos(pos));
        }
    }

    public void ReloadStage1() {

    }


    public void StartStage2() {
        GameConfig.runningStage(2);
        GameConfig.save();
        PrepareAndSave();
    }

    public void ReloadStage2() {

    }

    public void StartStage3() {
        GameConfig.runningStage(3);
        GameConfig.save();
        PrepareAndSave();
    }

    public void ReloadStage3() {

    }

    public void EndGame() {
        GameConfig.IsGameFinished(true);
        GameConfig.save();

        var pos = Utils.ConvertFrom3IntArray(WarOfClansServer.WOC_CONFIG.LobbySpawnPos());
        for (var team: Teams) {
            SetSpawnPointForPlayersInTeam(team, pos);
        }
        PrepareAndSave();
    }

    public void ReloadEndGame() {

    }

    public void SetSpawnPointForPlayersInTeam(WOCTeam team, BlockPos pos) {
        for (var player: team.PlayerNames) {
            var onlinePlayer = Utils.GetOnlinePlayerByName(player);
            if (onlinePlayer != null) {
                SetSpawnAndMovePlayerToPosition(new Pair<>(onlinePlayer, pos));
            } else {
                Utils.AddToPlayerQueue(player, GameMaster::SetSpawnAndMovePlayerToPosition, pos);
            }
        }
    }

    public void StartBattlePeriod (FormattableTime until) {
        var battle = new Battle(LocalDateTime.now(), until);
    }

    public void EndBattleExternal() {
        if (_currentBattle != null) {
            _currentBattle.End();
        }
    }

    private static boolean SetSpawnAndMovePlayerToPosition(Pair<ServerPlayerEntity, Object> Data) {
        Utils.SetPlayerSpawnPosition(Data.getA(),(BlockPos) Data.getB(), 0);
        Utils.TeleportPlayer(Data.getA(),(BlockPos)  Data.getB(), 0, 0);
        return true;
    }

    private void DeleteGameState() {
        try {
            Files.deleteIfExists(GameConfig.fileLocation());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ResetGame() {
        WOCTeam.InitializeFromScratch();
        WOCMap.InitializeForce();
        PrepareAndSave();
    }

    public void RunStage(int step) {
        if ((step - GetCurrentStage()) != 1) {
            return;
        }
        if (!WarOfClansServer.WOC_CONFIG.AllowAutoCompleteStage()) {
            return;
        }
        switch (step){
            case 0:  return;
            case 1: StartStage2();
            case 2: StartStage3();
            case 3: EndGame();
            default: {
                return;
            }
        }
    }
    public boolean BuyTerritory(WOCTeam source, Territory target) {
        var price = target.ActualPrice();
        if (!source.CanWithdrawPoints(price)) {
            return false;
        }
        var neighbours  = WOCMap.GetNeighbourTerritoriesFor(target);
        if (neighbours.stream().noneMatch(t -> t.IsCaptured && t.OwnerTeam == source)) {
            return false;
        }
        source.WithdrawPoints(price);
        Protector.ReloadProtectionAfterBuyingFor(target, source, WarOfClansServer.WOC_CONFIG.TimeToChangeTeamForTerritory());
        return true;
    }

    public boolean CaptureTerritory(WOCTeam source, Territory target) {
        if (target.IsBased) {
            return false;
        }
        Protector.ReloadProtectionAfterCapturingFor(target, source, WarOfClansServer.WOC_CONFIG.TimeToChangeTeamForTerritory());
        return true;
    }

    public boolean CaptureBaseTerritory(WOCTeam source, Territory target) {
        if (!target.IsBased) {
            return false;
        }
        if (!target.OwnerTeam.InAnnihilationMode) {
            return false;
        }
        Protector.ReloadProtectionAfterCapturingBaseFor(target, source, WarOfClansServer.WOC_CONFIG.TimeToChangeTeamForTerritory());
        return true;
    }

    public void CheckForGameEndCriteria() {
        var aliveTeams = Teams.stream().filter(t -> t.IsTeamAlive).toList();
        if (aliveTeams.size() == 1) {
            StartSoftEndGame(aliveTeams.get(0));
        }
    }

    private void StartSoftEndGame(WOCTeam winnerTeam) {

    }

    public void EndGameForTeamPlayers(WOCTeam wocTeam) {

        var pos = Utils.ConvertFrom3IntArray(WarOfClansServer.WOC_CONFIG.LobbySpawnPos());
        SetSpawnPointForPlayersInTeam(wocTeam, pos);
    }

    public void InternalStopBattle(Battle battle) {
        if (_currentBattle != battle) {
            return;
        }
        _updatables.remove(battle);
        _currentBattle = null;
        DeleteAllInstalledTotems();
    }

    @Override
    public void Update() {
        for (var u: _updatables) {
            u.Update();
        }
    }

    public void CheckForTerritoryCapture(Territory captureTerritory) {
        if (captureTerritory.CapturingTotems.size() != 5) {
            return;
        }
        var isAllActivated = true;
        for (var t: captureTerritory.CapturingTotems) {
            if (!t.IsActivated) {
                isAllActivated = false;
            }
        }
        if (!isAllActivated) {
            return;
        }
        // 0 = Red, 1 = Yellow, 2 = Blue, 3 = Green
        var totems = new int[4];
        for (var t: captureTerritory.CapturingTotems) {
            if (t.OwnedTeam == TeamColor.Red) {
                totems[0] += 1;
            }
            if (t.OwnedTeam == TeamColor.Yellow) {
                totems[1] += 1;
            }
            if (t.OwnedTeam == TeamColor.Blue) {
                totems[2] += 1;
            }
            if (t.OwnedTeam == TeamColor.Green) {
                totems[3] += 1;
            }
        }
        if (totems[0] >= 3) {
            CaptureTerritory(WOCTeam.RedTeam, captureTerritory);
            return;
        }
        if (totems[1] >= 3) {
            CaptureTerritory(WOCTeam.YellowTeam, captureTerritory);
            return;
        }
        if (totems[2] >= 3) {
            CaptureTerritory(WOCTeam.BlueTeam, captureTerritory);
            return;
        }
        if (totems[3] >= 3) {
            CaptureTerritory(WOCTeam.GreenTeam, captureTerritory);
            return;
        }
    }

    private void DeleteAllInstalledTotems() {
        for (var t: WOCMap.Instance().Territories) {
            t.DeleteCaptureTotems();
        }
    }
}
