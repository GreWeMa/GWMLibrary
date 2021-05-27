package dev.gwm.spongeplugin.library.command;

import dev.gwm.spongeplugin.library.GWMLibrary;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public final class SaveItemCommand implements CommandExecutor {

    private final Language language;

    public SaveItemCommand(Language language) {
        this.language = language;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        if (!(source instanceof Player)) {
            source.sendMessages(language.getTranslation("COMMAND_CAN_BE_EXECUTED_ONLY_BY_PLAYER", source));
            return CommandResult.empty();
        }
        Player player = (Player) source;
        String path = args.<String>getOne(Text.of("path")).get();
        boolean saveNbt = args.hasAny("n");
        ItemStack item = player.getItemInHand(HandTypes.MAIN_HAND).
                orElse(player.getItemInHand(HandTypes.OFF_HAND).
                        orElse(null));
        if (item == null) {
            player.sendMessages(language.getTranslation("NO_ITEM_IN_HAND", player));
            return CommandResult.empty();
        }
        GWMLibraryUtils.writeItemStack(item, saveNbt, GWMLibrary.getInstance().getSavedItemsConfig().getNode(path));
        GWMLibrary.getInstance().getSavedItemsConfig().save();
        player.sendMessages(language.getTranslation("ITEM_SAVED_SUCCESSFULLY",
                new ImmutablePair<>("PATH", path),
                player));
        return CommandResult.success();
    }
}
