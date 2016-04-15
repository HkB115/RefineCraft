package net.refination.refinecraft.signs;

//This enum is used when checking to see what signs are enabled
public enum Signs {
    BALANCE(new SignBalance()),
    BUY(new SignBuy()),
    DISPOSAL(new SignDisposal()),
    ENCHANT(new SignEnchant()),
    FREE(new SignFree()),
    GAMEMODE(new SignGameMode()),
    HEAL(new SignHeal()),
    INFO(new SignInfo()),
    KIT(new SignKit()),
    MAIL(new SignMail()),
    PROTECTION(new SignProtection()),
    REPAIR(new SignRepair()),
    SELL(new SignSell()),
    SPAWNMOB(new SignSpawnmob()),
    TIME(new SignTime()),
    TRADE(new SignTrade()),
    WARP(new SignWarp()),
    WEATHER(new SignWeather());
    private final RefineCraftSign sign;

    Signs(final RefineCraftSign sign) {
        this.sign = sign;
    }

    public RefineCraftSign getSign() {
        return sign;
    }
}
