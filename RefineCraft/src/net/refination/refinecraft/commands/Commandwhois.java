package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.refinecraft.craftbukkit.SetExpFix;
import net.refination.refinecraft.utils.DateUtil;
import net.refination.refinecraft.utils.NumberUtil;
import org.bukkit.Server;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandwhois extends RefineCraftCommand {
    public Commandwhois() {
        super("whois");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        User user = getPlayer(server, sender, args, 0);

        sender.sendMessage(tl("whoisTop", user.getName()));
        user.setDisplayNick();
        sender.sendMessage(tl("whoisNick", user.getDisplayName()));
        sender.sendMessage(tl("whoisHealth", user.getBase().getHealth()));
        sender.sendMessage(tl("whoisHunger", user.getBase().getFoodLevel(), user.getBase().getSaturation()));
        sender.sendMessage(tl("whoisExp", SetExpFix.getTotalExperience(user.getBase()), user.getBase().getLevel()));
        sender.sendMessage(tl("whoisLocation", user.getLocation().getWorld().getName(), user.getLocation().getBlockX(), user.getLocation().getBlockY(), user.getLocation().getBlockZ()));
        if (!RC.getSettings().isEcoDisabled()) {
            sender.sendMessage(tl("whoisMoney", NumberUtil.displayCurrency(user.getMoney(), RC)));
        }
        sender.sendMessage(tl("whoisIPAddress", user.getBase().getAddress().getAddress().toString()));
        final String location = user.getGeoLocation();
        if (location != null && (!sender.isPlayer() || RC.getUser(sender.getPlayer()).isAuthorized("refinecraft.geoip.show"))) {
            sender.sendMessage(tl("whoisGeoLocation", location));
        }
        sender.sendMessage(tl("whoisGamemode", tl(user.getBase().getGameMode().toString().toLowerCase(Locale.ENGLISH))));
        sender.sendMessage(tl("whoisGod", (user.isGodModeEnabled() ? tl("true") : tl("false"))));
        sender.sendMessage(tl("whoisOp", (user.getBase().isOp() ? tl("true") : tl("false"))));
        sender.sendMessage(tl("whoisFly", user.getBase().getAllowFlight() ? tl("true") : tl("false"), user.getBase().isFlying() ? tl("flying") : tl("notFlying")));
        sender.sendMessage(tl("whoisAFK", (user.isAfk() ? tl("true") : tl("false"))));
        sender.sendMessage(tl("whoisJail", (user.isJailed() ? user.getJailTimeout() > 0 ? DateUtil.formatDateDiff(user.getJailTimeout()) : tl("true") : tl("false"))));
        sender.sendMessage(tl("whoisMuted", (user.isMuted() ? user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : tl("true") : tl("false"))));

    }
}
