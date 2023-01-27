package titaninus.warofclans;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import titaninus.warofclans.blocks.TeamBanner;
import titaninus.warofclans.core.TeamColor;

public class WarOfClans implements ModInitializer {


    public static final String MOD_ID = "warofclans";
    public static final TeamBanner RED_TEAM_BANNER = new TeamBanner(TeamColor.Red, AbstractBlock.Settings.of(Material.WOOL).strength(5));
    //public static final TeamBanner GREEN_TEAM_BANNER = new TeamBanner(TeamColor.Green, AbstractBlock.Settings.of(Material.WOOL).strength(5));
    //public static final TeamBanner YELLOW_TEAM_BANNER = new TeamBanner(TeamColor.Yellow, AbstractBlock.Settings.of(Material.WOOL).strength(5));
    //public static final TeamBanner BLUE_TEAM_BANNER = new TeamBanner(TeamColor.Blue, AbstractBlock.Settings.of(Material.WOOL).strength(5));
    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "red_team_banner"), RED_TEAM_BANNER);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "red_team_banner"), new BlockItem(RED_TEAM_BANNER, new FabricItemSettings().fireproof().maxCount(1)));
    }
}
