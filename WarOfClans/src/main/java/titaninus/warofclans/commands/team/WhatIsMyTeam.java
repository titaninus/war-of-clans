package titaninus.warofclans.commands.team;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import titaninus.warofclans.commands.WOCCommandManager;
import titaninus.warofclans.core.TeamColor;
import titaninus.warofclans.core.WOCTeam;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WhatIsMyTeam implements WOCCommandManager.RegistrableCommand {
    @Override
    public void Initialize(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher
                .register(literal("whatIsMyTeam").executes(this::execute));
    }

    private int execute(CommandContext<ServerCommandSource> ctx) {
        var player = ctx.getSource().getPlayer();
        var team = WOCTeam.FindPlayerTeam(player);
        if (team == null) {
            ctx.getSource().sendMessage(Text.literal("You have no team"));
        } else {
            ctx.getSource().sendMessage(Text.literal("Your team is " + team.Color));
        }
        return 1;
    }

}
