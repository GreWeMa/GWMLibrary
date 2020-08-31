package dev.gwm.spongeplugin.library.command;

import dev.gwm.spongeplugin.library.GWMLibrary;
import dev.gwm.spongeplugin.library.superobject.Giveable;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.Language;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class BuyCommand implements CommandExecutor {

    private final Language language;
    private final Cause cause;

    public BuyCommand(Language language, Cause cause) {
        this.language = language;
        this.cause = cause;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) {
        if (!(source instanceof Player)) {
            source.sendMessages(language.getTranslation("COMMAND_EXECUTABLE_ONLY_BY_PLAYER", source));
            return CommandResult.empty();
        }
        Player player = (Player) source;
        UUID uuid = player.getUniqueId();
        SuperObject superObject = args.<SuperObject>getOne(Text.of("super-object")).get();
        String superObjectId = superObject.id();
        Giveable giveable = (Giveable) superObject;
        int amount = args.<Integer>getOne(Text.of("amount")).orElse(1);
        if (amount < 1) {
            player.sendMessages(language.getTranslation("BUY_AMOUNT_IS_LESS_THAN_ONE", player));
            return CommandResult.empty();
        }
        if (!player.hasPermission("gwm_library.command.buy." + superObjectId)) {
            player.sendMessages(language.getTranslation("HAVE_NOT_PERMISSION", player));
            return CommandResult.empty();
        }
        Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
        if (!optionalEconomyService.isPresent()) {
            player.sendMessages(language.getTranslation("ECONOMY_SERVICE_IS_NOT_FOUND", player));
            return CommandResult.empty();
        }
        EconomyService economyService = optionalEconomyService.get();
        Optional<UniqueAccount> optionalPlayerAccount = economyService.getOrCreateAccount(uuid);
        if (!optionalPlayerAccount.isPresent()) {
            player.sendMessages(language.getTranslation("ECONOMY_ACCOUNT_IS_NOT_FOUND", player));
            return CommandResult.empty();
        }
        UniqueAccount playerAccount = optionalPlayerAccount.get();
        Optional<BigDecimal> optionalPrice = giveable.getPrice();
        if (!optionalPrice.isPresent()) {
            player.sendMessages(language.getTranslation("SAVED_SUPER_OBJECT_IS_NOT_FOR_SALE",
                    new ImmutablePair<>("SUPER_OBJECT_ID", superObjectId),
                    player));
            return CommandResult.empty();
        }
        BigDecimal price = optionalPrice.get();
        BigDecimal totalPrice = price.multiply(new BigDecimal(String.valueOf(amount)));
        Currency currency = giveable.getSaleCurrency().orElse(economyService.getDefaultCurrency());
        BigDecimal balance = playerAccount.getBalance(currency);
        if (balance.compareTo(totalPrice) < 0) {
            player.sendMessages(language.getTranslation("HAVE_NOT_ENOUGH_MONEY", Arrays.asList(
                    new ImmutablePair<>("CURRENCY_ID", currency.getId()),
                    new ImmutablePair<>("CURRENCY_NAME", currency.getName()),
                    new ImmutablePair<>("CURRENCY_DISPLAY_NAME", TextSerializers.FORMATTING_CODE.serialize(currency.getDisplayName())),
                    new ImmutablePair<>("CURRENCY_SYMBOL", TextSerializers.FORMATTING_CODE.serialize(currency.getSymbol())),
                    new ImmutablePair<>("REQUIRED_AMOUNT", totalPrice),
                    new ImmutablePair<>("BALANCE", balance),
                    new ImmutablePair<>("DIFFERENCE", totalPrice.subtract(balance))
            ), player));
            return CommandResult.empty();
        }
        playerAccount.withdraw(currency, totalPrice, cause);
        giveable.give(player, amount, false);
        player.sendMessages(language.getTranslation("SUCCESSFUL_PURCHASE_OF_SAVED_SUPER_OBJECT",
                new ImmutablePair<>("SUPER_OBJECT_ID", superObjectId),
                player));
        return CommandResult.success();
    }
}
