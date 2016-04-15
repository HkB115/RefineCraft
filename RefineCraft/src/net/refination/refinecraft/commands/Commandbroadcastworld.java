package net.refination.refinecraft.commands;

import static net.refination.refinecraft.I18n.tl;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.refinecraft.textreader.InterfaceText;
import net.refination.refinecraft.textreader.KeywordReplacer;
import net.refination.refinecraft.textreader.SimpleTextInput;
import net.refination.refinecraft.utils.FormatUtil;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;


public class Commandbroadcastworld extends RefineCraftCommand {

    public Commandbroadcastworld() {
        super("broadcastworld");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        sendBroadcast(user.getWorld().getName(), user.getDisplayName(), getFinalArg(args, 0));
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException("world");
        }
        sendBroadcast(args[0], sender.getSender().getName(), getFinalArg(args, 1));
    }

    private void sendBroadcast(final String worldName, final String name, final String message) throws Exception {
        World world = RC.getWorld(worldName);
        if (world == null) {
            throw new Exception(tl("invalidWorld"));
        }
        sendToWorld(world, tl("broadcast", FormatUtil.replaceFormat(message).replace("\\n", "\n"), name));
    }

    private void sendToWorld(World world, String message) {
        InterfaceText broadcast = new SimpleTextInput(message);
        final Collection<Player> players = RC.getOnlinePlayers();

        for (Player player : players) {
            if (player.getWorld().equals(world)) {
                final User user = RC.getUser(player);
                broadcast = new KeywordReplacer(broadcast, new CommandSource(player), RC, false);
                for (String messageText : broadcast.getLines()) {
                    user.sendMessage(messageText);
                }
            }
        }
    }
}
