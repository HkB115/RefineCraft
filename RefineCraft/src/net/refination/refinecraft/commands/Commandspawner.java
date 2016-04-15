package net.refination.refinecraft.commands;

import net.refination.refinecraft.Mob;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.LocationUtil;
import net.refination.refinecraft.utils.NumberUtil;
import net.refination.refinecraft.utils.StringUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.CreatureSpawner;

import java.util.Locale;

import static net.refination.refinecraft.I18n.tl;


public class Commandspawner extends RefineCraftCommand {
    public Commandspawner() {
        super("spawner");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1 || args[0].length() < 2) {
            throw new NotEnoughArgumentsException(tl("mobsAvailable", StringUtil.joinList(Mob.getMobList())));
        }

        final Location target = LocationUtil.getTarget(user.getBase());
        if (target == null || target.getBlock().getType() != Material.MOB_SPAWNER) {
            throw new Exception(tl("mobSpawnTarget"));
        }

        String name = args[0];
        int delay = 0;

        Mob mob = null;
        mob = Mob.fromName(name);
        if (mob == null) {
            throw new Exception(tl("invalidMob"));
        }
        if (RC.getSettings().getProtectPreventSpawn(mob.getType().toString().toLowerCase(Locale.ENGLISH))) {
            throw new Exception(tl("disabledToSpawnMob"));
        }
        if (!user.isAuthorized("refinecraft.spawner." + mob.name.toLowerCase(Locale.ENGLISH))) {
            throw new Exception(tl("noPermToSpawnMob"));
        }
        if (args.length > 1) {
            if (NumberUtil.isInt(args[1])) {
                delay = Integer.parseInt(args[1]);
            }
        }
        final Trade charge = new Trade("spawner-" + mob.name.toLowerCase(Locale.ENGLISH), RC);
        charge.isAffordableFor(user);
        try {
            CreatureSpawner spawner = (CreatureSpawner) target.getBlock().getState();
            spawner.setSpawnedType(mob.getType());
            spawner.setDelay(delay);
            spawner.update();
        } catch (Throwable ex) {
            throw new Exception(tl("mobSpawnError"), ex);
        }
        charge.charge(user);
        user.sendMessage(tl("setSpawner", mob.name));

    }
}
