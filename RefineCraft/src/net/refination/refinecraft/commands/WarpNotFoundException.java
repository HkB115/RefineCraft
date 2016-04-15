package net.refination.refinecraft.commands;

import static net.refination.refinecraft.I18n.tl;

public class WarpNotFoundException extends Exception {
    public WarpNotFoundException() {
        super(tl("warpNotExist"));
    }

    public WarpNotFoundException(String message) {
        super(message);
    }
}
