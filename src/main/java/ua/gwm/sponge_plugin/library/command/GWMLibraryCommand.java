package ua.gwm.sponge_plugin.library.command;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import ua.gwm.sponge_plugin.library.GWMLibrary;
import ua.gwm.sponge_plugin.library.utils.Pair;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GWMLibraryCommand implements CommandCallable {

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        String[] args = arguments.equals("") ?
                new String[0] :
                arguments.split(" ");
        Optional<Player> optional_player = source instanceof Player ? Optional.of((Player) source) : Optional.empty();
        if (args.length == 0) {
            sendHelp(source);
            return CommandResult.empty();
        }
        switch (args[0].toLowerCase()) {
            case "help": {
                sendHelp(source);
                return CommandResult.success();
            }
            case "reload": {
                if (!source.hasPermission("gwm_library.command.reload")) {
                    source.sendMessage(GWMLibrary.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                GWMLibrary.getInstance().reload();
                source.sendMessage(GWMLibrary.getInstance().getLanguage().getText("SUCCESSFULLY_RELOADED"));
                return CommandResult.success();
            }
            case "save": {
                if (!source.hasPermission("gwm_library.command.save")) {
                    source.sendMessage(GWMLibrary.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                GWMLibrary.getInstance().save();
                source.sendMessage(GWMLibrary.getInstance().getLanguage().getText("SUCCESSFULLY_SAVED"));
                return CommandResult.success();
            }
            case "block-info": {
                if (!optional_player.isPresent()) {
                    source.sendMessages(GWMLibrary.getInstance().getLanguage().getText("COMMAND_USABLE_ONLY_BY_PLAYER"));
                    return CommandResult.success();
                }
                Player player = optional_player.get();
                if (!player.hasPermission("gwm_library.command.block_info")) {
                    player.sendMessages(GWMLibrary.getInstance().getLanguage().getText("HAVE_NOT_PERMISSION"));
                    return CommandResult.success();
                }
                BlockRay<World> block_ray = BlockRay.from(player).
                        stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).
                        build();
                BlockRayHit<World> block_ray_hit = block_ray.end().get();
                BlockState block = block_ray_hit.getLocation().getBlock();
                source.sendMessage(GWMLibrary.getInstance().getLanguage().getText("BLOCK_INFO",
                        new Pair<>("%BLOCK_INFO%", block)));
                return CommandResult.success();
            }
            default: {
                sendHelp(source);
                return CommandResult.empty();
            }
        }
    }

    private void sendHelp(CommandSource source) {
        source.sendMessages(GWMLibrary.getInstance().getLanguage().getTextList("HELP_MESSAGE",
                new Pair<>("%VERSION%", GWMLibrary.VERSION.toString())));
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return Collections.emptyList();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of();
    }
}
