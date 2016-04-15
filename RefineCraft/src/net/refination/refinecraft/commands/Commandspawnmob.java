package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.Mob;
import net.refination.refinecraft.SpawnMob;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.StringUtil;
import org.bukkit.Server;

import java.util.List;

import static net.refination.refinecraft.I18n.tl;


public class Commandspawnmob extends RefineCraftCommand {
    public Commandspawnmob() {
        super("spawnmob");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            final String mobList = SpawnMob.mobList(user);
            throw new NotEnoughArgumentsException(tl("mobsAvailable", mobList));
        }

        List<String> mobParts = SpawnMob.mobParts(args[0]);
        List<String> mobData = SpawnMob.mobData(args[0]);

        int mobCount = 1;
        if (args.length >= 2) {
            mobCount = Integer.parseInt(args[1]);
        }

        if (mobParts.size() > 1 && !user.isAuthorized("refinecraft.spawnmob.stack")) {
            throw new Exception(tl("cannotStackMob"));
        }

        if (args.length >= 3) {
            final User target = getPlayer(RC.getServer(), user, args, 2);
            SpawnMob.spawnmob(RC, server, user.getSource(), target, mobParts, mobData, mobCount);
            return;
        }

        SpawnMob.spawnmob(RC, server, user, mobParts, mobData, mobCount);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 3) {
            final String mobList = StringUtil.joinList(Mob.getMobList());
            throw new NotEnoughArgumentsException(tl("mobsAvailable", mobList));
        }

        List<String> mobParts = SpawnMob.mobParts(args[0]);
        List<String> mobData = SpawnMob.mobData(args[0]);
        int mobCount = Integer.parseInt(args[1]);

        final User target = getPlayer(RC.getServer(), args, 2, true, false);
        SpawnMob.spawnmob(RC, server, sender, target, mobParts, mobData, mobCount);
    }
}
