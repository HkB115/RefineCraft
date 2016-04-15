package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.utils.FormatUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import static net.refination.refinecraft.I18n.tl;

public class Commandgkick extends RefineCraftCommand {
    public Commandgkick() {
        super("gkick");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        String kickReason = args.length > 0 ? getFinalArg(args, 0) : tl("kickDefault");
        kickReason = FormatUtil.replaceFormat(kickReason.replace("\\n", "\n").replace("|", "\n"));

        for (Player onlinePlayer : RC.getOnlinePlayers()) {
            if (!sender.isPlayer() || !onlinePlayer.getName().equalsIgnoreCase(sender.getPlayer().getName())) {
                onlinePlayer.kickPlayer(kickReason);
            }
        }
        sender.sendMessage(tl("kickedAll"));
    }
}
