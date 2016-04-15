package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Kit;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.StringUtil;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import static net.refination.refinecraft.I18n.tl;


public class Commandkit extends RefineCraftCommand {
    public Commandkit() {
        super("kit");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            final String kitList = Kit.listKits(RC, user);
            user.sendMessage(kitList.length() > 0 ? tl("kits", kitList) : tl("noKits"));
            throw new NoChargeException();
        } else if (args.length > 1 && user.isAuthorized("refinecraft.kit.others")) {
            final User userTo = getPlayer(server, user, args, 1);
            final String kitNames = StringUtil.sanitizeString(args[0].toLowerCase(Locale.ENGLISH)).trim();
            giveKits(userTo, user, kitNames);
        } else {
            final String kitNames = StringUtil.sanitizeString(args[0].toLowerCase(Locale.ENGLISH)).trim();
            giveKits(user, user, kitNames);
        }
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            final String kitList = Kit.listKits(RC, null);
            sender.sendMessage(kitList.length() > 0 ? tl("kits", kitList) : tl("noKits"));
            throw new NoChargeException();
        } else {
            final User userTo = getPlayer(server, args, 1, true, false);
            final String[] kits = args[0].toLowerCase(Locale.ENGLISH).split(",");

            for (final String kitName : kits) {
                final Kit kit = new Kit(kitName, RC);
                kit.expandItems(userTo);

                sender.sendMessage(tl("kitGiveTo", kitName, userTo.getDisplayName()));
                userTo.sendMessage(tl("kitReceive", kitName));
            }
        }
    }

    private void giveKits(final User userTo, final User userFrom, final String kitNames) throws Exception {
        if (kitNames.isEmpty()) {
            throw new Exception(tl("kitNotFound"));
        }
        String[] kitList = kitNames.split(",");

        List<Kit> kits = new ArrayList<Kit>();

        for (final String kitName : kitList) {
            if (kitName.isEmpty()) {
                throw new Exception(tl("kitNotFound"));
            }

            Kit kit = new Kit(kitName, RC);
            kit.checkPerms(userFrom);
            kit.checkDelay(userFrom);
            kit.checkAffordable(userFrom);
            kits.add(kit);
        }

        for (final Kit kit : kits) {
            try {

                kit.checkDelay(userFrom);
                kit.checkAffordable(userFrom);
                kit.setTime(userFrom);
                kit.expandItems(userTo);
                kit.chargeUser(userTo);

                if (!userFrom.equals(userTo)) {
                    userFrom.sendMessage(tl("kitGiveTo", kit.getName(), userTo.getDisplayName()));
                }

                userTo.sendMessage(tl("kitReceive", kit.getName()));

            } catch (NoChargeException ex) {
                if (RC.getSettings().isDebug()) {
                    RC.getLogger().log(Level.INFO, "Soft kit error, abort spawning " + kit.getName(), ex);
                }
            } catch (Exception ex) {
                RC.showError(userFrom.getSource(), ex, "\\ kit: " + kit.getName());
            }
        }
    }
}
