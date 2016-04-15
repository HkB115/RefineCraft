package net.refination.refinecraft.commands;

import net.refination.refinecraft.Kit;
import net.refination.refinecraft.User;
import org.bukkit.Server;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;

public class Commandshowkit extends RefineCraftCommand {

    public Commandshowkit() {
        super("showkit");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length != 1) {
            throw new NotEnoughArgumentsException();
        }

        final String[] kits = args[0].toLowerCase(Locale.ENGLISH).split(",");
        for (final String kitName : kits) {
            Kit kit = new Kit(kitName, RC);
            user.sendMessage(tl("kitContains", kitName));
            for (String s : kit.getItems()) {
                user.sendMessage(tl("kitItem", s));
            }
        }
    }
}
