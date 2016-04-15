package net.refination.refinecraft.api;

import java.util.Locale;


public interface InterfaceI18n {
    /**
     * Gets the current locale setting
     *
     * @return the current locale, if not set it will return the default locale
     */
    Locale getCurrentLocale();
}
