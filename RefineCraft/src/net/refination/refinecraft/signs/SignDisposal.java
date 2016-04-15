package net.refination.refinecraft.signs;

import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;


public class SignDisposal extends RefineCraftSign {
    public SignDisposal() {
        super("Disposal");
    }

    @Override
    protected boolean onSignInteract(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) {
        player.getBase().openInventory(RC.getServer().createInventory(player.getBase(), 36, "Disposal"));
        return true;
    }
}
