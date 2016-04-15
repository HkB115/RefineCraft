package net.refination.providers;

public interface Provider {
    boolean tryProvider();

    String getHumanName();
}
