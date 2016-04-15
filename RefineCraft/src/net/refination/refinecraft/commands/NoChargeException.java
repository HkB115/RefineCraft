package net.refination.refinecraft.commands;


public class NoChargeException extends Exception {
    public NoChargeException() {
        super("Will charge later");
    }
}
