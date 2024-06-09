package dev.footer.gutils.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.footer.gutils.lib.Config;
import dev.footer.gutils.lib.Styler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings({"null", "resource", "ConstantConditions"})
public class InspectBiome implements Command<CommandSourceStack> {
    public static LiteralArgumentBuilder<CommandSourceStack> reg() {
        return Commands.literal("inspectBiome")
                .executes(new InspectBiome()).requires(src -> src.hasPermission(Config.config.inspectBiomePerm.getAsInt()))
                .then(Commands.literal("color").executes(InspectBiome::displayColorProps))
                .then(Commands.literal("tags").executes(InspectBiome::displayTags))
                .then(Commands.literal("features").executes(InspectBiome::displayFeatures))
                .then(Commands.literal("sounds").executes(InspectBiome::displaySounds))
//                .then(Commands.literal("mobs").executes(InspectBiome::displayMobs))
                ;
    }

    private static int displaySounds(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();
        Level level = p.level();
        BlockPos pos = p.getOnPos();
        Biome b = level.getBiome(pos).value();
        Component bName = Styler.formatBiome(level.getBiome(pos).getRegisteredName(), b);

        MutableComponent music = Component.literal("\n§cBackgroundMusic§a: ");
        if(b.getBackgroundMusic().isPresent()) {
            music.append("" + b.getBackgroundMusic().get());
        } else if(b.getBackgroundMusic().isEmpty()) {
            music.append("§bNone");
        }

        MutableComponent ambientLoop = Component.literal("\n§cAmbientLoop§a: ");
        if(b.getAmbientLoop().isPresent()) {
            ambientLoop.append("" + b.getAmbientLoop().get());
        } else if(b.getAmbientLoop().isEmpty()) {
            ambientLoop.append("§bNone");
        }

        MutableComponent props = Component.literal("")
                .append(bName)
                .append("\n§6Biome Sounds§f: ")
                .append(music)
                .append(ambientLoop)
                ;
        p.sendSystemMessage(props);
        return Command.SINGLE_SUCCESS;
    }

    public static int displayColorProps(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();
        if (p != null) {
            Level l = p.level();
            BlockPos pos = p.getOnPos();
            Biome b = l.getBiome(pos).value();
            Component bName = Styler.formatBiome(l.getBiome(pos).getRegisteredName(), b);

            Style fogCol = Style.EMPTY.withColor(b.getFogColor())
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "" + b.getFogColor()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy")))
                    ;
            Component fog = Component.literal("\n§cFogColor§a: ")
                    .append("" + b.getFogColor()).withStyle(fogCol);

            Style foliageCol = Style.EMPTY.withColor(b.getFoliageColor())
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "" + b.getFoliageColor()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy")))
                    ;
            Component foliage = Component.literal("\n§cFoliageColor§a: ")
                    .append("" + b.getFoliageColor()).withStyle(foliageCol);

            Style skyCol = Style.EMPTY.withColor(b.getSkyColor())
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "" + b.getSkyColor()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy")))
                    ;
            Component sky = Component.literal("\n§cSkyColor§a: ")
                    .append("" + b.getSkyColor()).withStyle(skyCol);

            Style waterCol = Style.EMPTY.withColor(b.getWaterColor())
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "" + b.getWaterColor()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy")))
                    ;
            Component water = Component.literal("\n§cWaterColor§a: ")
                    .append("" + b.getWaterColor()).withStyle(waterCol);

            Style wFogCol = Style.EMPTY.withColor(b.getWaterFogColor())
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "" + b.getWaterFogColor()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy")))
                    ;
            Component wFog = Component.literal("\n§cWaterFogColor§a: ")
                    .append("" + b.getWaterFogColor()).withStyle(wFogCol);

            Component msg = Component.literal("")
                    .append(bName)
                    .append("\n§6Biome Colors§f: ")
                    .append(fog)
                    .append(foliage)
                    .append(sky)
                    .append(water)
                    .append(wFog)
                    ;

            p.sendSystemMessage(msg);
        }

        return 0;
    }

