package net.refination.refinecraft;

import net.refination.refinecraft.messaging.InterfaceMessageRecipient;
import net.refination.refinecraft.messaging.SimpleMessageRecipient;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public final class Console implements InterfaceMessageRecipient {
    public static final String NAME = "Console";
    private static Console instance; // Set in refinecraft
    
    private final InterfaceRefineCraft RC;
    private final InterfaceMessageRecipient messageRecipient;

    public static Console getInstance() {
        return instance;
    }

    static void setInstance(InterfaceRefineCraft RC) { // Called in RefineCraft#onEnable()
        instance = new Console(RC);
    }
    
    /**
     * @deprecated Use {@link Console#getCommandSender()}
     */
    @Deprecated
    public static CommandSender getCommandSender(Server server) throws Exception {
        return server.getConsoleSender();
    }

    private Console(InterfaceRefineCraft RC) {
        this.RC = RC;
        this.messageRecipient = new SimpleMessageRecipient(RC, this);
    }

    public CommandSender getCommandSender() {
        return RC.getServer().getConsoleSender();
    }

    @Override public String getName() {
        return Console.NAME;
    }

    @Override public String getDisplayName() {
        return Console.NAME;
    }

    @Override public void sendMessage(String message) {
        getCommandSender().sendMessage(message);
    }

    @Override public boolean isReachable() {
        return true;
    }
    
    /* ================================
     * >> DELEGATE METHODS
     * ================================ */

    @Override public MessageResponse sendMessage(InterfaceMessageRecipient recipient, String message) {
        return this.messageRecipient.sendMessage(recipient, message);
    }

    @Override public MessageResponse onReceiveMessage(InterfaceMessageRecipient sender, String message) {
        return this.messageRecipient.onReceiveMessage(sender, message);
    }

    @Override public InterfaceMessageRecipient getReplyRecipient() {
        return this.messageRecipient.getReplyRecipient();
    }

    @Override public void setReplyRecipient(InterfaceMessageRecipient recipient) {
        this.messageRecipient.setReplyRecipient(recipient);
    }
}
