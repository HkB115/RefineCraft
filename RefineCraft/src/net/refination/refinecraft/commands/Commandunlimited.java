package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandunlimited extends RefineCraftCommand {
    public Commandunlimited() {
        super("unlimited");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        User target = user;

        if (args.length > 1 && user.isAuthorized("refinecraft.unlimited.others")) {
            target = getPlayer(server, user, args, 1);
        }

        if (args[0].equalsIgnoreCase("list")) {
            final String list = getList(target);
            user.sendMessage(list);
        } else if (args[0].equalsIgnoreCase("clear")) {
            final List<Integer> itemList = target.getUnlimited();

            int index = 0;
            while (itemList.size() > index) {
                final Integer item = itemList.get(index);
                if (toggleUnlimited(user, target, item.toString()) == false) {
                    index++;
                }
            }
        } else {
            toggleUnlimited(user, target, args[0]);
        }
    }

    private String getList(final User target) {
        final StringBuilder output = new StringBuilder();
        output.append(tl("unlimitedItems")).append(" ");
        boolean first = true;
        final List<Integer> items = target.getUnlimited();
        if (items.isEmpty()) {
            output.append(tl("none"));
        }
        for (Integer integer : items) {
            if (!first) {
                output.append(", ");
            }
            first = false;
            final String matname = Material.getMaterial(integer).toString().toLowerCase(Locale.ENGLISH).replace("_", "");
            output.append(matname);
        }

        return output.toString();
    }

    private Boolean toggleUnlimited(final User user, final User target, final String item) throws Exception {
        final ItemStack stack = RC.getItemDb().get(item, 1);
        stack.setAmount(Math.min(stack.getType().getMaxStackSize(), 2));

        final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
        if (RC.getSettings().permissionBasedItemSpawn() && (!user.isAuthorized("refinecraft.unlimited.item-all") && !user.isAuthorized("refinecraft.unlimited.item-" + itemname) && !user.isAuthorized("refinecraft.unlimited.item-" + stack.getTypeId()) && !((stack.getType() == Material.WATER_BUCKET || stack.getType() == Material.LAVA_BUCKET) && user.isAuthorized("refinecraft.unlimited.item-bucket")))) {
            throw new Exception(tl("unlimitedItemPermission", itemname));
        }

        String message = "disableUnlimited";
        boolean enableUnlimited = false;
        if (!target.hasUnlimited(stack)) {
            message = "enableUnlimited";
            enableUnlimited = true;
            if (!target.getBase().getInventory().containsAtLeast(stack, stack.getAmount())) {
                target.getBase().getInventory().addItem(stack);
            }
        }

        if (user != target) {
            user.sendMessage(tl(message, itemname, target.getDisplayName()));
        }
        target.sendMessage(tl(message, itemname, target.getDisplayName()));
        target.setUnlimited(stack, enableUnlimited);

        return true;
    }
}
