package net.refination.refinecraft;

import net.refination.api.InterfaceRefineCraft;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AlternativeCommandsHandler {
    private static final Logger LOGGER = Logger.getLogger("RefineCraft");
    private final transient Map<String, List<PluginCommand>> altcommands = new HashMap<>();
    private final transient Map<String, String> disabledList = new HashMap<>();
    private final transient InterfaceRefineCraft RC;

    public AlternativeCommandsHandler(final InterfaceRefineCraft RC) {
        this.RC = RC;
        for (Plugin plugin : RC.getServer().getPluginManager().getPlugins()) {
            if (plugin.isEnabled()) {
                addPlugin(plugin);
            }
        }
    }

    public final void addPlugin(final Plugin plugin) {
        if (plugin.getDescription().getMain().contains("net.refination.refinecraft")) {
            return;
        }
        final List<Command> commands = PluginCommandYamlParser.parse(plugin);
        final String pluginName = plugin.getDescription().getName().toLowerCase(Locale.ENGLISH);

        for (Command command : commands) {
            final PluginCommand pc = (PluginCommand) command;
            final List<String> labels = new ArrayList<>(pc.getAliases());
            labels.add(pc.getName());

            PluginCommand reg = RC.getServer().getPluginCommand(pluginName + ":" + pc.getName().toLowerCase(Locale.ENGLISH));
            if (reg == null) {
                reg = RC.getServer().getPluginCommand(pc.getName().toLowerCase(Locale.ENGLISH));
            }
            if (reg == null || !reg.getPlugin().equals(plugin)) {
                continue;
            }
            for (String label : labels) {
                List<PluginCommand> plugincommands = altcommands.get(label.toLowerCase(Locale.ENGLISH));
                if (plugincommands == null) {
                    plugincommands = new ArrayList<>();
                    altcommands.put(label.toLowerCase(Locale.ENGLISH), plugincommands);
                }
                boolean found = false;
                for (PluginCommand pc2 : plugincommands) {
                    if (pc2.getPlugin().equals(plugin)) {
                        found = true;
                    }
                }
                if (!found) {
                    plugincommands.add(reg);
                }
            }
        }
    }

    public void removePlugin(final Plugin plugin) {
        final Iterator<Map.Entry<String, List<PluginCommand>>> iterator = altcommands.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, List<PluginCommand>> entry = iterator.next();
            final Iterator<PluginCommand> pcIterator = entry.getValue().iterator();
            while (pcIterator.hasNext()) {
                final PluginCommand pc = pcIterator.next();
                if (pc.getPlugin() == null || pc.getPlugin().equals(plugin)) {
                    pcIterator.remove();
                }
            }
            if (entry.getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }

    public PluginCommand getAlternative(final String label) {
        final List<PluginCommand> commands = altcommands.get(label);
        if (commands == null || commands.isEmpty()) {
            return null;
        }
        if (commands.size() == 1) {
            return commands.get(0);
        }
        // return the first command that is not an alias
        for (PluginCommand command : commands) {
            if (command.getName().equalsIgnoreCase(label)) {
                return command;
            }
        }
        // return the first alias
        return commands.get(0);
    }

    public void executed(final String label, final PluginCommand pc) {
        final String altString = pc.getPlugin().getName() + ":" + pc.getLabel();
        if (RC.getSettings().isDebug()) {
            LOGGER.log(Level.INFO, "RefineCraft: Alternative command " + label + " found, using " + altString);
        }
        disabledList.put(label, altString);
    }

    public Map<String, String> disabledCommands() {
        return disabledList;
    }
}
