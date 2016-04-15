package net.refination.refinecraft.chat;

import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;


public class ChatStore {
    private final User user;
    private final String type;
    private final Trade charge;
    private long radius;

    ChatStore(final InterfaceRefineCraft RC, final User user, final String type) {
        this.user = user;
        this.type = type;
        this.charge = new Trade(getLongType(), RC);
    }

    public User getUser() {
        return user;
    }

    public Trade getCharge() {
        return charge;
    }

    public String getType() {
        return type;
    }

    public final String getLongType() {
        return type.length() == 0 ? "chat" : "chat-" + type;
    }

    public long getRadius() {
        return radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }
}