    public static int displayTags(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();
        Level level = p.level();
        BlockPos pos = p.getOnPos();
        Holder<Biome> biome = level.getBiome(pos);
        Stream<TagKey<Biome>> tags = biome.tags();
        Component bName = Styler.formatBiome(level.getBiome(pos).getRegisteredName(), biome.value());
        if (tags.findAny().isPresent()) {

            Component tagComp = Styler.formatBiomeTags(biome);
            MutableComponent props = Component.literal("")
                    .append(bName)
                    .append("\n§6Biome Tags§f: ")
                    .append(tagComp)
                    ;
            p.sendSystemMessage(props);
        } else {
            p.sendSystemMessage(Component.literal("" + bName).append("§cNo biome tags found!"));
        }

        return 0;
    }

    public static int displayFeatures(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();
        Level l = p.level();
        BlockPos pos = p.getOnPos();
        Biome b = l.getBiome(pos).value();
        if (!b.getGenerationSettings().features().isEmpty()) {

            Component bName = Styler.formatBiome(l.getBiome(pos).getRegisteredName(), b);

            int amount = 0;
            MutableComponent props = Component.literal("");

            List<HolderSet<PlacedFeature>> features = b.getGenerationSettings().features();

            try {
                for (HolderSet<PlacedFeature> holderSet : features) {
                    if (holderSet.size() > 0) {
                        String placedFeature = holderSet.get(0).getRegisteredName();
                        Component feature = Component.literal("\n§c" + placedFeature).withStyle(
                                Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, placedFeature))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy")))
                        );
                        amount++;
                        props.append(feature);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                props.append("§4IndexOutOfBounds" + e);
            }

            p.sendSystemMessage(Component.literal("").append(bName).append("\n§6Biome Features§f: (§a" + amount + "§f)").append(props));
        }

        return 0;
    }


    /*TODO
    public static int displayMobs(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();
        Level level = p.level();
        BlockPos pos = p.getOnPos();
        Biome b = level.getBiome(pos).value();
        Component bName = Styler.formatBiome(level.getBiome(pos).getRegisteredName(), b);
            List<EntityType<?>> types = b.getMobSettings().getEntityTypes().stream().toList();
            MutableComponent entities = Component.literal("");

            for(EntityType<?> mob : types) {

                Component entity = Component.literal("\n§c" + mob.toShortString()).withStyle(
                        Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, mob.toShortString()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy")))
                );

                entities.append(entity);
            }

                MutableComponent props = Component.literal("")
                    .append(bName)
                    .append("\n§6Biome Mobs§f: ")
                    .append(entities);

            p.sendSystemMessage(props);

        return Command.SINGLE_SUCCESS;
    }
     */

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer p = ctx.getSource().getPlayer();
            if (p != null) {
                Level l = p.level();
                BlockPos pos = p.getOnPos();
                Biome b = l.getBiome(pos).value();
                Component bName = Styler.formatBiome(l.getBiome(pos).getRegisteredName(), b);

                Biome.Precipitation precipitation = b.getPrecipitationAt(pos);
                MutableComponent rain = Component.literal("\n§cPossiblePrecipitation§a: ");
                if(precipitation == Biome.Precipitation.RAIN) {
                    rain.append("§bRain");
                } else if(precipitation == Biome.Precipitation.SNOW) {
                    rain.append("§bSnow");
                } else if(precipitation == Biome.Precipitation.NONE) {
                    rain = Component.literal("\n§cHasPrecipitation§a: §bFalse");
                }

                MutableComponent coldEnough = Component.literal("\n§cColdEnoughToSnow§a: ");
                if(b.coldEnoughToSnow(pos)) {
                    coldEnough.append("§bTrue");
                } else {
                    coldEnough.append("§bFalse");
                }


                MutableComponent props = Component.literal("\n§6Biome Properties§f: ")
                        .append("\n§cBaseTemperature§a: §b" + b.getBaseTemperature() + "§aF")
                        .append("\n§cHeightAdjustedTemperature§a: §b" + b.getHeightAdjustedTemperature(pos) + "§aF")
                        .append(rain)
                        .append(coldEnough)
                        ;

                p.sendSystemMessage(
                        Component.literal("").append(bName).append(props)
                );
            }

        return 0;
    }
}
