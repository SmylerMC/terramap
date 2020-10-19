package fr.thesmyler.terramap.network.warps;

import io.netty.buffer.ByteBuf;

/**
 * Used to filter warps in multi warp requests
 * 
 * TODO Implement warp filtering
 * 
 * @author SmylerMC
 *
 */
public abstract class AbstractWarpFilter {
	
	public abstract void writeToByteBuf(ByteBuf buf);
	
	public static AbstractWarpFilter readFromByteBuf(ByteBuf buf) {
		return null;
	}

}
