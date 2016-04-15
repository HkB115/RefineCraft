package net.refination.refinecraft.commands;

import net.refination.refinecraft.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashSet;

import static net.refination.refinecraft.I18n.tl;


public class Commandbreak extends RefineCraftCommand {
    public Commandbreak() {
        super("break");
    }

    //TODO: Switch to use util class
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Block block = user.getBase().getTargetBlock((HashSet<Byte>) null, 20);
        if (block == null) {
            throw new NoChargeException();
        }
        if (block.getType() == Material.AIR) {
            throw new NoChargeException();
        }
        if (block.getType() == Material.BEDROCK && !user.isAuthorized("refinecraft.break.bedrock")) {
            throw new Exception(tl("noBreakBedrock"));
        }
        //final List<ItemStack> list = (List<ItemStack>)block.getDrops();
        //final BlockBreakEvent event = new BlockBreakEvent(block, user.getBase(), list);
        final BlockBreakEvent event = new BlockBreakEvent(block, user.getBase());
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            throw new NoChargeException();
        } else {
            block.setType(Material.AIR);
        }
    }
}