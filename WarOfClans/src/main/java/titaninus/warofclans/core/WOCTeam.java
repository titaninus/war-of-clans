package titaninus.warofclans.core;

import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import titaninus.warofclans.gamelogic.GameMaster;
import titaninus.warofclans.server.WarOfClansServer;

import java.util.ArrayList;
import java.util.List;

import static titaninus.warofclans.core.TeamColor.*;

public class WOCTeam {

    public static Identifier INIT_FROM_SERVER = new Identifier("warofclans:woc_team_init");
    public static Identifier UPDATE_FROM_SERVER = new Identifier("warofclans:woc_team_update");
    public static WOCTeam RedTeam;
    public static WOCTeam GreenTeam;
    public static WOCTeam YellowTeam;
    public static WOCTeam BlueTeam;

    public static ArrayList<WOCTeam> Teams;

    private static Scoreboard _scoreboard;
    private static class SerializationWrapper {
        public List<WOCTeam> Teams;

        public static SerializationWrapper Create(List<WOCTeam> teams) {
            var wrapper = new SerializationWrapper();
            wrapper.Teams = teams;
            return wrapper;
        }
    }

    public static List<WOCTeam> DeserializeMultiple(String mapRaw) {
        return ObjectMapper.create().readValue(mapRaw, SerializationWrapper.class).Teams;
    }

    public static String SerializeMultiple(List<WOCTeam> teams) {
        return ObjectMapper.create().writeValueAsString(SerializationWrapper.Create(teams));
    }

    public static WOCTeam Deserialize(String mapRaw) {
        return ObjectMapper.create().readValue(mapRaw, WOCTeam.class);
    }

    public static void InitializeFromTeams(List<WOCTeam> teams) {

        InitializeFromTeamsWithoutEvent(teams);
        ServerPlayNetworking.registerGlobalReceiver(INIT_FROM_SERVER, (server, player, handler, buf, responseSender) -> {
            SendTeamsToPlayer(player);
        });
    }

    public static void InitializeInClient(List<WOCTeam> teams) {
        for (var team : teams) {
            if (team.Color == Red) {
                RedTeam = team;
            }
            if (team.Color == Green) {
                GreenTeam = team;
            }
            if (team.Color == Blue) {
                BlueTeam = team;
            }
            if (team.Color == Yellow) {
                YellowTeam = team;
            }
        }
        if (RedTeam == null) {
            RedTeam = CreateTeamWithColor(Red);
        }
        if (GreenTeam == null) {
            GreenTeam = CreateTeamWithColor(Green);
        }
        if (BlueTeam == null) {
            BlueTeam = CreateTeamWithColor(Blue);
        }
        if (YellowTeam == null) {
            YellowTeam = CreateTeamWithColor(Yellow);
        }
        CollectTeamsWithoutScoreboard();
    }

    public static void InitializeFromTeamsWithoutEvent(List<WOCTeam> teams) {

        _scoreboard = WarOfClansServer.MinecraftServer.getScoreboard();
        for (var team : teams) {
            if (team.Color == Red) {
                RedTeam = team;
            }
            if (team.Color == Green) {
                GreenTeam = team;
            }
            if (team.Color == Blue) {
                BlueTeam = team;
            }
            if (team.Color == Yellow) {
                YellowTeam = team;
            }
            team.ReloadFromSerialized();
        }
        if (RedTeam == null) {
            RedTeam = CreateTeamWithColor(Red);
        }
        if (GreenTeam == null) {
            GreenTeam = CreateTeamWithColor(Green);
        }
        if (BlueTeam == null) {
            BlueTeam = CreateTeamWithColor(Blue);
        }
        if (YellowTeam == null) {
            YellowTeam = CreateTeamWithColor(Yellow);
        }
        CollectTeams();
        for (var t : Teams) {
            t.WriteTeam();
        }
    }


    public static void InitializeFromScratch() {

        _scoreboard = WarOfClansServer.MinecraftServer.getScoreboard();
        RedTeam = CreateTeamWithColor(Red);
        GreenTeam = CreateTeamWithColor(Green);
        BlueTeam = CreateTeamWithColor(Blue);
        YellowTeam = CreateTeamWithColor(Yellow);
        CollectTeams();
        for (var t : Teams) {
            t.WriteTeam();
        }

        ServerPlayNetworking.registerGlobalReceiver(INIT_FROM_SERVER, (server, player, handler, buf, responseSender) -> {
            SendTeamsToPlayer(player);
        });
    }

    public static void SendTeamsToPlayer(ServerPlayerEntity p) {

        var newBuf = PacketByteBufs.create();
        var serializedInstance = SerializeMultiple(Teams);
        newBuf.writeString(serializedInstance);

        ServerPlayNetworking.send(p, UPDATE_FROM_SERVER, newBuf);
    }

    public TeamColor Color;
    //public transient ArrayList<PlayerEntity> Players;
    public ArrayList<String> PlayerNames;
    public transient Team team;
    public String TeamName;

    public int AvailablePoints;
    public int PointsOverall;
    public boolean InAnnihilationMode;

