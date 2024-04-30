package net.euport.mcscript.item;

import net.euport.mcscript.TutorialMod;
import net.euport.mcscript.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TutorialMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATIVE_MODE_TABS.register("mcscript_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.PROGRAM.get()))
                    .title(Component.translatable("creativetab.mcscript_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.PROGRAM.get());
                        pOutput.accept(ModItems.SOURCE_CODE.get());

                        pOutput.accept(ModBlocks.CPU_BLOCK.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
