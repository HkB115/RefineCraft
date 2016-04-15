package net.refination.refinecraft.storage;

import net.refination.refinecraft.InterfaceConf;
import net.refination.api.InterfaceRefineCraft;
import net.refination.api.InterfaceReload;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;


public abstract class AsyncStorageObjectHolder<T extends StorageObject> implements InterfaceConf, InterfaceStorageObjectHolder<T>, InterfaceReload {
    private transient T data;
    private final transient ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final transient Class<T> clazz;
    protected final transient InterfaceRefineCraft RC;

    public AsyncStorageObjectHolder(final InterfaceRefineCraft RC, final Class<T> clazz) {
        this.RC = RC;
        this.clazz = clazz;
        try {
            this.data = clazz.newInstance();
        } catch (IllegalAccessException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        } catch (InstantiationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public void acquireReadLock() {
        rwl.readLock().lock();
    }

    @Override
    public void acquireWriteLock() {
        while (rwl.getReadHoldCount() > 0) {
            rwl.readLock().unlock();
        }
        rwl.writeLock().lock();
        rwl.readLock().lock();
    }

    @Override
    public void close() {
        unlock();
    }

    @Override
    public void unlock() {
        if (rwl.isWriteLockedByCurrentThread()) {
            rwl.writeLock().unlock();
            new StorageObjectDataWriter();
        }
        while (rwl.getReadHoldCount() > 0) {
            rwl.readLock().unlock();
        }
    }

    @Override
    public void reloadConfig() {
        new StorageObjectDataReader();
    }

    @Override
    public void onReload() {
        new StorageObjectDataReader();
    }

    public abstract void finishRead();

    public abstract void finishWrite();

    public abstract File getStorageFile();


    private class StorageObjectDataWriter extends AbstractDelayedYamlFileWriter {
        StorageObjectDataWriter() {
            super(RC, getStorageFile());
        }

        @Override
        public StorageObject getObject() {
            acquireReadLock();
            return getData();
        }

        @Override
        public void onFinish() {
            unlock();
            finishWrite();
        }
    }


    private class StorageObjectDataReader extends AbstractDelayedYamlFileReader<T> {
        StorageObjectDataReader() {
            super(RC, getStorageFile(), clazz);
        }

        @Override
        public void onStart() {
            rwl.writeLock().lock();
        }

        @Override
        public void onSuccess(final T object) {
            if (object != null) {
                data = object;
            }
            rwl.writeLock().unlock();
            finishRead();
        }

        @Override
        public void onException() {
            if (data == null) {
                try {
                    data = clazz.newInstance();
                } catch (IllegalAccessException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                } catch (InstantiationException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
            rwl.writeLock().unlock();
        }
    }
}
