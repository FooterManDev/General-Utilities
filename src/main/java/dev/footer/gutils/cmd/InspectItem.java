package dev.footer.gutils.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.footer.gutils.lib.IdStyler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class InspectItem implements Command<CommandSourceStack> {

    public static LiteralArgumentBuilder<CommandSourceStack> reg() {
        return Commands.literal("inspect_item")
                .executes(new InspectItem())
                .then(Commands.literal("food").executes(InspectItem::displayFood))
                .then(Commands.literal("tags").executes(InspectItem::displayTags))
                ;
    }

    public static int displayTags(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        if (src.getEntity() instanceof Player p) {
            ItemStack stack = p.getMainHandItem();
            Component itemName = IdStyler.formatItem(stack.getItem());
            if (!stack.isEmpty()) {
                MutableComponent props = Component.literal("\n§6Tags§f: ");
                Component tags = IdStyler.formatTags(stack);
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
            if(!stack.isEmpty()) {

                Component itemName = IdStyler.formatItem(stack.getItem());

                Component props = Component.literal("\n§6Food Properties§f: ")
                        .append("\n§cNutrition: ").append("§b" + Objects.requireNonNull(stack.getFoodProperties(p)).getNutrition())
                        .append("\n§cSaturation: ").append("§b" + Objects.requireNonNull(stack.getFoodProperties(p)).getSaturationModifier() + "F")
                        .append("\n§cEffects: ").append("§b" + Objects.requireNonNull(stack.getFoodProperties(p)).getEffects())
                        ;

                Component msg = Component.literal("")
                        .append(itemName)
                        .append(props);

                p.sendSystemMessage(msg);
            }
        }
        return 0;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        if(src.getEntity() instanceof Player p) {
            ItemStack stack = p.getMainHandItem();
            if(!stack.isEmpty()) {

                Component itemName = IdStyler.formatItem(stack.getItem());

                int damage = stack.getDamageValue();
                int maxDmg = stack.getMaxDamage();

                Component props = Component.literal("\n§6Properties§f: ")
                        .append("\n§cDamage: ").append("§b" + damage + " §6/ §b" + maxDmg)
                        ;

                Component msg = Component.literal("")
                        .append(itemName)
                        .append(props);

                p.sendSystemMessage(msg);
            }
        }
        return 0;
    }
}
