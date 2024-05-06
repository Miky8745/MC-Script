package net.euport.mcscript.events;

import net.euport.mcscript.MCScript;
import net.euport.mcscript.block.entity.CPUBlockEntity;
import net.euport.mcscript.custom.Utils;
import net.euport.mcscript.custom.ram.RAM;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

@Mod.EventBusSubscriber(modid = MCScript.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Save {
    @SubscribeEvent
    public static void save(LevelEvent.Save event) {
        RAM.saveState(CPUBlockEntity.ram, new File(Utils.MEMORY_STATE_URI));
    }
}
