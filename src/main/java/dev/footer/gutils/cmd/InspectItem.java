package dev.footer.gutils.cmd;

import com.google.common.collect.Multimap;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.footer.gutils.lib.Styler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

import static net.minecraft.world.item.Rarity.*;

@SuppressWarnings({"SpellCheckingInspection", "Redundant"})
public class InspectItem implements Command<CommandSourceStack> {

    public static LiteralArgumentBuilder<CommandSourceStack> reg() {
        return Commands.literal("inspect_item")
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
                MutableComponent props = Component.literal("\n§6Tags§f: ");
                Component tags = Styler.formatItemTags(stack);
                Component msg = Component.literal("")
                        .append(itemName)
                        .append(props)
                        .append(tags);
                p.sendSystemMessage(msg);
            }
        }
        return 0;
    }

    public static int displayFood(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        if(src.getEntity() instanceof Player p) {
            ItemStack stack = p.getMainHandItem();
            if(!stack.isEmpty() && stack.isEdible()) {

                Component itemName = Styler.formatItem(stack.getItem());

                Component props = Component.literal("\n§6Food Properties§f: ")
                        .append("\n§cNutrition§a: ").append("§b" + Objects.requireNonNull(stack.getFoodProperties(p)).getNutrition())
                        .append("\n§cSaturation§a: ").append("§b" + Objects.requireNonNull(stack.getFoodProperties(p)).getSaturationModifier() + "F")
                        .append("\n§cEffects§a: ").append("§b" + Objects.requireNonNull(stack.getFoodProperties(p)).getEffects())
                        ;

                Component msg = Component.literal("")
                        .append(itemName)
                        .append(props);

                p.sendSystemMessage(msg);
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

                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                MutableComponent enchants = Component.literal("\n§5Enchantments§f: ");

                    enchantments.forEach((enchantment, level) -> enchants.append("\n§c" +
                            enchantment.getFullname(level).plainCopy().getString() + "§a: §d" + level));

                Component msg = Component.literal("")
                        .append(itemName)
                        .append(enchants);

                p.sendSystemMessage(msg);
            }
        }
        return 0;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        if(src.getEntity() instanceof Player p) {

            ItemStack stack = p.getMainHandItem();
            Multimap<Attribute, AttributeModifier> attributes = stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
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

                Collection<AttributeModifier> speedAtt = attributes.get(Attributes.ATTACK_SPEED);
                if(!speedAtt.isEmpty()) {
                    double speed = speedAtt.iterator().next().getAmount();
                    double roundedSpeed = Math.round(speed * 10.0) / 10.0;
                    atts.append("\n§cSpeed§a: ").append("§b" + roundedSpeed + "§aD");
                }
                Collection<AttributeModifier> attackDmgAtt = attributes.get(Attributes.ATTACK_DAMAGE);
                if(!attackDmgAtt.isEmpty()) {
                    double attackDmg = attackDmgAtt.iterator().next().getAmount();
                    atts.append("\n§cAttackDamage§a: ").append("§b" + attackDmg + "§aD");
                }

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
            }
        }
        return 0;
    }
}
