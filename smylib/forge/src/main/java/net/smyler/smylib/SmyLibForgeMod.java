package net.smyler.smylib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smyler.smylib.proxy.SmyLibSidedProxy;
import org.apache.logging.log4j.Logger;

@Mod(modid = SmyLibForgeMod.MOD_ID)
public class SmyLibForgeMod {

    public static final String MOD_ID = "smylib";

    private Logger logger;

    @SidedProxy(
            modId = SmyLibForgeMod.MOD_ID,
            clientSide = "net.smyler.smylib.proxy.SmyLibClientProxy",
            serverSide = "net.smyler.smylib.proxy.SmyLibServerProxy"
    )
    public static SmyLibSidedProxy sidedProxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        SmyLibForgeMod.sidedProxy.init(this, event);
    }

    public Logger logger() {
        return this.logger;
    }

}
