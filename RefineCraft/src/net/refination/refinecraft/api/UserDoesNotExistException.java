package net.refination.refinecraft.api;

import static net.refination.refinecraft.I18n.tl;


public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException(String name) {
        super(tl("userDoesNotExist", name));
    }
}
