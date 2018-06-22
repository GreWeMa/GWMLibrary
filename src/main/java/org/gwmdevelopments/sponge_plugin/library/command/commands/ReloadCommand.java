package org.gwmdevelopments.sponge_plugin.library.command.commands;

import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class ReloadCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        GWMLibrary.getInstance().reload();
        src.sendMessage(GWMLibrary.getInstance().getLanguage().getText("SUCCESSFULLY_RELOADED"));
        return CommandResult.success();
    }
}
