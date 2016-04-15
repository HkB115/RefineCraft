package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FloatUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;

import static net.refination.refinecraft.I18n.tl;


public class Commandspeed extends RefineCraftCommand {
    public Commandspeed() {
        super("speed");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }
        final boolean isFly = isFlyMode(args[0]);
        final float speed = getMoveSpeed(args[1]);
        speedOtherPlayers(server, sender, isFly, true, speed, args[2]);
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        boolean isFly;
        float speed;
        boolean isBypass = user.isAuthorized("refinecraft.speed.bypass");
        if (args.length == 1) {
            isFly = flyPermCheck(user, user.getBase().isFlying());
            speed = getMoveSpeed(args[0]);
        } else {
            isFly = flyPermCheck(user, isFlyMode(args[0]));
            speed = getMoveSpeed(args[1]);
            if (args.length > 2 && user.isAuthorized("refinecraft.speed.others")) {
                if (args[2].trim().length() < 2) {
                    throw new PlayerNotFoundException();
                }
                speedOtherPlayers(server, user.getSource(), isFly, isBypass, speed, args[2]);
                return;
            }
        }

        if (isFly) {
            user.getBase().setFlySpeed(getRealMoveSpeed(speed, isFly, isBypass));
            user.sendMessage(tl("moveSpeed", tl("flying"), speed, user.getDisplayName()));
        } else {
            user.getBase().setWalkSpeed(getRealMoveSpeed(speed, isFly, isBypass));
            user.sendMessage(tl("moveSpeed", tl("walking"), speed, user.getDisplayName()));
        }
    }

    private void speedOtherPlayers(final Server server, final CommandSource sender, final boolean isFly, final boolean isBypass, final float speed, final String name) throws PlayerNotFoundException {
        boolean skipHidden = sender.isPlayer() && !RC.getUser(sender.getPlayer()).canInteractGhosted();
        boolean foundUser = false;
        final List<Player> matchedPlayers = server.matchPlayer(name);
        for (Player matchPlayer : matchedPlayers) {
            final User player = RC.getUser(matchPlayer);
            if (skipHidden && player.isHidden(sender.getPlayer()) && !sender.getPlayer().canSee(matchPlayer)) {
                continue;
            }
            foundUser = true;
            if (isFly) {
                matchPlayer.setFlySpeed(getRealMoveSpeed(speed, isFly, isBypass));
                sender.sendMessage(tl("moveSpeed", tl("flying"), speed, matchPlayer.getDisplayName()));
            } else {
                matchPlayer.setWalkSpeed(getRealMoveSpeed(speed, isFly, isBypass));
                sender.sendMessage(tl("moveSpeed", tl("walking"), speed, matchPlayer.getDisplayName()));
            }
        }
        if (!foundUser) {
            throw new PlayerNotFoundException();
        }
    }

    private Boolean flyPermCheck(User user, boolean input) throws Exception {
        boolean canFly = user.isAuthorized("refinecraft.speed.fly");
        boolean canWalk = user.isAuthorized("refinecraft.speed.walk");
        if (input && canFly || !input && canWalk || !canFly && !canWalk) {
            return input;
        } else if (canWalk) {
            return false;
        }
        return true;
    }

    private boolean isFlyMode(final String modeString) throws NotEnoughArgumentsException {
        boolean isFlyMode;
        if (modeString.contains("fly") || modeString.equalsIgnoreCase("f")) {
            isFlyMode = true;
        } else if (modeString.contains("walk") || modeString.contains("run") || modeString.equalsIgnoreCase("w") || modeString.equalsIgnoreCase("r")) {
            isFlyMode = false;
        } else {
            throw new NotEnoughArgumentsException();
        }
        return isFlyMode;
    }

    private float getMoveSpeed(final String moveSpeed) throws NotEnoughArgumentsException {
        float userSpeed;
        try {
            userSpeed = FloatUtil.parseFloat(moveSpeed);
            if (userSpeed > 10f) {
                userSpeed = 10f;
            } else if (userSpeed < 0.0001f) {
                userSpeed = 0.0001f;
            }
        } catch (NumberFormatException e) {
            throw new NotEnoughArgumentsException();
        }
        return userSpeed;
    }

    private float getRealMoveSpeed(final float userSpeed, final boolean isFly, final boolean isBypass) {
        final float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1f;
        if (!isBypass) {
            maxSpeed = (float) (isFly ? RC.getSettings().getMaxFlySpeed() : RC.getSettings().getMaxWalkSpeed());
        }

        if (userSpeed < 1f) {
            return defaultSpeed * userSpeed;
        } else {
            float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }
}
