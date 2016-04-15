package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.textreader.InterfaceText;
import net.refination.refinecraft.textreader.KeywordReplacer;
import net.refination.refinecraft.textreader.TextInput;
import net.refination.refinecraft.textreader.TextPager;
import org.bukkit.Server;


public class Commandinfo extends RefineCraftCommand {
    public Commandinfo() {
        super("info");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (sender.isPlayer()) {
            RC.getUser(sender.getPlayer()).setDisplayNick();
        }

        final InterfaceText input = new TextInput(sender, "info", true, RC);
        final InterfaceText output = new KeywordReplacer(input, sender, RC);
        final TextPager pager = new TextPager(output);
        pager.showPage(args.length > 0 ? args[0] : null, args.length > 1 ? args[1] : null, commandLabel, sender);
    }
}
