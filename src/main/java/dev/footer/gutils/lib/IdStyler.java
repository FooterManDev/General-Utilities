package dev.footer.gutils.lib;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
This class is used to format Item/Block IDs.
 */

public class IdStyler {

    public static Component formatItem(Item item) {
        String s = item.getDescriptionId();
        String subtract = s.substring(5);
        String string = subtract.replaceFirst("\\.", ":");

        String[] parts = string.split(":");
        if (parts.length >= 2) {
            String modId = parts[0];
            String id = parts[1];

            Component namespace = Component.literal(modId).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.YELLOW);
            Component semi = Component.literal(":").withStyle(ChatFormatting.GOLD);
            Component name = Component.literal(id).withStyle(ChatFormatting.YELLOW);

            return Component.literal("").append(namespace).append(semi).append(name);
        }
        return Component.literal("Unknown Item");
    }

    public static Component formatBlock(Block block) {
        String s = block.getDescriptionId();
        String subtract = s.substring(6);
        String string = subtract.replaceFirst("\\.", ":");

        String[] parts = string.split(":");
        if (parts.length >= 2) {
            String modId = parts[0];
            String id = parts[1];

            Component namespace = Component.literal(modId).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.YELLOW);
            Component semi = Component.literal(":").withStyle(ChatFormatting.GOLD);
            Component name = Component.literal(id).withStyle(ChatFormatting.YELLOW);

            return Component.literal("").append(namespace).append(semi).append(name);
        }
        return Component.literal("Unknown Block");
    }

    public static Component formatTags(ItemStack stack) {
        MutableComponent tagsComponent = Component.literal("");
        Stream<TagKey<Item>> tags = stack.getTags();
        List<String> tagKeys = tags.map(TagKey::toString).toList();

        if (!tagKeys.isEmpty()) {
            for (String tagKey : tagKeys) {
                Pattern pattern = Pattern.compile("TagKey\\[.*?/(.*?)]");
                Matcher matcher = pattern.matcher(tagKey);

                if (matcher.find()) {
                    String tagPart = matcher.group(1);

                    String[] parts = tagPart.split(":");
                    if (parts.length >= 2) {
                        String modid = parts[0];
                        String tagName = parts[1];

                        String formattedTagName = tagName.replace("/", "§6/§r");

                        Component namespace = Component.literal(modid).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.RED);
                        Component tag = Component.literal(formattedTagName).withStyle(ChatFormatting.AQUA);
                        Component colon = Component.literal(":").withStyle(Style.EMPTY.withColor(0x7287fd));
                        tagsComponent = tagsComponent.append("\n").append(namespace).append(colon).append(tag);
                    }
                }
            }
        }

        return tagsComponent;
    }
}
