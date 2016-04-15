package net.refination.refinecraft.storage;


public interface InterfaceStorageObjectHolder<T extends StorageObject> {
    T getData();

    void acquireReadLock();

    void acquireWriteLock();

    void close();

    void unlock();
}
