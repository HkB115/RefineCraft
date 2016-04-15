package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import static net.refination.refinecraft.I18n.tl;


public class Commanditemdb extends RefineCraftCommand {
    public Commanditemdb() {
        super("itemdb");
    }

    @Override
    protected void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception {
        ItemStack itemStack = null;
        boolean itemHeld = false;
        if (args.length < 1) {
            if (sender.isPlayer()) {
                itemHeld = true;
                itemStack = sender.getPlayer().getItemInHand();
            }
            if (itemStack == null) {
                throw new NotEnoughArgumentsException();
            }
        } else {
            itemStack = RC.getItemDb().get(args[0]);
        }
        sender.sendMessage(tl("itemType", itemStack.getType().toString(), itemStack.getTypeId() + ":" + Integer.toString(itemStack.getDurability())));

        if (itemHeld && itemStack.getType() != Material.AIR) {
            int maxuses = itemStack.getType().getMaxDurability();
            int durability = ((maxuses + 1) - itemStack.getDurability());
            if (maxuses != 0) {
                sender.sendMessage(tl("durability", Integer.toString(durability)));
            }
        }
        final String itemNameList = RC.getItemDb().names(itemStack);
        if (itemNameList != null) {
            sender.sendMessage(tl("itemNames", RC.getItemDb().names(itemStack)));
        }
    }
}
