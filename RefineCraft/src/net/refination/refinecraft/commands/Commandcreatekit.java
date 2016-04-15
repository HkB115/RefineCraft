package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.refination.refinecraft.I18n.tl;

public class Commandcreatekit extends RefineCraftCommand {

    public Commandcreatekit() {
        super("createkit");
    }

    // /createkit <name> <delay>
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length != 2) {
            throw new NotEnoughArgumentsException();
        }

        // Command handler will auto fail if this fails.
        long delay = Long.valueOf(args[1]);
        String kitname = args[0];
        ItemStack[] items = user.getBase().getInventory().getContents();
        List<String> list = new ArrayList<String>();
        for (ItemStack is : items) {
            if (is != null && is.getType() != null && is.getType() != Material.AIR) {
                String serialized = RC.getItemDb().serialize(is);
                list.add(serialized);
            }
        }

        RC.getSettings().addKit(kitname, list, delay);
        user.sendMessage(tl("createdKit", kitname, list.size(), delay));
    }
}
