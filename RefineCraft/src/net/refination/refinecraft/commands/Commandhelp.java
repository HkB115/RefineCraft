package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.refinecraft.textreader.*;
import net.refination.refinecraft.utils.NumberUtil;
import org.bukkit.Server;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandhelp extends RefineCraftCommand {
    public Commandhelp() {
        super("help");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        InterfaceText output;
        String pageStr = args.length > 0 ? args[0] : null;
        String chapterPageStr = args.length > 1 ? args[1] : null;
        String command = commandLabel;
        final InterfaceText input = new TextInput(user.getSource(), "help", false, RC);

        if (input.getLines().isEmpty()) {
            if (NumberUtil.isInt(pageStr) || pageStr == null) {
                output = new HelpInput(user, "", RC);
            } else {
                if (pageStr.length() > 26) {
                    pageStr = pageStr.substring(0, 25);
                }
                output = new HelpInput(user, pageStr.toLowerCase(Locale.ENGLISH), RC);
                command = command.concat(" ").concat(pageStr);
                pageStr = chapterPageStr;
            }
            chapterPageStr = null;
        } else {
            user.setDisplayNick();
            output = new KeywordReplacer(input, user.getSource(), RC);
        }
        final TextPager pager = new TextPager(output);
        pager.showPage(pageStr, chapterPageStr, command, user.getSource());
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sender.sendMessage(tl("helpConsole"));
    }
}
