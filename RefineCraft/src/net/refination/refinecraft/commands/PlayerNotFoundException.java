package net.refination.refinecraft.commands;

import static net.refination.refinecraft.I18n.tl;

public class PlayerNotFoundException extends NoSuchFieldException {
    public PlayerNotFoundException() {
        super(tl("playerNotFound"));
    }
}
