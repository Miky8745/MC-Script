package net.euport.mcscript.block.entity;

import net.euport.mcscript.TutorialMod;
import net.euport.mcscript.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TutorialMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<CPUBlockEntity>> CPU_BLOCK_BE =
            BLOCK_ENTITIES.register("cpu_block_be", () ->
                    BlockEntityType.Builder.of(CPUBlockEntity::new,
                            ModBlocks.CPU_BLOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
