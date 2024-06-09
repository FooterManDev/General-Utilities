package dev.footer.gutils.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.footer.gutils.lib.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"redundant", "cast", "resource"})
public class InspectBlock implements Command<CommandSourceStack> {
    public static LiteralArgumentBuilder<CommandSourceStack> reg() {
        return Commands.literal("inspectBlock")
                .executes(new InspectBlock()).requires(src -> src.hasPermission(Config.config.inspectBlockPerm.getAsInt()))
                .then(Commands.literal("sounds").executes(InspectBlock::displaySounds))
                .then(Commands.literal("tags").executes(InspectBlock::displayTags))
//                .then(Commands.literal("energy").executes(InspectBlock::displayEnergy))
                ;
    }

    private static BlockPos getTarget(CommandSourceStack src) {
        if (src.getEntity() instanceof Player p) {
            Vec3 eyePos = p.getEyePosition(1.0F);
            Vec3 lookDir = p.getLookAngle();
            double reachDist = 5.0D;
            Vec3 endPos = eyePos.add(lookDir.scale(reachDist));
            BlockHitResult res = p.level().clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p));
            if (res.getType() == HitResult.Type.BLOCK) {
                return res.getBlockPos();
            }
        }
        return null;
    }

    public static int displayTags(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        BlockPos pos = getTarget(ctx.getSource());
        if (pos != null && src.getEntity() instanceof Player p) {
            BlockState state = p.level().getBlockState(pos);
            Block b = state.getBlock();

            Component blockName = Styler.formatBlock(b);

            MutableComponent props = Component.literal("\n§6Block Tags§f: ");
            Component tags = Styler.formatBlockTags(b);

            Component msg = Component.literal("")
                    .append(blockName)
                    .append(props)
                    .append(tags);
            p.sendSystemMessage(msg);
        } else {
            src.sendSystemMessage(Component.literal("§cNo Block found!"));
        }
        return 0;
    }

    private static int displaySounds(CommandContext<CommandSourceStack> ctx) {
        BlockPos pos = getTarget(ctx.getSource());
        Map<String, Object> json = new HashMap<>();
        if (pos != null) {
            CommandSourceStack src = ctx.getSource();
            if (src.getEntity() instanceof Player p) {
                BlockState state = p.level().getBlockState(pos);
                Block b = state.getBlock();

                Component blockName = Styler.formatBlock(b);

                SoundType soundType = b.getSoundType(state, p.level(), pos, p);

                json.put("Hit", soundType.getHitSound().getLocation());
                json.put("Break", soundType.getBreakSound().getLocation());
                json.put("Step", soundType.getStepSound().getLocation());
                json.put("Place", soundType.getPlaceSound().getLocation());

                Component sounds = Component.literal("\n§6Sounds§f: ")
                        .append("\n§cHit§a: §b" + soundType.getHitSound().getLocation())
                        .append("\n§cBreak§a: §b" + soundType.getBreakSound().getLocation())
                        .append("\n§cStep§a: §b" + soundType.getStepSound().getLocation())
                        .append("\n§cPlace§a: §b" + soundType.getPlaceSound().getLocation());

                Component msg = Component.literal("")
                        .append(blockName)
                        .append(sounds);

                p.sendSystemMessage(msg);
            }
            return Command.SINGLE_SUCCESS;
        } else {
            ctx.getSource().sendSystemMessage(Component.literal("§cNo Block found!"));
        }
        return 0;
    }

    /*private static int displayEnergy(CommandContext<CommandSourceStack> ctx) {
        BlockPos pos = getTarget(ctx.getSource());
        if (pos != null) {
            CommandSourceStack src = ctx.getSource();
            if (src.getEntity() instanceof Player p) {
                BlockState state = p.level().getBlockState(pos);
                BlockEntity be = p.level().getBlockEntity(pos);
                Block b = state.getBlock();
                p.level().getCapability(Capabilities.EnergyStorage, pos)


                if(state.hasBlockEntity()) {
                    int storedEnergy = Capabilities.EnergyStorage.BLOCK.getCapability(p.level(), pos, state, be,)
                }
            }
        }
        return 0;
    }*/


    @Override
    public int run(CommandContext<CommandSourceStack> ctx) {
        Map<String, Object> json = new HashMap<>();
        BlockPos pos = getTarget(ctx.getSource());
        if (pos != null) {
            CommandSourceStack src = ctx.getSource();
            if (src.getEntity() instanceof Player p) {
                BlockState state = p.level().getBlockState(pos);
                Block b = state.getBlock();
                Level level = p.level();

                Component blockName = Styler.formatBlock(b);

                int light = b.getLightEmission(state, level, pos);
                String lightColors;
                if (light <= 3) {
                    lightColors = "§8"; // Dark Gray
                } else if (light <= 7) {
                    lightColors = "§6"; // Gold
                } else {
                    lightColors = "§e"; // Yellow
                }

                int flammability = state.getFlammability(level, pos, Direction.UP);
                String flameColors;
                if (flammability <= 5) {
                    flameColors = "§4"; // Dark Red
                } else if (flammability <= 10) {
                    flameColors = "§6"; // Gold
                } else {
                    flameColors = "§d"; // Purple
                }

                Explosion dummyExpl = new Explosion(level, null, pos.getX(), pos.getY(), pos.getZ(), 0,
                        false, Explosion.BlockInteraction.DESTROY);
                float explosionResistance = b.getExplosionResistance(state, level, pos, dummyExpl);

                json.put("Hardness", b.defaultDestroyTime());
                json.put("Friction", b.getFriction());
                json.put("ExplosionResistance", explosionResistance);
                json.put("JumpFactor", b.getJumpFactor());
                json.put("SpeedFactor", b.getSpeedFactor());
                json.put("LightEmission", light);
                json.put("Flammability", flammability);

                Component props = Component.literal("\n§6Properties§f: ")
                        .append("\n§cHardness§a: ").append("§b" + b.defaultDestroyTime() + "§aF")
                        .append("\n§cFriction§a: ").append("§b" + b.getFriction() + "§aF")
                        .append("\n§cExplosion Resistance§a: ").append("§b" + explosionResistance + "§aF")
                        .append("\n§cJump Factor§a: ").append("§b" + b.getJumpFactor() + "§aF")
                        .append("\n§cSpeed Factor§a: ").append("§b" + b.getSpeedFactor() + "§aF")
                        .append("\n§cLight Emission§a: ").append(lightColors + light)
                        .append("\n§cFlammability§a: ").append(flameColors + flammability)
                        ;

                Component msg = Component.literal("")
                        .append(blockName)
                        .append(props);

                p.sendSystemMessage(msg);
            }
            return Command.SINGLE_SUCCESS;
        } else {
            ctx.getSource().sendSystemMessage(Component.literal("§cNo Block found!"));
        }
        return 0;
    }
}
