package net.smyler.smylib.proxy;

import net.smyler.smylib.SmyLibForgeMod;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public interface SmyLibSidedProxy {

    void init(SmyLibForgeMod mod, FMLInitializationEvent event);

}
