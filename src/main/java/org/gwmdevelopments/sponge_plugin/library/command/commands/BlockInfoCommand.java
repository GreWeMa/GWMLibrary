package org.gwmdevelopments.sponge_plugin.library.command.commands;

import org.gwmdevelopments.sponge_plugin.library.GWMLibrary;
import org.gwmdevelopments.sponge_plugin.library.utils.Pair;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

public class BlockInfoCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessages(GWMLibrary.getInstance().getLanguage().getText("COMMAND_USABLE_ONLY_BY_PLAYER"));
            return CommandResult.success();
        }
        Player player = (Player) src;
        BlockRay<World> blockRay = BlockRay.from(player).
                stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).
                build();
        BlockRayHit<World> blockRayHit = blockRay.end().get();
        BlockState block = blockRayHit.getLocation().getBlock();
        src.sendMessage(GWMLibrary.getInstance().getLanguage().getText("BLOCK_INFO",
                new Pair<>("%BLOCK%", block)));
        return CommandResult.success();
    }
}
