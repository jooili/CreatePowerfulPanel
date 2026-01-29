package com.jooi.create.powerfulpanel;

import com.jooi.create.powerfulpanel.common.registries.CPPPackets;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;


@Mod(CreatePowerfulPanel.MODID)
public class CreatePowerfulPanel {
    public static final String MODID = "createpowerfulpanel";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    public CreatePowerfulPanel(IEventBus modEventBus, ModContainer modContainer) {
        onCtor(modEventBus, modContainer);
    }




    private void onCtor(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("CreatePowerfulPanel initializing!");

        REGISTRATE.registerEventListeners(modEventBus);

        modEventBus.addListener(CPPPackets::register);

        modEventBus.addListener(this::init);
    }

    private void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("CreatePowerfulPanel initialized");
        });
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }



}
