package com.jooi.create.powerfulpanel.mixin;

import com.jooi.create.powerfulpanel.common.registries.CPPPackets;
import com.jooi.create.powerfulpanel.common.utilities.RecipeMultiplier;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelScreen;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.annotation.Nullable;

@Mixin(FactoryPanelScreen.class)
public abstract class MixinFactoryPanelScreen extends AbstractSimiScreen {

    @Shadow(remap = false)
    private FactoryPanelBehaviour behaviour;

    @Shadow(remap = false)
    private boolean restocker;

    @Shadow(remap = false)
    @Nullable
    private BigItemStack outputConfig;

    @Unique
    @Nullable
    private ScrollInput CPP$RecipeMultiplier;

    @Inject(
            method = "init",
            at = @At("RETURN")
    )
    private void CPP$onInit(CallbackInfo ci) {
        if (!(behaviour instanceof RecipeMultiplier rspl))
            return;

        int x = guiLeft;
        int y = guiTop;


        CPP$RecipeMultiplier = new ScrollInput(x + 34 - 3 + 2 + 2 + 2, y + windowHeight - 19 - 5, 16, 16)
                .withRange(-1, 64 * 100);


        CPP$RecipeMultiplier = CPP$RecipeMultiplier.withShiftStep(10);

        CPP$RecipeMultiplier.setState(rspl.CPP$getRecipeMultiplier());
        CPP$updateRecipeMultiplierLabel();

        addRenderableWidget(CPP$RecipeMultiplier);

    }

    @Unique
    private void CPP$updateRecipeMultiplierLabel() {
        if (CPP$RecipeMultiplier == null)
            return;

        String key = "gauge.cpp.recipe_multiplier";
        if (CPP$RecipeMultiplier.getState() == -1)
            key = key + ".none";

        CPP$RecipeMultiplier.titled(Component.translatable(key));
    }

    @Inject(
            method = "renderWindow",
            at = @At("RETURN"),
            remap = false
                                    )
    private void CPP$onRenderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!(behaviour instanceof RecipeMultiplier))
            return;

        if (CPP$RecipeMultiplier == null)
            return;

        int x = CPP$RecipeMultiplier.getX();
        int y = CPP$RecipeMultiplier.getY();
        int width = 16;
        int height = 16;

        graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF8B8B8B);

        graphics.fill(x - 1, y - 1, x + width, y, 0xFF373737);
        graphics.fill(x - 1, y - 1, x, y + height, 0xFF373737);

        graphics.fill(x, y + height, x + width + 1, y + height + 1,0xFFffffff);
        graphics.fill(x + width, y, x + width + 1, y + height + 1,0xFFffffff);

        GuiGameElement.of(Items.SHULKER_BOX)
                .scale(0.75f)
                .at(x + 2, y + 3)
                .render(graphics);

        ItemStack asStack = new ItemStack(Items.SHULKER_BOX);
        graphics.renderItem(asStack, x, y);

        int multiplier = CPP$RecipeMultiplier.getState();
        String displayText = (multiplier == -1) ? " ---" : "×" + multiplier;
        graphics.renderItemDecorations(font, asStack, x, y, displayText);

    }

    @Inject(
            method = "sendIt",
            at = @At("RETURN"),
            remap = false
    )
    private void CPP$onSendIt(CallbackInfo ci) {
        if (!(behaviour instanceof RecipeMultiplier) || CPP$RecipeMultiplier == null)
            return;

        new CPPPackets.UpdateRecipeMultiplier(behaviour.getPanelPosition(), CPP$RecipeMultiplier.getState()).send();
    }
}