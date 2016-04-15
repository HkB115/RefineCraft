package net.refination.refinecraft;

import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.refination.refinecraft.I18n.tl;


public class Backup implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("RefineCraft");
    private transient final Server server;
    private transient final InterfaceRefineCraft RC;
    private transient boolean running = false;
    private transient int taskId = -1;
    private transient boolean active = false;

    public Backup(final InterfaceRefineCraft RC) {
        this.RC = RC;
        server = RC.getServer();
        if (!RC.getOnlinePlayers().isEmpty()) {
            RC.runTaskAsynchronously(new Runnable() {
                @Override
                public void run() {
                    startTask();
                }
            });
        }
    }

    public void onPlayerJoin() {
        startTask();
    }

    public synchronized void stopTask() {
        running = false;
        if (taskId != -1) {
            server.getScheduler().cancelTask(taskId);
        }
        taskId = -1;
    }

    private synchronized void startTask() {
        if (!running) {
            final long interval = RC.getSettings().getBackupInterval() * 1200; // minutes -> ticks
            if (interval < 1200) {
                return;
            }
            taskId = RC.scheduleSyncRepeatingTask(this, interval, interval);
            running = true;
        }
    }

    @Override
    public void run() {
        if (active) {
            return;
        }
        active = true;
        final String command = RC.getSettings().getBackupCommand();
        if (command == null || "".equals(command)) {
            return;
        }
        if ("save-all".equalsIgnoreCase(command)) {
            final CommandSender cs = server.getConsoleSender();
            server.dispatchCommand(cs, "save-all");
            active = false;
            return;
        }
        LOGGER.log(Level.INFO, tl("backupStarted"));
        final CommandSender cs = server.getConsoleSender();
        server.dispatchCommand(cs, "save-all");
        server.dispatchCommand(cs, "save-off");

        RC.runTaskAsynchronously(new Runnable() {
            @Override
            public void run() {
                try {
                    final ProcessBuilder childBuilder = new ProcessBuilder(command);
                    childBuilder.redirectErrorStream(true);
                    childBuilder.directory(RC.getDataFolder().getParentFile().getParentFile());
                    final Process child = childBuilder.start();
                    RC.runTaskAsynchronously(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));
                                try {
                                    String line;
                                    do {
                                        line = reader.readLine();
                                        if (line != null) {
                                            LOGGER.log(Level.INFO, line);
                                        }
                                    } while (line != null);
                                } finally {
                                    reader.close();
                                }
                            } catch (IOException ex) {
                                LOGGER.log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    child.waitFor();
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } finally {
                    class BackupEnableSaveTask implements Runnable {
                        @Override
                        public void run() {
                            server.dispatchCommand(cs, "save-on");
                            if (RC.getOnlinePlayers().isEmpty()) {
                                stopTask();
                            }
                            active = false;
                            LOGGER.log(Level.INFO, tl("backupFinished"));
                        }
                    }
                    RC.scheduleSyncDelayedTask(new BackupEnableSaveTask());
                }
            }
        });
    }
}
