package net.refination.refinecraft.textreader;

import net.refination.refinecraft.User;
import net.refination.api.InterfaceRefineCraft;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.refination.refinecraft.I18n.tl;


public class HelpInput implements InterfaceText {
    private static final String DESCRIPTION = "description";
    private static final String PERMISSION = "permission";
    private static final String PERMISSIONS = "permissions";
    private static final Logger logger = Logger.getLogger("RefineCraft");
    private final transient List<String> lines = new ArrayList<String>();
    private final transient List<String> chapters = new ArrayList<String>();
    private final transient Map<String, Integer> bookmarks = new HashMap<String, Integer>();

    public HelpInput(final User user, final String match, final InterfaceRefineCraft RC) throws IOException {
        boolean reported = false;
        final List<String> newLines = new ArrayList<String>();
        String pluginName = "";
        String pluginNameLow = "";
        if (!match.equalsIgnoreCase("")) {
            lines.add(tl("helpMatching", match));
        }

        for (Plugin p : RC.getServer().getPluginManager().getPlugins()) {
            try {
                final List<String> pluginLines = new ArrayList<String>();
                final PluginDescriptionFile desc = p.getDescription();
                final Map<String, Map<String, Object>> cmds = desc.getCommands();
                pluginName = p.getDescription().getName();
                pluginNameLow = pluginName.toLowerCase(Locale.ENGLISH);
                if (pluginNameLow.equals(match)) {
                    lines.clear();
                    newLines.clear();
                    lines.add(tl("helpFrom", p.getDescription().getName()));
                }
                final boolean isOnWhitelist = user.isAuthorized("refinecraft.help." + pluginNameLow);

                for (Map.Entry<String, Map<String, Object>> k : cmds.entrySet()) {
                    try {
                        if (!match.equalsIgnoreCase("") && (!pluginNameLow.contains(match)) && (!k.getKey().toLowerCase(Locale.ENGLISH).contains(match)) && (!(k.getValue().get(DESCRIPTION) instanceof String && ((String) k.getValue().get(DESCRIPTION)).toLowerCase(Locale.ENGLISH).contains(match)))) {
                            continue;
                        }

                        if (pluginNameLow.contains("refinecraft")) {
                            final String node = "refinecraft." + k.getKey();
                            if (!RC.getSettings().isCommandDisabled(k.getKey()) && user.isAuthorized(node)) {
                                pluginLines.add(tl("helpLine", k.getKey(), k.getValue().get(DESCRIPTION)));
                            }
                        } else {
                            if (RC.getSettings().showNonEssCommandsInHelp()) {
                                final Map<String, Object> value = k.getValue();
                                Object permissions = null;
                                if (value.containsKey(PERMISSION)) {
                                    permissions = value.get(PERMISSION);
                                } else if (value.containsKey(PERMISSIONS)) {
                                    permissions = value.get(PERMISSIONS);
                                }
                                if (isOnWhitelist || user.isAuthorized("refinecraft.help." + pluginNameLow + "." + k.getKey())) {
                                    pluginLines.add(tl("helpLine", k.getKey(), value.get(DESCRIPTION)));
                                } else if (permissions instanceof List && !((List<Object>) permissions).isEmpty()) {
                                    boolean enabled = false;
                                    for (Object o : (List<Object>) permissions) {
                                        if (o instanceof String && user.isAuthorized(o.toString())) {
                                            enabled = true;
                                            break;
                                        }
                                    }
                                    if (enabled) {
                                        pluginLines.add(tl("helpLine", k.getKey(), value.get(DESCRIPTION)));
                                    }
                                } else if (permissions instanceof String && !"".equals(permissions)) {
                                    if (user.isAuthorized(permissions.toString())) {
                                        pluginLines.add(tl("helpLine", k.getKey(), value.get(DESCRIPTION)));
                                    }
                                } else {
                                    if (!RC.getSettings().hidePermissionlessHelp()) {
                                        pluginLines.add(tl("helpLine", k.getKey(), value.get(DESCRIPTION)));
                                    }
                                }
                            }
                        }
                    } catch (NullPointerException ex) {
                    }
                }
                if (!pluginLines.isEmpty()) {
                    newLines.addAll(pluginLines);
                    if (pluginNameLow.equals(match)) {
                        break;
                    }
                    if (match.equalsIgnoreCase("")) {
                        lines.add(tl("helpPlugin", pluginName, pluginNameLow));
                    }
                }
            } catch (NullPointerException ex) {
            } catch (Exception ex) {
                if (!reported) {
                    logger.log(Level.WARNING, tl("commandHelpFailedForPlugin", pluginNameLow), ex);
                }
                reported = true;
            }
        }
        lines.addAll(newLines);
    }

    @Override
    public List<String> getLines() {
        return lines;
    }

    @Override
    public List<String> getChapters() {
        return chapters;
    }

    @Override
    public Map<String, Integer> getBookmarks() {
        return bookmarks;
    }
}
