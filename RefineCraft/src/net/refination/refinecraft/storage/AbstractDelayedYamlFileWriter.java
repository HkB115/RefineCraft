package net.refination.refinecraft.storage;

import net.refination.api.InterfaceRefineCraft;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;


public abstract class AbstractDelayedYamlFileWriter implements Runnable {
    private final transient File file;

    public AbstractDelayedYamlFileWriter(InterfaceRefineCraft RC, File file) {
        this.file = file;
        RC.runTaskAsynchronously(this);
    }

    public abstract StorageObject getObject();

    @Override
    public void run() {
        PrintWriter pw = null;
        try {
            final StorageObject object = getObject();
            final File folder = file.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
            pw = new PrintWriter(file);
            new YamlStorageWriter(pw).save(object);
        } catch (FileNotFoundException ex) {
            Bukkit.getLogger().log(Level.SEVERE, file.toString(), ex);
        } finally {
            onFinish();
            if (pw != null) {
                pw.close();
            }
        }

    }

    public abstract void onFinish();
}
