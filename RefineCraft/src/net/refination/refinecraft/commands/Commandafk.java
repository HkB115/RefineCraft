package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandafk extends RefineCraftCommand {
    public Commandafk() {
        super("afk");
    }

    @Override
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        if (args.length > 0 && user.isAuthorized("refinecraft.afk.others")) {
            User afkUser = getPlayer(server, user, args, 0);
            toggleAfk(afkUser);
        } else {
            toggleAfk(user);
        }
    }

    @Override
    public void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length > 0) {
            User afkUser = getPlayer(server, args, 0, true, false);
            toggleAfk(afkUser);
        } else {
            throw new NotEnoughArgumentsException();
        }
    }

    private void toggleAfk(User user) {
        user.setDisplayNick();
        String msg = "";
        if (!user.toggleAfk()) {
            //user.sendMessage(_("markedAsNotAway"));
            if (!user.isHidden()) {
                msg = tl("userIsNotAway", user.getDisplayName());
            }
            user.updateActivity(false);
        } else {
            //user.sendMessage(_("markedAsAway"));
            if (!user.isHidden()) {
                msg = tl("userIsAway", user.getDisplayName());
            }
        }
        if (!msg.isEmpty()) {
            RC.broadcastMessage(user, msg);
        }
    }
}

