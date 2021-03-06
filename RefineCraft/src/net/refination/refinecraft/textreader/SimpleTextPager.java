package net.refination.refinecraft.textreader;

import net.refination.refinecraft.CommandSource;

import java.util.List;

public class SimpleTextPager {
    private final transient InterfaceText text;

    public SimpleTextPager(final InterfaceText text) {
        this.text = text;
    }

    public void showPage(final CommandSource sender) {
        for (String line : text.getLines()) {
            sender.sendMessage(line);
        }
    }

    public List<String> getLines() {
        return text.getLines();
    }

    public String getLine(int line) {
        if (text.getLines().size() < line) {
            return null;
        }
        return text.getLines().get(line);
    }
}
