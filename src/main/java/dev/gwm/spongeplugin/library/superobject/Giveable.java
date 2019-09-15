package dev.gwm.spongeplugin.library.superobject;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.Optional;

public interface Giveable {

    void give(Player player, int amount, boolean force);

    default Optional<Currency> getSaleCurrency() {
        return Optional.empty();
    }

    default Optional<BigDecimal> getPrice() {
        return Optional.empty();
    }
}