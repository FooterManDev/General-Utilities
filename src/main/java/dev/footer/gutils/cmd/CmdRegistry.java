package dev.footer.gutils.cmd;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CmdRegistry {


    @SubscribeEvent
    public void registerCmds(RegisterCommandsEvent rCmds) {
        rCmds.getDispatcher().register(InspectBlock.reg());
        rCmds.getDispatcher().register(InspectItem.reg());
    }
}
