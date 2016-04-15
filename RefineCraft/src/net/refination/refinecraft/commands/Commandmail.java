package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.refinecraft.textreader.InterfaceText;
import net.refination.refinecraft.textreader.SimpleTextInput;
import net.refination.refinecraft.textreader.TextPager;
import net.refination.refinecraft.utils.FormatUtil;
import net.refination.refinecraft.utils.StringUtil;
import org.bukkit.Server;

import java.util.List;
import java.util.UUID;

import static net.refination.refinecraft.I18n.tl;


public class Commandmail extends RefineCraftCommand {
    private static int mailsPerMinute = 0;
    private static long timestamp = 0;

    public Commandmail() {
        super("mail");
    }

    //TODO: Tidy this up / TL these errors.
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length >= 1 && "read".equalsIgnoreCase(args[0])) {
            final List<String> mail = user.getMails();
            if (mail.isEmpty()) {
                user.sendMessage(tl("noMail"));
                throw new NoChargeException();
            }

            InterfaceText input = new SimpleTextInput(mail);
            final TextPager pager = new TextPager(input);
            pager.showPage(args.length > 1 ? args[1] : null, null, commandLabel + " " + args[0], user.getSource());

            user.sendMessage(tl("mailClear"));
            return;
        }
        if (args.length >= 3 && "send".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("refinecraft.mail.send")) {
                throw new Exception(tl("noPerm", "refinecraft.mail.send"));
            }

            if (user.isMuted()) {
                throw new Exception(tl("voiceSilenced"));
            }

            User u = getPlayer(server, args[1], true, true);
            if (u == null) {
                throw new Exception(tl("playerNeverOnServer", args[1]));
            }

            final String mail = tl("mailFormat", user.getName(), StringUtil.sanitizeString(FormatUtil.stripFormat(getFinalArg(args, 2))));
            if (mail.length() > 1000) {
                throw new Exception(tl("mailTooLong"));
            }

            if (!u.isIgnoredPlayer(user)) {
                if (Math.abs(System.currentTimeMillis() - timestamp) > 60000) {
                    timestamp = System.currentTimeMillis();
                    mailsPerMinute = 0;
                }
                mailsPerMinute++;
                if (mailsPerMinute > RC.getSettings().getMailsPerMinute()) {
                    throw new Exception(tl("mailDelay", RC.getSettings().getMailsPerMinute()));
                }
                u.addMail(tl("mailMessage", mail));
            }

            user.sendMessage(tl("mailSentTo", u.getDisplayName(), u.getName()));
            user.sendMessage(mail);
            return;
        }
        if (args.length > 1 && "sendall".equalsIgnoreCase(args[0])) {
            if (!user.isAuthorized("refinecraft.mail.sendall")) {
                throw new Exception(tl("noPerm", "refinecraft.mail.sendall"));
            }
            RC.runTaskAsynchronously(new SendAll(tl("mailFormat", user.getName(), FormatUtil.stripFormat(getFinalArg(args, 1)))));
            user.sendMessage(tl("mailSent"));
            return;
        }
        if (args.length >= 1 && "clear".equalsIgnoreCase(args[0])) {
            user.setMails(null);
            user.sendMessage(tl("mailCleared"));
            return;
        }
        throw new NotEnoughArgumentsException();
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (args.length >= 1 && "read".equalsIgnoreCase(args[0])) {
            throw new Exception(tl("onlyPlayers", commandLabel + " read"));
        } else if (args.length >= 1 && "clear".equalsIgnoreCase(args[0])) {
            throw new Exception(tl("onlyPlayers", commandLabel + " clear"));
        } else if (args.length >= 3 && "send".equalsIgnoreCase(args[0])) {
            User u = getPlayer(server, args[1], true, true);
            if (u == null) {
                throw new Exception(tl("playerNeverOnServer", args[1]));
            }
            u.addMail(tl("mailFormat", "Server", getFinalArg(args, 2)));
            sender.sendMessage(tl("mailSent"));
            return;
        } else if (args.length >= 2 && "sendall".equalsIgnoreCase(args[0])) {
            RC.runTaskAsynchronously(new SendAll(tl("mailFormat", "Server", getFinalArg(args, 1))));
            sender.sendMessage(tl("mailSent"));
            return;
        } else if (args.length >= 2) {
            //allow sending from console without "send" argument, since it's the only thing the console can do
            User u = getPlayer(server, args[0], true, true);
            if (u == null) {
                throw new Exception(tl("playerNeverOnServer", args[0]));
            }
            u.addMail(tl("mailFormat", "Server", getFinalArg(args, 1)));
            sender.sendMessage(tl("mailSent"));
            return;
        }
        throw new NotEnoughArgumentsException();
    }


    private class SendAll implements Runnable {
        String message;

        public SendAll(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            for (UUID userid : RC.getUserMap().getAllUniqueUsers()) {
                User user = RC.getUserMap().getUser(userid);
                if (user != null) {
                    user.addMail(message);
                }
            }
        }
    }
}
