package net.refination.api;

import static net.refination.refinecraft.I18n.tl;

public class NoLoanPermittedException extends Exception {
    public NoLoanPermittedException() {
        super(tl("negativeBalanceError"));
    }
}
