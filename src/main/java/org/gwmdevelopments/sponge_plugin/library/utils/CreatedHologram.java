package org.gwmdevelopments.sponge_plugin.library.utils;

import de.randombyte.holograms.api.HologramsService;
import org.spongepowered.api.world.ChunkTicketManager;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

public class CreatedHologram {

    private final List<HologramsService.Hologram> holograms;
    private final Location<World> cachedLocation;
    private final Optional<ChunkTicketManager.LoadingTicket> usedTicket;

    public CreatedHologram(List<HologramsService.Hologram> holograms, Location<World> cachedLocation,
                           Optional<ChunkTicketManager.LoadingTicket> usedTicket) {
        this.holograms = holograms;
        this.cachedLocation = cachedLocation;
        this.usedTicket = usedTicket;
    }

    public List<HologramsService.Hologram> getHolograms() {
        return holograms;
    }

    public Location<World> getCachedLocation() {
        return cachedLocation;
    }

    public Optional<ChunkTicketManager.LoadingTicket> getUsedTicket() {
        return usedTicket;
    }
}
