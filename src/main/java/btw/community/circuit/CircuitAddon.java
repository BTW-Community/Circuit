package btw.community.circuit;

import btw.BTWAddon;
import net.fabricmc.circuit.CircuitInitializer;
import net.fabricmc.circuit.block.CircuitBlock;

public class CircuitAddon extends BTWAddon {

    public CircuitAddon() {
        super();
    }

    @Override
    public void preInitialize() {

    }

    @Override
    public void initialize() {
        CircuitBlock.initBlock();
        CircuitInitializer.initMod();
    }
}