package net.fabricmc.circuit;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.circuit.block.CircuitBlock;

public class Circuit implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		CircuitBlock.initBlock();
	}
}
