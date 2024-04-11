package dev.footer.gutils;

import dev.footer.gutils.cmd.CmdRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static dev.footer.gutils.GeneralUtilities.ID;

@Mod(ID)
@SuppressWarnings("unused")
public class GeneralUtilities {
    public static final String ID = "gutils";
    public static final Logger LOGGER = LogManager.getLogger();


    public GeneralUtilities() {
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        NeoForge.EVENT_BUS.register(new CmdRegistry());
    }
}
