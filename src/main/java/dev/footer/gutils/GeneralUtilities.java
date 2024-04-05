package dev.footer.gutils;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
    }
}