    public int LivesAmount = 1;
    public boolean IsTeamAlive;

    public String GetSerialized() {
        return ObjectMapper.create().writeValueAsString(this);
    }


    private void ReloadFromSerialized() {
        ReloadTeam();
    }

    private void ReloadTeam() {
        team = _scoreboard.getTeam(TeamName);
    }

    private void WriteTeam() {
        TeamName = team.getName();
    }


    private static void CreateScoreboardTeams() {
        CreateScoreboardTeam(Red);
        CreateScoreboardTeam(Green);
        CreateScoreboardTeam(Yellow);
        CreateScoreboardTeam(Blue);
    }

    private static void CreateScoreboardTeam(TeamColor teamColor) {
        var team = teamColor.toString();
        if (_scoreboard.getTeam(team) == null) {
            Team team2 = _scoreboard.addTeam(team);
            team2.setDisplayName(Text.literal(team));
        }
        var useTeam = _scoreboard.getTeam(team);
        assert useTeam != null;
        useTeam.setColor(teamColor.Convert());
        switch (teamColor) {
            case Red -> {
                RedTeam.team = useTeam;
            }
            case Green -> {
                GreenTeam.team = useTeam;
            }
            case Yellow -> {
                YellowTeam.team = useTeam;
            }
            case Blue -> {
                BlueTeam.team = useTeam;
            }
        }
    }

    private static void CollectTeamsWithoutScoreboard() {
        Teams = new ArrayList<>();
        Teams.add(RedTeam);
        Teams.add(GreenTeam);
        Teams.add(YellowTeam);
        Teams.add(BlueTeam);
    }

    private static void CollectTeams() {
        CollectTeamsWithoutScoreboard();
        CreateScoreboardTeams();
    }

    public static WOCTeam CreateTeamWithColor(TeamColor color) {
        var team = new WOCTeam();
        team.Color = color;
        team.PlayerNames = new ArrayList<>();
        return team;
    }


    public static boolean RemovePlayerFromTeam(WOCTeam team, PlayerEntity player) {
        return team.RemovePlayer(player);
    }

    public static boolean AddPlayerToTeam(WOCTeam team, PlayerEntity player) {
        return team.AddPlayer(player);
    }

    public static WOCTeam FindPlayerTeam(PlayerEntity player) {
        for (var team : Teams) {
            if (team.PlayerNames.contains(player.getName().getString())) {
                return team;
            }
        }
        return null;
    }

    public static WOCTeam GetTeamByColor(TeamColor color) {
        return switch (color) {
            case Red -> RedTeam;
            case Green -> GreenTeam;
            case Yellow -> YellowTeam;
            case Blue -> BlueTeam;
            default -> throw new IllegalStateException("Unexpected value: " + color);
        };
    }

    public Text MakeColoredText(String text) {
        var colorCode = Color.getColorCode();
        return Text.literal(colorCode + String.join(" " + colorCode, text.split(" ")));
    }

    public boolean RemovePlayer(PlayerEntity player) {
        var name = player.getName().getString();
        if (PlayerNames.contains(name)) {
            PlayerNames.remove(name);
            _scoreboard.removePlayerFromTeam(player.getName().getString(), team);
            GameMaster.Instance().PrepareAndSave();
            return true;
        }
        return false;
    }


    public boolean AddPlayer(PlayerEntity player) {
        var name = player.getName().getString();
        var prevTeam = FindPlayerTeam(player);
        if (prevTeam != null) {
            var result = RemovePlayerFromTeam(prevTeam, player);
            if (!result) {
                return false;
            }
        }
        if (PlayerNames.contains(name)) {
            return false;
        }
        if (PlayerNames.size() < WarOfClansServer.WOC_CONFIG.NumberPlayersInTeam()) {
            PlayerNames.add(name);
            _scoreboard.addPlayerToTeam(player.getName().getString(), team);
            GameMaster.Instance().PrepareAndSave();
            return true;
        }
        return false;
    }

    public boolean ContainsPlayer(PlayerEntity player) {
        return PlayerNames.contains(player.getName().getString());
    }

    public boolean ContainsPlayer(String name) {
        return PlayerNames.contains(name);
    }

    public void AddPoints(int amount) {
        PointsOverall += amount;
        AvailablePoints += amount;
        GameMaster.Instance().PrepareAndSave();
    }

    public boolean CanWithdrawPoints(int amount) {
        return AvailablePoints >= amount;
    }

    public boolean WithdrawPoints(int amount) {
        if (CanWithdrawPoints(amount)) {
            AvailablePoints -= amount;
            GameMaster.Instance().PrepareAndSave();
            return true;
        } else {
            return false;
        }
    }

    public void EndGameForTeam() {
        IsTeamAlive = false;
        GameMaster.Instance().EndGameForTeamPlayers(this);
        GameMaster.Instance().CheckForGameEndCriteria();
    }

    @Override
    public String toString() {
        return String.format("%s", MakeColoredText("Team " + Color.toString()));
    }
}
