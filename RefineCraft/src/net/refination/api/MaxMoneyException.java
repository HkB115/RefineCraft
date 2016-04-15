package net.refination.api;

import static net.refination.refinecraft.I18n.tl;

public class MaxMoneyException extends Exception {
    public MaxMoneyException() {
        super(tl("maxMoney"));
    }
}
