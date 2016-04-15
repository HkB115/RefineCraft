package net.refination.refinecraft.commands;

import net.refination.refinecraft.Backup;
import net.refination.refinecraft.CommandSource;
import org.bukkit.Server;

import static net.refination.refinecraft.I18n.tl;


public class Commandbackup extends RefineCraftCommand {
    public Commandbackup() {
        super("backup");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        final Backup backup = RC.getBackup();
        if (backup == null) {
            throw new Exception(tl("backupDisabled"));
        }
        final String command = RC.getSettings().getBackupCommand();
        if (command == null || "".equals(command) || "save-all".equalsIgnoreCase(command)) {
            throw new Exception(tl("backupDisabled"));
        }
        backup.run();
        sender.sendMessage(tl("backupStarted"));
    }
}
