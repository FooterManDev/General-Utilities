package dev.footer.gutils.cmd;

import dev.footer.gutils.GeneralUtilities;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GeneralUtilities.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GUCommands {

    @SubscribeEvent
    public static void regCmds(RegisterCommandsEvent cmdE) {
        InspectBlock.reg(cmdE.getDispatcher());
    }
}
