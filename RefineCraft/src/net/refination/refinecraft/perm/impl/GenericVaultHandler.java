package net.refination.refinecraft.perm.impl;

public class GenericVaultHandler extends AbstractVaultHandler {
    @Override
    public boolean tryProvider() {
        return super.canLoad();
    }
}
