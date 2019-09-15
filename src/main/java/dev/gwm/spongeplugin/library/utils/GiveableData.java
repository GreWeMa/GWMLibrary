package dev.gwm.spongeplugin.library.utils;

import dev.gwm.spongeplugin.library.GWMLibrary;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import java.math.BigDecimal;
import java.util.Optional;

public class GiveableData {

    private final Optional<Currency> saleCurrency;
    private final Optional<BigDecimal> price;

    public GiveableData(ConfigurationNode node) {
        ConfigurationNode priceNode = node.getNode("PRICE");
        ConfigurationNode saleCurrencyNode = node.getNode("SALE_CURRENCY");
        if (!saleCurrencyNode.isVirtual()) {
            String saleCurrencyId = saleCurrencyNode.getString();
            Optional<EconomyService> optionalEconomyService = GWMLibrary.getInstance().getEconomyService();
            if (!optionalEconomyService.isPresent()) {
                throw new IllegalArgumentException("Economy Service is not found, but parameter \"SALE_CURRENCY\" is set!");
            }
            saleCurrency = GWMLibraryUtils.getCurrencyById(optionalEconomyService.get(), saleCurrencyId);
            if (!saleCurrency.isPresent()) {
                throw new IllegalArgumentException("Currency \"" + saleCurrencyId + "\" is not found!");
            }
        } else {
            saleCurrency = Optional.empty();
        }
        if (!priceNode.isVirtual()) {
            price = Optional.of(new BigDecimal(priceNode.getString()));
        } else {
            price = Optional.empty();
        }
        if (price.isPresent() && price.get().compareTo(BigDecimal.ZERO) < 1) {
            throw new IllegalArgumentException("Price is equal to or less than 0!");
        }
    }

    public GiveableData(Optional<Currency> saleCurrency, Optional<BigDecimal> price) {
        this.saleCurrency = saleCurrency;
        if (price.isPresent() && price.get().compareTo(BigDecimal.ZERO) < 1) {
            throw new IllegalArgumentException("Price is equal to or less than 0!");
        }
        this.price = price;
    }

    public Optional<Currency> getSaleCurrency() {
        return saleCurrency;
    }

    public Optional<BigDecimal> getPrice() {
        return price;
    }
}
