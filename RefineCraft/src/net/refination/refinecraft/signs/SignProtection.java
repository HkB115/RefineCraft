package net.refination.refinecraft.signs;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.Trade.OverflowType;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.FormatUtil;
import net.refination.api.InterfaceRefineCraft;
import net.refination.api.MaxMoneyException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static net.refination.refinecraft.I18n.tl;


@Deprecated // This sign will be removed soon
public class SignProtection extends RefineCraftSign {
    private final transient Set<Material> protectedBlocks = EnumSet.noneOf(Material.class);

    public SignProtection() {
        super("Protection");
        protectedBlocks.add(Material.CHEST);
        protectedBlocks.add(Material.BURNING_FURNACE);
        protectedBlocks.add(Material.FURNACE);
        protectedBlocks.add(Material.DISPENSER);
    }

    @Override
    protected boolean onSignCreate(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, ChargeException {
        sign.setLine(3, "§4" + username);
        if (hasAdjacentBlock(sign.getBlock())) {
            final SignProtectionState state = isBlockProtected(sign.getBlock(), player, username, true);
            if (state == SignProtectionState.NOSIGN || state == SignProtectionState.OWNER || player.isAuthorized("refinecraft.signs.protection.override")) {
                sign.setLine(3, "§1" + username);
                return true;
            }
        }
        player.sendMessage(tl("signProtectInvalidLocation"));
        return false;
    }

    @Override
    protected boolean onSignBreak(final InterfaceSign sign, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        final SignProtectionState state = checkProtectionSign(sign, player, username);
        return state == SignProtectionState.OWNER;
    }

    public boolean hasAdjacentBlock(final Block block, final Block... ignoredBlocks) {
        final Block[] faces = getAdjacentBlocks(block);
        for (Block b : faces) {
            for (Block ignoredBlock : ignoredBlocks) {
                if (b.getLocation().equals(ignoredBlock.getLocation())) {
                    //TODO: What?
                }
            }
            if (protectedBlocks.contains(b.getType())) {
                return true;
            }
        }
        return false;
    }

    private void checkIfSignsAreBroken(final Block block, final User player, final String username, final InterfaceRefineCraft RC) throws MaxMoneyException {
        final Map<Location, SignProtectionState> signs = getConnectedSigns(block, player, username, false);
        for (Map.Entry<Location, SignProtectionState> entry : signs.entrySet()) {
            if (entry.getValue() != SignProtectionState.NOSIGN) {
                final Block sign = entry.getKey().getBlock();
                if (!hasAdjacentBlock(sign, block)) {
                    block.setType(Material.AIR);
                    final Trade trade = new Trade(new ItemStack(Material.SIGN, 1), RC);
                    trade.pay(player, OverflowType.DROP);
                }
            }
        }
    }

    private Map<Location, SignProtectionState> getConnectedSigns(final Block block, final User user, final String username, boolean secure) {
        final Map<Location, SignProtectionState> signs = new HashMap<Location, SignProtectionState>();
        getConnectedSigns(block, signs, user, username, secure ? 4 : 2);
        return signs;
    }

    private void getConnectedSigns(final Block block, final Map<Location, SignProtectionState> signs, final User user, final String username, final int depth) {
        final Block[] faces = getAdjacentBlocks(block);
        for (Block b : faces) {
            final Location loc = b.getLocation();
            if (signs.containsKey(loc)) {
                continue;
            }
            final SignProtectionState check = checkProtectionSign(b, user, username);
            signs.put(loc, check);

            if (protectedBlocks.contains(b.getType()) && depth > 0) {
                getConnectedSigns(b, signs, user, username, depth - 1);
            }
        }
    }

    private SignProtectionState checkProtectionSign(final Block block, final User user, final String username) {
        if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
            final BlockSign sign = new BlockSign(block);
            if (sign.getLine(0).equals(this.getSuccessName())) {
                return checkProtectionSign(sign, user, username);
            }
        }
        return SignProtectionState.NOSIGN;
    }

    private SignProtectionState checkProtectionSign(final InterfaceSign sign, final User user, final String username) {
        if (user == null || username == null) {
            return SignProtectionState.NOT_ALLOWED;
        }
        if (user.isAuthorized("refinecraft.signs.protection.override")) {
            return SignProtectionState.OWNER;
        }
        if (FormatUtil.stripFormat(sign.getLine(3)).equalsIgnoreCase(username)) {
            return SignProtectionState.OWNER;
        }
        for (int i = 1; i <= 2; i++) {
            final String line = sign.getLine(i);
            if (line.startsWith("(") && line.endsWith(")") && user.inGroup(line.substring(1, line.length() - 1))) {
                return SignProtectionState.ALLOWED;
            } else if (line.equalsIgnoreCase(username)) {
                return SignProtectionState.ALLOWED;
            }
        }
        return SignProtectionState.NOT_ALLOWED;
    }

    private Block[] getAdjacentBlocks(final Block block) {
        return new Block[]{block.getRelative(BlockFace.NORTH), block.getRelative(BlockFace.SOUTH), block.getRelative(BlockFace.EAST), block.getRelative(BlockFace.WEST), block.getRelative(BlockFace.DOWN), block.getRelative(BlockFace.UP)};
    }

