package net.refination.refinecraft.messaging;

import static net.refination.refinecraft.I18n.tl;

import net.refination.refinecraft.Console;
import net.refination.refinecraft.InterfaceRefineCraft;
import net.refination.refinecraft.InterfaceUser;
import net.refination.refinecraft.User;

import java.lang.ref.WeakReference;

/**
 * Represents a simple reusable implementation of {@link InterfaceMessageRecipient}. This class provides functionality for the following methods:
 * <ul>
 *     <li>{@link InterfaceMessageRecipient#sendMessage(InterfaceMessageRecipient, String)}</li>
 *     <li>{@link InterfaceMessageRecipient#onReceiveMessage(InterfaceMessageRecipient, String)}</li>
 *     <li>{@link InterfaceMessageRecipient#getReplyRecipient()}</li>
 *     <li>{@link InterfaceMessageRecipient#setReplyRecipient(InterfaceMessageRecipient)}</li>
 * </ul>
 * 
 * <b>The given {@code parent} must implement the following methods to prevent overflow:</b>
 * <ul>
 *     <li>{@link InterfaceMessageRecipient#sendMessage(String)}</li>
 *     <li>{@link InterfaceMessageRecipient#getName()}</li>
 *     <li>{@link InterfaceMessageRecipient#getDisplayName()}</li>
 *     <li>{@link InterfaceMessageRecipient#isReachable()}</li>
 * </ul>
 * 
 * The reply-recipient is wrapped in a {@link WeakReference}.
 */
public class SimpleMessageRecipient implements InterfaceMessageRecipient {

    private final InterfaceRefineCraft RC;
    private final InterfaceMessageRecipient parent;
    
    private long lastMessageMs;
    private WeakReference<InterfaceMessageRecipient> replyRecipient;
    
    public SimpleMessageRecipient(InterfaceRefineCraft RC, InterfaceMessageRecipient parent) {
        this.RC = RC;
        this.parent = parent;
    }

    @Override
    public void sendMessage(String message) {
        this.parent.sendMessage(message);
    }

    @Override
    public String getName() {
        return this.parent.getName();
    }

    @Override public String getDisplayName() {
        return this.parent.getDisplayName();
    }

    @Override public MessageResponse sendMessage(InterfaceMessageRecipient recipient, String message) {
        MessageResponse messageResponse = recipient.onReceiveMessage(this.parent, message);
        switch (messageResponse) {
            case UNREACHABLE:
                sendMessage(tl("recentlyForeverAlone", recipient.getDisplayName()));
                break;
            case MESSAGES_IGNORED:
                sendMessage(tl("msgIgnore", recipient.getDisplayName()));
                break;
            case SENDER_IGNORED:
                break;
            // When this recipient is AFK, notify the sender. Then, proceed to send the message.
            case SUCCESS_BUT_AFK:
                sendMessage(tl("userAFK", recipient.getDisplayName()));
            default:
                sendMessage(tl("msgFormat", tl("me"), recipient.getDisplayName(), message));
        }
        // If the message was a success, set this sender's reply-recipient to the current recipient.
        if (messageResponse.isSuccess()) {
            setReplyRecipient(recipient);
        }
        return messageResponse;
    }

    @Override
    public MessageResponse onReceiveMessage(InterfaceMessageRecipient sender, String message) {
        if (!isReachable()) {
            return MessageResponse.UNREACHABLE;
        }
        
        User user = this.parent instanceof User ? (User) this.parent : null;
        boolean afk = false;
        if (user != null) {
            if (user.isIgnoreMsg()
                && !(sender instanceof Console)) { // Console must never be ignored.
                return MessageResponse.MESSAGES_IGNORED;
            }
            afk = user.isAfk();
            // Check whether this recipient ignores the sender, only if the sender is not the console.
            if (sender instanceof InterfaceUser && user.isIgnoredPlayer((InterfaceUser) sender)) {
                return MessageResponse.SENDER_IGNORED;
            }
        }
        // Display the formatted message to this recipient.
        sendMessage(tl("msgFormat", sender.getDisplayName(), tl("me"), message));

        if (RC.getSettings().isLastMessageReplyRecipient()) {
            // If this recipient doesn't have a reply recipient, initiate by setting the first
            // message sender to this recipient's replyRecipient.
            long timeout = RC.getSettings().getLastMessageReplyRecipientTimeout() * 1000;
            if (getReplyRecipient() == null || !getReplyRecipient().isReachable() 
                || System.currentTimeMillis() - this.lastMessageMs > timeout) {
                setReplyRecipient(sender);
            }
        } else { // Old message functionality, always set the reply recipient to the last person who sent us a message.
            setReplyRecipient(sender);
        }
        this.lastMessageMs = System.currentTimeMillis();
        return afk ? MessageResponse.SUCCESS_BUT_AFK : MessageResponse.SUCCESS;
    }

    @Override public boolean isReachable() {
        return this.parent.isReachable();
    }

    /**
     * {@inheritDoc}
     * <p />
     * <b>This {@link net.refination.refinecraft.messaging.SimpleMessageRecipient} implementation stores the a weak reference to the recipient.</b>
     */
    @Override
    public InterfaceMessageRecipient getReplyRecipient() {
        return replyRecipient == null ? null : replyRecipient.get();
    }

    /**
     * {@inheritDoc}
     * <p />
     * <b>This {@link net.refination.refinecraft.messaging.SimpleMessageRecipient} implementation stores the a weak reference to the recipient.</b>
     */
    @Override
    public void setReplyRecipient(final InterfaceMessageRecipient replyRecipient) {
        this.replyRecipient = new WeakReference<>(replyRecipient);
    }
}
