package net.smyler.smylib.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.smyler.smylib.SmyLibForgeMod;

public interface SmyLibSidedProxy {

    void init(SmyLibForgeMod mod, FMLInitializationEvent event);

}
