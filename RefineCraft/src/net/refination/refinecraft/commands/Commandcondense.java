package net.refination.refinecraft.commands;

import net.refination.refinecraft.ChargeException;
import net.refination.refinecraft.Trade;
import net.refination.refinecraft.Trade.OverflowType;
import net.refination.refinecraft.User;
import net.refination.api.MaxMoneyException;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

import static net.refination.refinecraft.I18n.tl;


public class Commandcondense extends RefineCraftCommand {
    public Commandcondense() {
        super("condense");
    }

    private Map<ItemStack, SimpleRecipe> condenseList = new HashMap<>();

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        List<ItemStack> is = new ArrayList<>();

        boolean validateReverse = false;
        if (args.length > 0) {
            is = RC.getItemDb().getMatching(user, args);
        } else {
            for (ItemStack stack : user.getBase().getInventory().getContents()) {
                if (stack == null || stack.getType() == Material.AIR) {
                    continue;
                }
                is.add(stack);
            }
            validateReverse = true;
        }

        boolean didConvert = false;
        for (final ItemStack itemStack : is) {
            if (condenseStack(user, itemStack, validateReverse)) {
                didConvert = true;
            }
        }
        user.getBase().updateInventory();

        if (didConvert) {
            user.sendMessage(tl("itemsConverted"));
        } else {
            user.sendMessage(tl("itemsNotConverted"));
            throw new NoChargeException();
        }
    }

    private boolean condenseStack(final User user, final ItemStack stack, final boolean validateReverse) throws ChargeException, MaxMoneyException {
        final SimpleRecipe condenseType = getCondenseType(stack);
        if (condenseType != null) {
            final ItemStack input = condenseType.getInput();
            final ItemStack result = condenseType.getResult();

            if (validateReverse) {
                boolean pass = false;
                for (Recipe revRecipe : RC.getServer().getRecipesFor(input)) {
                    if (getStackOnRecipeMatch(revRecipe, result) != null) {
                        pass = true;
                        break;
                    }
                }
                if (!pass) {
                    return false;
                }
            }

            int amount = 0;

            for (final ItemStack contents : user.getBase().getInventory().getContents()) {
                if (contents != null && contents.isSimilar(stack)) {
                    amount += contents.getAmount();
                }
            }

            int output = ((amount / input.getAmount()) * result.getAmount());
            amount -= amount % input.getAmount();

            if (amount > 0) {
                input.setAmount(amount);
                result.setAmount(output);
                final Trade remove = new Trade(input, RC);
                final Trade add = new Trade(result, RC);
                remove.charge(user);
                add.pay(user, OverflowType.DROP);
                return true;
            }
        }
        return false;
    }

    private SimpleRecipe getCondenseType(final ItemStack stack) {
        if (condenseList.containsKey(stack)) {
            return condenseList.get(stack);
        }

        final Iterator<Recipe> intr = RC.getServer().recipeIterator();
        while (intr.hasNext()) {
            final Recipe recipe = intr.next();
            final Collection<ItemStack> recipeItems = getStackOnRecipeMatch(recipe, stack);

            if (recipeItems != null && (recipeItems.size() == 4 || recipeItems.size() == 9) && (recipeItems.size() > recipe.getResult().getAmount())) {
                final ItemStack input = stack.clone();
                input.setAmount(recipeItems.size());
                final SimpleRecipe newRecipe = new SimpleRecipe(recipe.getResult(), input);
                condenseList.put(stack, newRecipe);
                return newRecipe;
            }
        }

        condenseList.put(stack, null);
        return null;
    }

    private Collection<ItemStack> getStackOnRecipeMatch(final Recipe recipe, final ItemStack stack) {
        final Collection<ItemStack> inputList;

        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe sRecipe = (ShapedRecipe) recipe;
            inputList = sRecipe.getIngredientMap().values();
        } else if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe slRecipe = (ShapelessRecipe) recipe;
            inputList = slRecipe.getIngredientList();
        } else {
            return null;
        }

        boolean match = true;
        Iterator<ItemStack> iter = inputList.iterator();
        while (iter.hasNext()) {
            ItemStack inputSlot = iter.next();
            if (inputSlot == null) {
                iter.remove();
                continue;
            }

            if (inputSlot.getDurability() == Short.MAX_VALUE) {
                inputSlot.setDurability((short) 0);
            }
            if (!inputSlot.isSimilar(stack)) {
                match = false;
            }
        }

        if (match) {
            return inputList;
        }
        return null;
    }


    private class SimpleRecipe implements Recipe {
        private ItemStack result;
        private ItemStack input;

        private SimpleRecipe(ItemStack result, ItemStack input) {
            this.result = result;
            this.input = input;
        }

        @Override
        public ItemStack getResult() {
            return result.clone();
        }

        public ItemStack getInput() {
            return input.clone();
        }
    }
}
