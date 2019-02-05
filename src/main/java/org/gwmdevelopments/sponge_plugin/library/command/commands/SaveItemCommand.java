package org.gwmdevelopments.sponge_plugin.library.command.commands;

import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;
import org.gwmdevelopments.sponge_plugin.library.utils.GWMLibraryUtils;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class SaveItemCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        if (!(src instanceof Player)) {
            src.sendMessage(GWMLibrary.getInstance().getLanguage().getText("COMMAND_CAN_BE_EXECUTED_ONLY_BY_PLAYER", src, null));
            return CommandResult.success();
        }
        Player player = (Player) src;
        String path = args.<String>getOne(Text.of("path")).get();
        boolean saveNbt = args.<Boolean>getOne(Text.of("save-nbt")).get();
        ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).
                orElse(player.getItemInHand(HandTypes.OFF_HAND).
                        orElse(null));
        if (item == null) {
            player.sendMessage(GWMLibrary.getInstance().getLanguage().getText("NO_ITEM_IN_HAND", src, null));
            return CommandResult.success();
        }
        GWMLibraryUtils.writeItemStack(item, saveNbt, GWMLibrary.getInstance().getSavedItemsConfig().getNode(path));
        GWMLibrary.getInstance().getSavedItemsConfig().save();
        player.sendMessage(GWMLibrary.getInstance().getLanguage().getText("ITEM_SAVED_SUCCESSFULLY", src, null,
                new Pair<>("%PATH%", path)));
        return CommandResult.success();
    }
}
