package titaninus.warofclans.core;

import io.wispforest.owo.config.annotation.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;

@Environment(EnvType.SERVER)
@Config(name = "woc-game-config", wrapperName = "WOCGameConfig")
public class GameConfigModel {
    public int runningStage = -1;
    public boolean IsGameStarted = false;
    public boolean IsGameInitialized = false;
    public boolean IsGameFinished = false;
    public ArrayList<WOCTeam> Teams = new ArrayList<>();
    public WOCMap Map = WOCMap.New();
}