    public SignProtectionState isBlockProtected(final Block block, final User user, final String username, boolean secure) {
        final Map<Location, SignProtectionState> signs = getConnectedSigns(block, user, username, secure);
        SignProtectionState retstate = SignProtectionState.NOSIGN;
        for (SignProtectionState state : signs.values()) {
            if (state == SignProtectionState.ALLOWED) {
                retstate = state;
            } else if (state == SignProtectionState.NOT_ALLOWED && retstate != SignProtectionState.ALLOWED) {
                retstate = state;
            }
        }
        if (!secure || retstate == SignProtectionState.NOSIGN) {
            for (SignProtectionState state : signs.values()) {
                if (state == SignProtectionState.OWNER) {
                    return state;
                }
            }
        }
        return retstate;
    }

    public boolean isBlockProtected(final Block block) {
        final Block[] faces = getAdjacentBlocks(block);
        for (Block b : faces) {
            if (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
                final Sign sign = (Sign) b.getState();
                if (sign.getLine(0).equalsIgnoreCase("§1[Protection]")) {
                    return true;
                }
            }
            if (protectedBlocks.contains(b.getType())) {
                final Block[] faceChest = getAdjacentBlocks(b);

                for (Block a : faceChest) {
                    if (a.getType() == Material.SIGN_POST || a.getType() == Material.WALL_SIGN) {
                        final Sign sign = (Sign) a.getState();
                        if (sign.getLine(0).equalsIgnoreCase("§1[Protection]")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Set<Material> getBlocks() {
        return protectedBlocks;
    }

    @Override
    public boolean areHeavyEventRequired() {
        return true;
    }

    @Override
    protected boolean onBlockPlace(final Block block, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        for (Block adjBlock : getAdjacentBlocks(block)) {
            final SignProtectionState state = isBlockProtected(adjBlock, player, username, true);

            if ((state == SignProtectionState.ALLOWED || state == SignProtectionState.NOT_ALLOWED) && !player.isAuthorized("refinecraft.signs.protection.override")) {
                player.sendMessage(tl("noPlacePermission", block.getType().toString().toLowerCase(Locale.ENGLISH)));
                return false;
            }
        }
        return true;

    }

    @Override
    protected boolean onBlockInteract(final Block block, final User player, final String username, final InterfaceRefineCraft RC) throws SignException {
        final SignProtectionState state = isBlockProtected(block, player, username, false);

        if (state == SignProtectionState.OWNER || state == SignProtectionState.NOSIGN || state == SignProtectionState.ALLOWED) {
            return true;
        }

        if (state == SignProtectionState.NOT_ALLOWED && player.isAuthorized("refinecraft.signs.protection.override")) {
            return true;
        }


        player.sendMessage(tl("noAccessPermission", block.getType().toString().toLowerCase(Locale.ENGLISH)));
        return false;
    }

    @Override
    protected boolean onBlockBreak(final Block block, final User player, final String username, final InterfaceRefineCraft RC) throws SignException, MaxMoneyException {
        final SignProtectionState state = isBlockProtected(block, player, username, false);

        if (state == SignProtectionState.OWNER || state == SignProtectionState.NOSIGN) {
            checkIfSignsAreBroken(block, player, username, RC);
            return true;
        }

        if ((state == SignProtectionState.ALLOWED || state == SignProtectionState.NOT_ALLOWED) && player.isAuthorized("refinecraft.signs.protection.override")) {
            checkIfSignsAreBroken(block, player, username, RC);
            return true;
        }


        player.sendMessage(tl("noDestroyPermission", block.getType().toString().toLowerCase(Locale.ENGLISH)));
        return false;
    }

    @Override
    public boolean onBlockBreak(final Block block, final InterfaceRefineCraft RC) {
        final SignProtectionState state = isBlockProtected(block, null, null, false);

        return state == SignProtectionState.NOSIGN;
    }

    @Override
    public boolean onBlockExplode(final Block block, final InterfaceRefineCraft RC) {
        final SignProtectionState state = isBlockProtected(block, null, null, false);

        return state == SignProtectionState.NOSIGN;
    }

    @Override
    public boolean onBlockBurn(final Block block, final InterfaceRefineCraft RC) {
        final SignProtectionState state = isBlockProtected(block, null, null, false);

        return state == SignProtectionState.NOSIGN;
    }

    @Override
    public boolean onBlockIgnite(final Block block, final InterfaceRefineCraft RC) {
        final SignProtectionState state = isBlockProtected(block, null, null, false);

        return state == SignProtectionState.NOSIGN;
    }

    @Override
    public boolean onBlockPush(final Block block, final InterfaceRefineCraft RC) {
        final SignProtectionState state = isBlockProtected(block, null, null, false);

        return state == SignProtectionState.NOSIGN;
    }

    public enum SignProtectionState {
        NOT_ALLOWED, ALLOWED, NOSIGN, OWNER
    }
}
