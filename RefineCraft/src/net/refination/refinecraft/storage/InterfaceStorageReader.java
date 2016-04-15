package net.refination.refinecraft.storage;


public interface InterfaceStorageReader {
    <T extends StorageObject> T load(final Class<? extends T> clazz) throws ObjectLoadException;
}
