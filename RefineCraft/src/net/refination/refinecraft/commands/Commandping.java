package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.utils.FormatUtil;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;

// This command can be used to echo messages to the users screen, mostly useless but also an #EasterEgg
public class Commandping extends RefineCraftCommand {
    public Commandping() {
        super("ping");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {

            sender.sendMessage(tl("pong"));
        } else {
            sender.sendMessage(FormatUtil.replaceFormat(getFinalArg(args, 0)));
        }
    }
}
