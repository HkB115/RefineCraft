package net.refination.api.events;

import net.refination.refinecraft.signs.RefineCraftSign;
import net.refination.api.InterfaceUser;

public class SignBreakEvent extends SignEvent {
    public SignBreakEvent(RefineCraftSign.InterfaceSign sign, RefineCraftSign RCSign, InterfaceUser user) {
        super(sign, RCSign, user);
    }
}
