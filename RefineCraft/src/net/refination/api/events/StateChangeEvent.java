package net.refination.api.events;

import net.refination.api.InterfaceUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This handles common boilerplate for other StateChangeEvents
 */
public class StateChangeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    InterfaceUser affected;
    InterfaceUser controller;

    public StateChangeEvent(InterfaceUser affected, InterfaceUser controller) {
        super();
        this.affected = affected;
        this.controller = controller;
    }

    public StateChangeEvent(boolean isAsync, InterfaceUser affected, InterfaceUser controller) {
        super(isAsync);
        this.affected = affected;
        this.controller = controller;
    }

    public InterfaceUser getAffected() {
        return this.affected;
    }

    public InterfaceUser getController() {
        return controller;
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
