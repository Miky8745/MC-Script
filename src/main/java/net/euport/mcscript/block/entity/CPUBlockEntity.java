package net.euport.mcscript.block.entity;

import net.euport.mcscript.custom.RAM;
import net.euport.mcscript.custom.Utils;
import net.euport.mcscript.item.ModItems;
import net.euport.mcscript.screen.CPUBlockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.euport.mcscript.custom.Utils.handleOutput;
import static net.euport.mcscript.custom.Utils.print;

public class CPUBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;
    private static int power = 0;
    private static int tickCounter = 0;
    public static RAM ram = new RAM(16);

    public CPUBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CPU_BLOCK_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CPUBlockEntity.this.progress;
                    case 1 -> CPUBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CPUBlockEntity.this.progress = pValue;
                    case 1 -> CPUBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.mcscript.cpu_block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CPUBlockMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("cpu_block.progress", progress);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("cpu_block.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        //print(String.valueOf(getPower(pLevel.getBlockState(pPos), pLevel, pPos)));
        tickCounter++;
        pLevel.updateNeighborsAt(pPos, pState.getBlock());

        if(hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if(hasProgressFinished()) {
                try {
                    Utils.loadProgram(this.itemHandler.getStackInSlot(INPUT_SLOT).getDisplayName().getString());
                    craftItem();
                    resetProgress();
                } catch (Exception e) {
                    print(e.getMessage());
                }
            }
        } else {
            resetProgress();
        }

        if (tickCounter == 19) {
            power = 0;
        }

        else if(tickCounter > 20) {
            tickCounter = 0;
            try {
                if (hasProgram()) {
                    int in = getPower(pLevel.getBlockState(pPos), pLevel, pPos);
                    String[] params = {String.valueOf(in)};
                    String[] generatedOutput = Utils.runProgram(params);
                    //print(generatedOutput[0]);
                    handleOutput(generatedOutput);
                }
            } catch (Exception e) {
                print(e.getMessage());
            }
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        ItemStack result = new ItemStack(ModItems.PROGRAM.get(), 1);
        this.itemHandler.extractItem(INPUT_SLOT, 1, false);

        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean hasRecipe() {
        boolean hasCraftingItem = this.itemHandler.getStackInSlot(INPUT_SLOT).getItem() == ModItems.SOURCE_CODE.get();
        ItemStack result = new ItemStack(ModItems.PROGRAM.get());

        return hasCraftingItem && canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasProgram() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == ModItems.PROGRAM.get();
    }

    private int getPower(BlockState pState, Level pLevel, BlockPos pPos) {
        if (pState.hasBlockEntity()) {
            if (pLevel.getBlockEntity(pPos) instanceof CPUBlockEntity CPUBlockEntity) {
                if (CPUBlockEntity.level != null) {
                    return CPUBlockEntity.level.getBestNeighborSignal(pPos);
                }
            }
        }
        return 0;
    }

    public static int getOutput(@NotNull BlockGetter level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            if (level.getBlockEntity(pos) instanceof CPUBlockEntity CPUBlockEntity) {
                if(CPUBlockEntity.level != null) {
                    return power;
                }
            }
        }
        return 0;
    }
}
