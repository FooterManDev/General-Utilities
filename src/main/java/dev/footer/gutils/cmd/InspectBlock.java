package dev.footer.gutils.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.footer.gutils.lib.Styler;
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

@SuppressWarnings({"redundant", "cast"})
public class InspectBlock implements Command<CommandSourceStack> {
    public static LiteralArgumentBuilder<CommandSourceStack> reg() {
        return Commands.literal("inspect_block")
                .executes(new InspectBlock())
                .then(Commands.literal("sounds").executes(InspectBlock::displayAllSounds))
                .then(Commands.literal("tags").executes(InspectBlock::displayTags))
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

            MutableComponent props = Component.literal("\n§6Tags§f: ");
            Component tags = Styler.formatBlockTags(b);
            Component msg = Component.literal("")
                    .append(blockName)
                    .append(props)
                    .append(tags);
            p.sendSystemMessage(msg);
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

                Component blockName = Styler.formatBlock(b);

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

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
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

                Component props = Component.literal("\n§6Properties§f: ")
                        .append("\n§cHardness§f: ").append("§b" + b.defaultDestroyTime() + "F")
                        .append("\n§cFriction§f: ").append("§b" + b.getFriction() + "F")
                        .append("\n§cExplosion Resistance§f: ").append("§b" + explosionResistance + "F")
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
}
