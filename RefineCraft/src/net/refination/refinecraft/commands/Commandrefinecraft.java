package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.RefineCraftUpgrade;
import net.refination.refinecraft.User;
import net.refination.refinecraft.UserMap;
import net.refination.refinecraft.metrics.Metrics;
import net.refination.refinecraft.utils.DateUtil;
import net.refination.refinecraft.utils.FloatUtil;
import net.refination.refinecraft.utils.NumberUtil;
import com.google.common.base.Charsets;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static net.refination.refinecraft.I18n.tl;

// This command has 4 undocumented behaviours #EasterEgg
public class Commandrefinecraft extends RefineCraftCommand {
    public Commandrefinecraft() {
        super("refinecraft");
    }

    private transient int taskid;
    private final transient Map<Player, Block> noteBlocks = new HashMap<Player, Block>();

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            run_disabled(server, sender, commandLabel, args);
        } else if (args[0].equalsIgnoreCase("debug")) {
            run_debug(server, sender, commandLabel, args);
        } else if (args[0].equalsIgnoreCase("nya")) {
            run_nya(server, sender, commandLabel, args);
        } else if (args[0].equalsIgnoreCase("moo")) {
            run_moo(server, sender, commandLabel, args);
        } else if (args[0].equalsIgnoreCase("reset")) {
            run_reset(server, sender, commandLabel, args);
        } else if (args[0].equalsIgnoreCase("opt-out")) {
            run_optout(server, sender, commandLabel, args);
        } else if (args[0].equalsIgnoreCase("cleanup")) {
            run_cleanup(server, sender, commandLabel, args);
        } else if (args[0].equalsIgnoreCase("uuidconvert")) {
            run_uuidconvert(server, sender, commandLabel, args);
        } else if (args[0].equalsIgnoreCase("uuidtest")) {
            run_uuidtest(server, sender, commandLabel, args);
        } else {
            run_reload(server, sender, commandLabel, args);
        }
    }

    //If you do not supply an argument this command will list 'overridden' commands.
    private void run_disabled(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sender.sendMessage("/<command> <reload/debug>");

        final StringBuilder disabledCommands = new StringBuilder();
        for (Map.Entry<String, String> entry : RC.getAlternativeCommandsHandler().disabledCommands().entrySet()) {
            if (disabledCommands.length() > 0) {
                disabledCommands.append(", ");
            }
            disabledCommands.append(entry.getKey()).append(" => ").append(entry.getValue());
        }
        if (disabledCommands.length() > 0) {
            sender.sendMessage(tl("blockList"));
            sender.sendMessage(disabledCommands.toString());
        }
    }

    private void run_reset(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new Exception("/<command> reset <player>");
        }
        final User user = getPlayer(server, args, 1, true, true);
        user.reset();
        sender.sendMessage("Reset RefineCraft userdata for player: " + user.getDisplayName());
    }

    private void run_debug(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        RC.getSettings().setDebug(!RC.getSettings().isDebug());
        sender.sendMessage("RefineCraft " + RC.getDescription().getVersion() + " debug mode " + (RC.getSettings().isDebug() ? "enabled" : "disabled"));
    }

    private void run_reload(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        RC.reload();
        sender.sendMessage(tl("refinecraftReload", RC.getDescription().getVersion()));
    }

    private void run_nya(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        final Map<String, Float> noteMap = new HashMap<String, Float>();
        noteMap.put("1F#", 0.5f);
        noteMap.put("1G", 0.53f);
        noteMap.put("1G#", 0.56f);
        noteMap.put("1A", 0.6f);
        noteMap.put("1A#", 0.63f);
        noteMap.put("1B", 0.67f);
        noteMap.put("1C", 0.7f);
        noteMap.put("1C#", 0.76f);
        noteMap.put("1D", 0.8f);
        noteMap.put("1D#", 0.84f);
        noteMap.put("1E", 0.9f);
        noteMap.put("1F", 0.94f);
        noteMap.put("2F#", 1.0f);
        noteMap.put("2G", 1.06f);
        noteMap.put("2G#", 1.12f);
        noteMap.put("2A", 1.18f);
        noteMap.put("2A#", 1.26f);
        noteMap.put("2B", 1.34f);
        noteMap.put("2C", 1.42f);
        noteMap.put("2C#", 1.5f);
        noteMap.put("2D", 1.6f);
        noteMap.put("2D#", 1.68f);
        noteMap.put("2E", 1.78f);
        noteMap.put("2F", 1.88f);
        final String tuneStr = "1D#,1E,2F#,,2A#,1E,1D#,1E,2F#,2B,2D#,2E,2D#,2A#,2B,,2F#,,1D#,1E,2F#,2B,2C#,2A#,2B,2C#,2E,2D#,2E,2C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1C#,,2F#,,2G#,,1D,1D#,,1C#,1D,1C#,1B,,1B,,1C#,,1D,,1D,1C#,1B,1C#,1D#,2F#,2G#,1D#,2F#,1C#,1D#,1B,1C#,1B,1D#,,2F#,,2G#,1D#,2F#,1C#,1D#,1B,1D,1D#,1D,1C#,1B,1C#,1D,,1B,1C#,1D#,2F#,1C#,1D,1C#,1B,1C#,,1B,,1B,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1A#,,1B,,1F#,1G#,1B,,1F#,1G#,1B,1C#,1D#,1B,1E,1D#,1E,2F#,1B,,1B,,1F#,1G#,1B,1F#,1E,1D#,1C#,1B,,,,1F#,1B,,1F#,1G#,1B,,1F#,1G#,1B,1B,1C#,1D#,1B,1F#,1G#,1F#,1B,,1B,1A#,1B,1F#,1G#,1B,1E,1D#,1E,2F#,1B,,1B,,";
        final String[] tune = tuneStr.split(",");
        taskid = RC.scheduleSyncRepeatingTask(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                final String note = tune[i];
                i++;
                if (i >= tune.length) {
                    Commandrefinecraft.this.stopTune();
                }
                if (note == null || note.isEmpty()) {
                    return;
                }
                Sound noteHarp;
                try {
                    noteHarp = Sound.valueOf("NOTE_PIANO"); // pre-1.9, referenced internally as note.harp
                } catch (IllegalArgumentException e) {
                    noteHarp = Sound.valueOf("BLOCK_NOTE_HARP"); // 1.9
                }

                for (Player onlinePlayer : RC.getOnlinePlayers()) {
                    onlinePlayer.playSound(onlinePlayer.getLocation(), noteHarp, 1, noteMap.get(note));
                }
            }
        }, 20, 2);
    }

    private void stopTune() {
        RC.getScheduler().cancelTask(taskid);
        for (Block block : noteBlocks.values()) {
            if (block.getType() == Material.NOTE_BLOCK) {
                block.setType(Material.AIR);
            }
        }
        noteBlocks.clear();
    }

    private final String[] consoleMoo = new String[]{"         (__)", "         (oo)", "   /------\\/", "  / |    ||", " *  /\\---/\\", "    ~~   ~~", "....\"Have you mooed today?\"..."};
    private final String[] playerMoo = new String[]{"            (__)", "            (oo)", "   /------\\/", "  /  |      | |", " *  /\\---/\\", "    ~~    ~~", "....\"Have you mooed today?\"..."};

    private void run_moo(final Server server, final CommandSource sender, final String command, final String args[]) {
        Sound moo;
        try {
            moo = Sound.valueOf("COW_IDLE"); // pre-1.9
        } catch (IllegalArgumentException e) {
            moo = Sound.valueOf("ENTITY_COW_MILK"); // 1.9
        }
        if (args.length == 2 && args[1].equals("moo")) {
            for (String s : consoleMoo) {
                logger.info(s);
            }
            for (Player player : RC.getOnlinePlayers()) {
                player.sendMessage(playerMoo);
                player.playSound(player.getLocation(), moo, 1, 1.0f);
            }
        } else {
            if (sender.isPlayer()) {
                sender.getSender().sendMessage(playerMoo);
                final Player player = sender.getPlayer();
                player.playSound(player.getLocation(), moo, 1, 1.0f);

            } else {
                sender.getSender().sendMessage(consoleMoo);
            }
        }
    }

    private void run_optout(final Server server, final CommandSource sender, final String command, final String args[]) {
        final Metrics metrics = RC.getMetrics();
        try {
            sender.sendMessage("RefineCraft collects simple metrics to highlight which features to concentrate work on in the future.");
            if (metrics.isOptOut()) {
                metrics.enable();
            } else {
                metrics.disable();
            }
            sender.sendMessage("Anonymous Metrics are now " + (metrics.isOptOut() ? "disabled" : "enabled") + " for all plugins.");
        } catch (IOException ex) {
            sender.sendMessage("Unable to modify 'plugins/PluginMetrics/config.yml': " + ex.getMessage());
        }
    }

    private void run_cleanup(final Server server, final CommandSource sender, final String command, final String args[]) throws Exception {
        if (args.length < 2 || !NumberUtil.isInt(args[1])) {
            sender.sendMessage("This sub-command will delete users who havent logged in in the last <days> days.");
            sender.sendMessage("Optional parameters define the minium amount required to prevent deletion.");
            sender.sendMessage("Unless you define larger default values, this command wil ignore people who have more than 0 money/homes.");
            throw new Exception("/<command> cleanup <days> [money] [homes]");
        }
        sender.sendMessage(tl("cleaning"));

        final long daysArg = Long.parseLong(args[1]);
        final double moneyArg = args.length >= 3 ? FloatUtil.parseDouble(args[2].replaceAll("[^0-9\\.]", "")) : 0;
        final int homesArg = args.length >= 4 && NumberUtil.isInt(args[3]) ? Integer.parseInt(args[3]) : 0;
        final UserMap userMap = RC.getUserMap();

        RC.runTaskAsynchronously(new Runnable() {
            @Override
            public void run() {
                Long currTime = System.currentTimeMillis();
                for (UUID u : userMap.getAllUniqueUsers()) {
                    final User user = RC.getUserMap().getUser(u);
                    if (user == null) {
                        continue;
                    }

                    long lastLog = user.getLastLogout();
                    if (lastLog == 0) {
                        lastLog = user.getLastLogin();
                    }
                    if (lastLog == 0) {
                        user.setLastLogin(currTime);
                    }

                    if (user.isNPC()) {
                        continue;
                    }

                    long timeDiff = currTime - lastLog;
                    long milliDays = daysArg * 24L * 60L * 60L * 1000L;
                    int homeCount = user.getHomes().size();
                    double moneyCount = user.getMoney().doubleValue();

                    if ((lastLog == 0) || (timeDiff < milliDays) || (homeCount > homesArg) || (moneyCount > moneyArg)) {
                        continue;
                    }

                    if (RC.getSettings().isDebug()) {
                        RC.getLogger().info("Deleting user: " + user.getName() + " Money: " + moneyCount + " Homes: " + homeCount + " Last seen: " + DateUtil.formatDateDiff(lastLog));
                    }

                    user.reset();
                }
                sender.sendMessage(tl("cleaned"));
            }
        });

    }

    private void run_uuidconvert(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        sender.sendMessage("Starting RefineCraft UUID userdata conversion, this may lag the server.");

        Boolean ignoreUFCache = (args.length > 2 && args[1].toLowerCase(Locale.ENGLISH).contains("ignore"));
        RefineCraftUpgrade.uuidFileConvert(RC, ignoreUFCache);

        sender.sendMessage("UUID conversion complete, check your server log for more information.");
    }

    private void run_uuidtest(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new Exception("/<command> uuidtest <name>");
        }
        String name = args[1];
        sender.sendMessage("Looking up UUID for " + name);

        UUID onlineUUID = null;

        for (Player player : RC.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                onlineUUID = player.getUniqueId();
                break;
            }
        }

        UUID RCUUID = RC.getUserMap().getUser(name).getConfigUUID();

        org.bukkit.OfflinePlayer player = RC.getServer().getOfflinePlayer(name);
        UUID bukkituuid = player.getUniqueId();
        sender.sendMessage("Bukkit Lookup: " + bukkituuid.toString());

        if (onlineUUID != null && onlineUUID != bukkituuid) {
            sender.sendMessage("Online player: " + onlineUUID.toString());
        }

        if (RCUUID != null && RCUUID != bukkituuid) {
            sender.sendMessage("RefineCraft config: " + RCUUID.toString());
        }

        UUID npcuuid = UUID.nameUUIDFromBytes(("NPC:" + name).getBytes(Charsets.UTF_8));
        sender.sendMessage("NPC UUID: " + npcuuid.toString());

        UUID offlineuuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        sender.sendMessage("Offline Mode UUID: " + offlineuuid.toString());
    }
}
