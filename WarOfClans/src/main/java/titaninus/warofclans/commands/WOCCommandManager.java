package titaninus.warofclans.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import titaninus.warofclans.commands.general.*;
import titaninus.warofclans.commands.team.*;
import titaninus.warofclans.commands.territory.*;
import titaninus.warofclans.commands.totems.*;

import java.util.ArrayList;
import java.util.List;


public class WOCCommandManager {

    public List<RegistrableCommand> Commands;
    public void Register() {

        Commands = new ArrayList<>();
        //Commands.add(new TestCommand());
        // Controls
        Commands.add(new StartStage0Command());
        Commands.add(new StartStage1Command());
        Commands.add(new StartStage2Command());
        Commands.add(new StartStage3Command());
        Commands.add(new SaveWOCServerSettings());
        //Teams
        Commands.add(new PrintTeams());
        Commands.add(new SetTeamByAdmin());
        Commands.add(new WhatIsMyTeam());
        Commands.add(new SetMyTeam());
        Commands.add(new GivePoints());
        Commands.add(new RemovePoints());
        // Territories
        Commands.add(new SetSpawnPointInTerritory());
        Commands.add(new SpawnInTerritory());
        Commands.add(new WhatIsTerritory());
        Commands.add(new SetTerritoryOwner());
        // Totems
        Commands.add(new SetTotemInTerritory());
        // Lobby
        Commands.add(new SetLobbySpawnPoint());
        Commands.add(new SpawnInLobby());


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {


            System.out.println("[Custom Command Manager] Found " + Commands.size() + " commands");
            for (RegistrableCommand command : Commands) {
                System.out.println("[Custom Command Manager] Register command: " + command.getClass().getName());
                command.Initialize(dispatcher, registryAccess, environment);
            }
        });
    }

    public interface RegistrableCommand {
        public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment);
    }

}
