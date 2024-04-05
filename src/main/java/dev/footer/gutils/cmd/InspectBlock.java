package dev.footer.gutils.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("deprecation")
public class InspectBlock {

    public static void reg(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("inspect_block")
                .executes(InspectBlock::displayProps)
                .then(Commands.literal("sounds").executes(InspectBlock::displayAllSounds))
        );
    }

    private static BlockPos getTarget(CommandSourceStack src) {
        if (src.getEntity() instanceof Player p) {
            Vec3 eyePos = p.getEyePosition(1.0F);
            Vec3 lookDir = p.getLookAngle();
            double reachDist = 5.0D;
            Vec3 endPos = eyePos.add(lookDir.scale(reachDist));
            HitResult res = p.level().clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p));
            if (res.getType() == HitResult.Type.BLOCK) {
                return ((BlockHitResult) res).getBlockPos();
            }
        }
        return null;
    }

    private static Component blockName(Block b) {
        String s = b.getDescriptionId();
        String subtract = s.substring(6);
        String string = subtract.replaceFirst("\\.", ":");

        String[] parts = string.split(":");
        if (parts.length >= 2) {
            String modId = parts[0];
            String blockId = parts[1];

            Component namespace = Component.literal(modId).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.YELLOW);
            Component semi = Component.literal(":").withStyle(ChatFormatting.GOLD);
            Component blockName = Component.literal(blockId).withStyle(ChatFormatting.YELLOW);

            return Component.literal("").append(namespace).append(semi).append(blockName);
        }
        return Component.literal("Unknown Block");
    }

    private static int displayProps(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BlockPos pos = getTarget(ctx.getSource());
        if (pos != null) {
            CommandSourceStack src = ctx.getSource();
            if (src.getEntity() instanceof Player p) {
                BlockState state = p.level().getBlockState(pos);
                Block b = state.getBlock();
                Level level = p.level();

                Component blockName = blockName(b);

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

                Component props = Component.literal("\n§6Properties§f: ")
                        .append("\n§cHardness§f: ").append("§b" + b.defaultDestroyTime() + "F")
                        .append("\n§cFriction§f: ").append("§b" + b.getFriction() + "F")
                        .append("\n§cExplosion Resistance§f: ").append("§b" + b.getExplosionResistance() + "F")
                        .append("\n§cJump Factor§f: ").append("§b" + b.getJumpFactor() + "F")
                        .append("\n§cSpeed Factor§f: ").append("§b" + b.getSpeedFactor() + "F")
                        .append("\n§cLight Emission§f: ").append(lightColors + light)
                        .append("\n§cFlammability§f: ").append(flameColors + flammability)
                        ;

                Component msg = Component.literal("")
                        .append(blockName)
                        .append(props);

                p.sendSystemMessage(msg);
            }
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int displayAllSounds(CommandContext<CommandSourceStack> ctx) {
        BlockPos pos = getTarget(ctx.getSource());
        if (pos != null) {
            CommandSourceStack src = ctx.getSource();
            if (src.getEntity() instanceof Player p) {
                BlockState state = p.level().getBlockState(pos);
                Block b = state.getBlock();

                Component blockName = blockName(b);

                SoundType soundType = b.getSoundType(state, p.level(), pos, p);

                Component sounds = Component.literal("\n§6Sounds§f: ")
                        .append("\nHit: §a" + soundType.getHitSound().getLocation())
                        .append("\nBreak: §a" + soundType.getBreakSound().getLocation())
                        .append("\nStep: §a" + soundType.getStepSound().getLocation())
                        .append("\nPlace: §a" + soundType.getPlaceSound().getLocation());

                Component msg = Component.literal("")
                        .append(blockName)
                        .append(sounds);

                p.sendSystemMessage(msg);
            }
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
}
