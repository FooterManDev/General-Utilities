package dev.footer.gutils.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.footer.gutils.lib.Styler;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.function.UnaryOperator;

import static net.minecraft.world.item.Rarity.*;

@SuppressWarnings({"SpellCheckingInspection", "Redundant"})
public class InspectItem implements Command<CommandSourceStack> {

    public static LiteralArgumentBuilder<CommandSourceStack> reg() {
        return Commands.literal("inspectItem")
                .executes(new InspectItem())
                .then(Commands.literal("food").executes(InspectItem::displayFood))
                .then(Commands.literal("tags").executes(InspectItem::displayTags))
                .then(Commands.literal("enchants").executes(InspectItem::displayEnchants));
    }

    public static int displayTags(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        if (src.getEntity() instanceof Player p) {
            ItemStack stack = p.getMainHandItem();
            Component itemName = Styler.formatItem(stack.getItem());
            if (!stack.isEmpty()) {
                MutableComponent props = Component.literal("\n§6Item Tags§f: ");
                Component tags = Styler.formatItemTags(stack);
                Component msg = Component.literal("")
                        .append(itemName)
                        .append(props)
                        .append(tags);
                p.sendSystemMessage(msg);
            } else {
                p.sendSystemMessage(Component.literal("§cItem or Hand is Empty."));
            }
        }
        return 0;
    }

    public static int displayFood(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        if(src.getEntity() instanceof Player p) {
            ItemStack stack = p.getMainHandItem();
            if(!stack.isEmpty()) {
                if(stack.has(DataComponents.FOOD)) {

                    Component itemName = Styler.formatItem(stack.getItem());
                    var effectsList = Objects.requireNonNull(stack.getFoodProperties(p)).effects().stream().toList();
                    MutableComponent effects = Component.literal("");

//                for (FoodProperties.PossibleEffect effectInstance : stack.getFoodProperties(p).effects()) {
//                    MobEffect effect = effectInstance.effect().getEffect().value();
//                    int lvl = effectInstance.effect().getAmplifier() + 1;
//                    MutableComponent effectName = effect.getDisplayName().plainCopy();
//                    effects.append("\n§c" + effectName + "§a: ").append("§b" + lvl);
//                }

//                effectsList.forEach(e -> {
//                    Component effect = Styler.formatMobEffect(stack, p);
//                    effects.append(effect);
//                });

                    Component props = Component.literal("\n§6Food Properties§f: ")
                            .append("\n§cNutrition§a: ").append("§b" + Objects.requireNonNull(stack.getFoodProperties(p)).nutrition())
                            .append("\n§cSaturation§a: ").append("§b" + Objects.requireNonNull(stack.getFoodProperties(p)).saturation())
                            .append("\n§cCanAlwaysEat§a: ").append("§b" + Objects.requireNonNull(stack.getFoodProperties(p)).canAlwaysEat())
//                        .append("\n§cEffects§a: ").append("§b" + effects)
                            ;

                    Component msg = Component.literal("")
                            .append(itemName)
                            .append(props);

                    p.sendSystemMessage(msg);
                } else {
                    p.sendSystemMessage(Component.literal("§cItem does not have Food Component!"));
                }
            } else {
                p.sendSystemMessage(Component.literal("§cItem or Hand is Empty."));
            }
        }
        return 0;
    }

    public static int displayEnchants(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        if(src.getEntity() instanceof Player p) {
            ItemStack stack = p.getMainHandItem();
            if(!stack.isEmpty() && stack.isEnchanted()) {

                Component itemName = Styler.formatItem(stack.getItem());

                ItemEnchantments enchantments = stack.getEnchantments();
                MutableComponent enchants = Component.literal("\n§5Enchantments§f: ");

                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                        Enchantment enchantment = entry.getKey().value();
                        int lvl = entry.getIntValue();
                        enchants.append("\n§c" +
                                enchantment.getFullname(lvl).plainCopy().getString() + "§a: §d" + lvl);
                }

                Component msg = Component.literal("")
                        .append(itemName)
                        .append(enchants);

                p.sendSystemMessage(msg);
            } else if(!stack.isEnchanted() && !stack.isEmpty()) {
                p.sendSystemMessage(Component.literal("§cItem is not Enchanted!"));
            } else if(stack.isEmpty()) {
                p.sendSystemMessage(Component.literal("§cItem or Hand is Empty."));
            }
        }
        return 0;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        if(src.getEntity() instanceof Player p) {

            ItemStack stack = p.getMainHandItem();
            ItemAttributeModifiers attributes = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            MutableComponent atts = Component.literal("");

            if(!stack.isEmpty()) {

                Component itemName;
                if (stack.getItem() instanceof BlockItem) {
                    Block block = ((BlockItem) stack.getItem()).getBlock();
                    itemName = Styler.formatBlock(block);
                } else {
                    itemName = Styler.formatItem(stack.getItem());
                }

                UnaryOperator<Style> color = switch (stack.getRarity()) {
                    case COMMON -> COMMON.getStyleModifier();
                    case UNCOMMON -> UNCOMMON.getStyleModifier();
                    case RARE -> RARE.getStyleModifier();
                    case EPIC -> EPIC.getStyleModifier();
                };

                Component durability = Styler.formatDurability(stack);
                MutableComponent isDmgable = Component.literal("");
                if(stack.isDamageableItem()) {
                    isDmgable.append("\n§cDurability§a: ").append(durability).append(" §6/§b " + stack.getMaxDamage());
                }

                Integer attackdmg = stack.get(DataComponents.DAMAGE);

                if(!(attackdmg == null)) {
                    atts.append("\n§cAttackDamage§a: ").append("§b" + attackdmg);
                }

//                Collection<AttributeModifier> speedAtt = attributes.get(Attributes.ATTACK_SPEED)
//                if(!speedAtt.isEmpty()) {
//                    double speed = speedAtt.iterator().next().getAmount();
//                    double roundedSpeed = Math.round(speed * 10.0) / 10.0;
//                    atts.append("\n§cSpeed§a: ").append("§b" + roundedSpeed + "§aD");
//                }
//                Collection<AttributeModifier> attackDmgAtt = attributes.get(Attributes.ATTACK_DAMAGE);
//                if(!attackDmgAtt.isEmpty()) {
//                    double attackDmg = attackDmgAtt.iterator().next().getAmount();
//                    atts.append("\n§cAttackDamage§a: ").append("§b" + attackDmg + "§aD");
//                }

                Component props = Component.literal("\n§6Properties§f: ")
                        .append(isDmgable)
                        .append("\n§cMaxStackSize§a: ").append("§b" + stack.getMaxStackSize())
                        .append("\n§cEnchantability§a: ").append("§b" + stack.getEnchantmentValue())
                        .append("\n§cCanBarterWith§a: ").append("§b" + stack.isPiglinCurrency())
                        .append("\n§cIsRepairable§a: ").append("§b" + stack.isRepairable())
                        .append("\n§cRarity§a: ").append(Character.toUpperCase(stack.getRarity().toString().toLowerCase().charAt(0))
                                + stack.getRarity().toString().toLowerCase().substring(1)).withStyle(color)
                        ;

                Component msg = Component.literal("")
                        .append(itemName)
                        .append(props)
                        .append(atts);



                p.sendSystemMessage(msg);
            } else {
                p.sendSystemMessage(Component.literal("§cItem or Hand is Empty."));
            }
        }
        return 0;
    }
}
