package net.refination.api.events;

import net.refination.refinecraft.signs.RefineCraftSign;
import net.refination.refinecraft.signs.RefineCraftSign.InterfaceSign;
import net.refination.api.InterfaceUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This handles common boilerplate for other SignEvent
 */
public class SignEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    InterfaceSign sign;
    RefineCraftSign RCSign;
    InterfaceUser user;

    public SignEvent(final InterfaceSign sign, final RefineCraftSign RCSign, final InterfaceUser user) {
        super();
        this.sign = sign;
        this.RCSign = RCSign;
        this.user = user;
    }

    public InterfaceSign getSign() {
        return sign;
    }

    public RefineCraftSign getRefineCraftSign() {
        return RCSign;
    }

    public InterfaceUser getUser() {
        return user;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
