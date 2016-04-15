package net.refination.refinecraft.messaging;

/**
 * Represents an interface for message recipients.
 */
public interface InterfaceMessageRecipient {

    /**
     * Sends (prints) a message to this recipient.
     *
     * @param message message
     */
    void sendMessage(String message);

    /**
     * This method is called when this {@link InterfaceMessageRecipient} is sending a message to another {@link InterfaceMessageRecipient}.
     * <br />
     * The {@link MessageResponse} that is returned is used to determine what exactly should happen in the {@link #sendMessage(InterfaceMessageRecipient,
     * String)} implementation by the {@code sender}.
     *
     * @param recipient recipient to receive the {@code message}
     * @param message message to send
     *
     * @return the response of the message
     */
    MessageResponse sendMessage(InterfaceMessageRecipient recipient, String message);

    /**
     * This method is called when this recipient is receiving a message from another {@link InterfaceMessageRecipient}.
     * <br />
     * The {@link MessageResponse} that is returned is used to determine what exactly should happen in the {@link #sendMessage(InterfaceMessageRecipient,
     * String)} implementation by the {@code sender}.
     * <p />
     * <b>This method should only be called by {@link #sendMessage(InterfaceMessageRecipient, String)}.</b>
     *
     * @param sender sender of the {@code message}
     * @param message message being received
     *
     * @return the response of the message
     */
    MessageResponse onReceiveMessage(InterfaceMessageRecipient sender, String message);

    /**
     * Returns the name of this recipient. This name is typically used internally to identify this recipient.
     *
     * @return name of this recipient
     *
     * @see #getDisplayName()
     */
    String getName();

    /**
     * Returns the display name of this recipient. This name is typically used when formatting messages.
     *
     * @return display name of this recipient
     */
    String getDisplayName();

    /**
     * Returns whether this recipient is reachable. A case where the recipient is not reachable is if they are offline.
     *
     * @return whether this recipient is reachable
     */
    boolean isReachable();

    /**
     * Returns the {@link InterfaceMessageRecipient} this recipient should send replies to.
     *
     * @return message recipient
     */
    InterfaceMessageRecipient getReplyRecipient();

    /**
     * Sets the {@link InterfaceMessageRecipient} this recipient should send replies to.
     *
     * @param recipient message recipient to set
     */
    void setReplyRecipient(InterfaceMessageRecipient recipient);

    /**
     * Represents a response for sending or receiving a message when using {@link InterfaceMessageRecipient#sendMessage(InterfaceMessageRecipient, String)} or
     * {@link InterfaceMessageRecipient#onReceiveMessage(InterfaceMessageRecipient, String)}.
     */
    enum MessageResponse {
        /** States that the message was received and assumed readable by the receiver. */
        SUCCESS,
        /** States that the message was received, but the receiver was away, assuming the message was not read. */
        SUCCESS_BUT_AFK,
        /** States that the message was <b>NOT</b> received as a result of the receiver ignoring all messages. */
        MESSAGES_IGNORED,
        /** States that the message was <b>NOT</b> received as a result of the sender being ignored by the recipient. */
        SENDER_IGNORED,
        /** States that the message was <b>NOT</b> received as a result of the recipient being unreachable. */
        UNREACHABLE;

        /**
         * Returns whether this response is a success. In other words equal to {@link #SUCCESS} or {@link #SUCCESS_BUT_AFK}
         *
         * @return whether the response is a success
         */
        public boolean isSuccess() {
            return this == SUCCESS || this == SUCCESS_BUT_AFK;
        }
    }
}
