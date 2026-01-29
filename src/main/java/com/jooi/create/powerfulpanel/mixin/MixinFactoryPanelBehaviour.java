package com.jooi.create.powerfulpanel.mixin;

import com.jooi.create.powerfulpanel.common.utilities.RecipeMultiplier;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FactoryPanelBehaviour.class)
public abstract class MixinFactoryPanelBehaviour extends FilteringBehaviour implements RecipeMultiplier {

    @Unique
    private static final String CPP_Recipe_Multiplier_KEY = "CPP$RecipeMultiplier";

    @Unique
    private int CPP$RecipeMultiplier = -1;

    public MixinFactoryPanelBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
        super(be, slot);
    }

    public boolean CPP$hasRecipeMultiplier() {
        return CPP$RecipeMultiplier >= 0;
    }

    public int CPP$getRecipeMultiplier() {
        return CPP$RecipeMultiplier;
    }

    public void CPP$setRecipeMultiplier(int value) {
        if (value < 0)
            value = -1;
        CPP$RecipeMultiplier = value;
    }

    @Unique
    private void CPP$writeData(CompoundTag nbt) {
        var fpb = CPP$FPB();
        if (!fpb.active)
            return;

        String tagName = CreateLang.asId(fpb.slot.name());
        var tag = nbt.getCompound(tagName);

        tag.putInt(CPP_Recipe_Multiplier_KEY, CPP$RecipeMultiplier);
        nbt.put(tagName, tag);
    }

    @Inject(
            method = "write",
            at = @At("RETURN"),
            remap = false
    )
    private void CPP$onWrite(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        CPP$writeData(nbt);
    }

    @Inject(
            method = "read",
            at = @At("RETURN"),
            remap = false
    )
    private void Cpp$onRead(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        var fpb = CPP$FPB();
        if (!fpb.active)
            return;

        var tag = nbt.getCompound(CreateLang.asId(fpb.slot.name()));

        if (tag.contains(CPP_Recipe_Multiplier_KEY, CompoundTag.TAG_INT))
            this.CPP$setRecipeMultiplier(tag.getInt(CPP_Recipe_Multiplier_KEY));
        else
            this.CPP$setRecipeMultiplier(-1);
    }

    @Redirect(
            method = "tickRequests",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelConnection;amount:I",
                    opcode = org.objectweb.asm.Opcodes.GETFIELD
            ),
            remap = false
    )
    private int CPP$redirectConnectionAmount(FactoryPanelConnection connection) {
        int originalAmount = connection.amount;
        if (CPP$hasRecipeMultiplier()) {
            FactoryPanelBehaviour fpb = CPP$FPB();
            int limit = CPP$calculateEffectiveLimit(fpb);

            return originalAmount * limit;
        }
        return originalAmount;
    }

    @Redirect(
            method = "tickRequests", 
            at = @At(
                    value = "INVOKE", 
                    target = "Lcom/simibubi/create/content/logistics/stockTicker/PackageOrderWithCrafts;singleRecipe(Ljava/util/List;)Lcom/simibubi/create/content/logistics/stockTicker/PackageOrderWithCrafts;"
            ),
            remap = false
    )
    private PackageOrderWithCrafts CPP$redirectSingleRecipe(List<BigItemStack> pattern) {
        FactoryPanelBehaviour fpb = CPP$FPB();
        int recipeCount = CPP$calculateEffectiveLimit(fpb);
        return new PackageOrderWithCrafts(PackageOrder.empty(), List.of(new PackageOrderWithCrafts.CraftingEntry(new PackageOrder(pattern), recipeCount)));
    }

    @Redirect(
            method = "tickRequests",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/content/logistics/factoryBoard/FactoryPanelBehaviour;recipeOutput:I",
                    opcode = org.objectweb.asm.Opcodes.GETFIELD
            ),
            remap = false
    )
    private int CPP$redirectRecipeOutput(FactoryPanelBehaviour instance) {
        int originalOutput = instance.recipeOutput;
        if (CPP$hasRecipeMultiplier()) {
            int limit = CPP$calculateEffectiveLimit(instance);
            return originalOutput * limit;
        }
        return originalOutput;
    }

    @Unique
    private int CPP$calculateEffectiveLimit(FactoryPanelBehaviour fpb) {
        int targetCount = fpb.count * (fpb.upTo ? 1 : fpb.getFilter().getMaxStackSize());

        int promised = fpb.getPromised();
        int inStorage = fpb.getLevelInStorage();
        int remaining = Math.max(0, targetCount - promised - inStorage);

        if (remaining <= 0) {
            return 1;
        }

        int recipeOutput = fpb.recipeOutput;
        int neededRecipes = (remaining + recipeOutput - 1) / recipeOutput;

        int effectiveLimit = Math.min(CPP$getRecipeMultiplier(), neededRecipes);

        return Math.max(1, effectiveLimit);
    }

    @Unique
    private FactoryPanelBehaviour CPP$FPB() {
        // This is safe, because Mixins.
        return (FactoryPanelBehaviour) (Object) this;
    }

}
