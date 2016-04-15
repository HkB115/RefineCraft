package net.refination.refinecraft.commands;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.api.MaxMoneyException;
import org.bukkit.Server;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandsudo extends RefineCraftLoopCommand {
    public Commandsudo() {
        super("sudo");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final String[] arguments = new String[args.length - 1];
        if (arguments.length > 0) {
            System.arraycopy(args, 1, arguments, 0, args.length - 1);
        }

        final String command = getFinalArg(arguments, 0);
        boolean multiple = sender.getSender().hasPermission("refinecraft.sudo.multiple");

        sender.sendMessage(tl("sudoRun", args[0], command, ""));
        loopOnlinePlayers(server, sender, multiple, multiple, args[0], new String[]{command});
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User user, String[] args) throws NotEnoughArgumentsException, PlayerExemptException, ChargeException, MaxMoneyException {
        if (user.getName().equals(sender.getSender().getName())) {
            return; // Silently don't do anything.
        }

        if (user.isAuthorized("refinecraft.sudo.exempt") && sender.isPlayer()) {
            sender.sendMessage(tl("sudoExempt", user.getName()));
            return;
        }

        if (args[0].toLowerCase(Locale.ENGLISH).startsWith("c:")) {
            user.getBase().chat(getFinalArg(args, 0).substring(2));
            return;
        }

        final String command = getFinalArg(args, 0);
        if (command != null && command.length() > 0) {
            class SudoCommandTask implements Runnable {
                @Override
                public void run() {
                    try {
                        RC.getServer().dispatchCommand(user.getBase(), command);
                    } catch (Exception e) {
                        sender.sendMessage(tl("errorCallingCommand", command));
                    }
                }
            }
            RC.scheduleSyncDelayedTask(new SudoCommandTask());
        }
    }
}
