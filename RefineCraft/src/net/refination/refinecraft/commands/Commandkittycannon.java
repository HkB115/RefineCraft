package net.refination.refinecraft.commands;

import net.refination.refinecraft.Mob;
import net.refination.refinecraft.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Ocelot;

import java.util.Random;

// This command is not documented on the wiki #EasterEgg
public class Commandkittycannon extends RefineCraftCommand {
    private static final Random random = new Random();

    public Commandkittycannon() {
        super("kittycannon");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Mob cat = Mob.OCELOT;
        final Ocelot ocelot = (Ocelot) cat.spawn(user.getWorld(), server, user.getBase().getEyeLocation());
        if (ocelot == null) {
            return;
        }
        final int i = random.nextInt(Ocelot.Type.values().length);
        ocelot.setCatType(Ocelot.Type.values()[i]);
        ocelot.setTamed(true);
        ocelot.setBaby();
        ocelot.setVelocity(user.getBase().getEyeLocation().getDirection().multiply(2));

        class KittyCannonExplodeTask implements Runnable {
            @Override
            public void run() {
                final Location loc = ocelot.getLocation();
                ocelot.remove();
                loc.getWorld().createExplosion(loc, 0F);
            }
        }
        RC.scheduleSyncDelayedTask(new KittyCannonExplodeTask(), 20);

    }
}
