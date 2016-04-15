package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.textreader.InterfaceText;
import net.refination.refinecraft.textreader.KeywordReplacer;
import net.refination.refinecraft.textreader.TextInput;
import net.refination.refinecraft.textreader.TextPager;
import net.refination.refinecraft.utils.NumberUtil;
import org.bukkit.Server;


public class Commandcustomtext extends RefineCraftCommand {
    public Commandcustomtext() {
        super("customtext");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (sender.isPlayer()) {
            RC.getUser(sender.getPlayer()).setDisplayNick();
        }

        final InterfaceText input = new TextInput(sender, "custom", true, RC);
        final InterfaceText output = new KeywordReplacer(input, sender, RC);
        final TextPager pager = new TextPager(output);
        String chapter = commandLabel;
        String page;

        if (commandLabel.equalsIgnoreCase("customtext") && args.length > 0 && !NumberUtil.isInt(commandLabel)) {
            chapter = args[0];
            page = args.length > 1 ? args[1] : null;
        } else {
            page = args.length > 0 ? args[0] : null;
        }

        pager.showPage(chapter, page, null, sender);
    }
}
