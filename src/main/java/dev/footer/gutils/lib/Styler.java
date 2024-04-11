package dev.footer.gutils.lib;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
This class is used to format various components in chat.
 */

public class Styler {

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

    public static Component formatItemTags(ItemStack stack) {
        MutableComponent tagsComponent = Component.literal("");
        Stream<TagKey<Item>> tags = stack.getTags();
        List<String> tagKeys = tags.map(TagKey::toString).toList();
        Map<String, List<String>> tagsByModId = new HashMap<>();

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

                        tagsByModId.computeIfAbsent(modid, k -> new ArrayList<>()).add(tagName);
                    }
                }
            }
        }

        for (Map.Entry<String, List<String>> entry : tagsByModId.entrySet()) {
            String modid = entry.getKey();
            List<String> tagNames = entry.getValue();

            Component namespace = Component.literal(modid).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.RED);

            for (String tagName : tagNames) {
                Component tag = Component.literal(tagName).withStyle(ChatFormatting.AQUA);
                Component colon = Component.literal(":").withStyle(ChatFormatting.GREEN);

                MutableComponent singleTag = Component.literal("").append(namespace).append(colon).append(tag);
                singleTag = singleTag.withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, singleTag.getString())));
                singleTag = singleTag.withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy"))));
                tagsComponent = tagsComponent.append("\n").append(singleTag);
            }
        }

        return tagsComponent;
    }

    public static Component formatBlockTags(Block b) {
        MutableComponent tagsComponent = Component.literal("");
        BlockState state = b.defaultBlockState();
        Stream<TagKey<Block>> tags = state.getTags();
        List<String> tagKeys = tags.map(TagKey::toString).toList();
        Map<String, List<String>> tagsByModId = new HashMap<>();

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

                        tagsByModId.computeIfAbsent(modid, k -> new ArrayList<>()).add(tagName);
                    }
                }
            }
        }

        for (Map.Entry<String, List<String>> entry : tagsByModId.entrySet()) {
            String modid = entry.getKey();
            List<String> tagNames = entry.getValue();

            Component namespace = Component.literal(modid).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.RED);

            for (String tagName : tagNames) {
                Component tag = Component.literal(tagName).withStyle(ChatFormatting.AQUA);
                Component colon = Component.literal(":").withStyle(ChatFormatting.GREEN);

                MutableComponent singleTag = Component.literal("").append(namespace).append(colon).append(tag);
                singleTag = singleTag.withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, singleTag.getString())));
                singleTag = singleTag.withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy"))));
                tagsComponent = tagsComponent.append("\n").append(singleTag);
            }
        }

        return tagsComponent;
    }

    public static Component formatDurability(ItemStack stack) {
        int barColor = stack.getBarColor();
        int maxDmg = stack.getMaxDamage();
        int remainingDurability = maxDmg - stack.getDamageValue();
        Style durabilityCol = Style.EMPTY.withColor(barColor);

        return Component.literal(String.valueOf(remainingDurability)).withStyle(durabilityCol);
    }
}
