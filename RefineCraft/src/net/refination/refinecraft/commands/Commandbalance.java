package net.refination.refinecraft.commands;

import net.refination.refinecraft.CommandSource;
import net.refination.refinecraft.User;
import net.refination.refinecraft.utils.NumberUtil;
import org.bukkit.Server;

import java.math.BigDecimal;

import static net.refination.refinecraft.I18n.tl;


public class Commandbalance extends RefineCraftCommand {
    public Commandbalance() {
        super("balance");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        User target = getPlayer(server, args, 0, true, true);
        sender.sendMessage(tl("balanceOther", target.isHidden() ? target.getName() : target.getDisplayName(), NumberUtil.displayCurrency(target.getMoney(), RC)));
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 1 && user.isAuthorized("refinecraft.balance.others")) {
            final User target = getPlayer(server, args, 0, true, true);
            final BigDecimal bal = target.getMoney();
            user.sendMessage(tl("balanceOther", target.isHidden() ? target.getName() : target.getDisplayName(), NumberUtil.displayCurrency(bal, RC)));
        } else if (args.length < 2) {
            final BigDecimal bal = user.getMoney();
            user.sendMessage(tl("balance", NumberUtil.displayCurrency(bal, RC)));
        } else {
            throw new NotEnoughArgumentsException();
        }
    }
}
