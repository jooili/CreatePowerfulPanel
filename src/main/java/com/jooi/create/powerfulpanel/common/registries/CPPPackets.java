package com.jooi.create.powerfulpanel.common.registries;

import com.jooi.create.powerfulpanel.CreatePowerfulPanel;
import com.jooi.create.powerfulpanel.common.utilities.RecipeMultiplier;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.utility.AdventureUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class CPPPackets {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .executesOn(HandlerThread.MAIN);

        registrar.playToServer(UpdateRecipeMultiplier.TYPE, UpdateRecipeMultiplier.STREAM_CODEC, UpdateRecipeMultiplier::handle);
    }

    public record UpdateRecipeMultiplier(FactoryPanelPosition pos, int limit ) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<UpdateRecipeMultiplier> TYPE = new CustomPacketPayload.Type<>(
                CreatePowerfulPanel.asResource("update_recipe_multiplier")
        );

        public static final StreamCodec<ByteBuf, UpdateRecipeMultiplier> STREAM_CODEC = StreamCodec.composite(
                FactoryPanelPosition.STREAM_CODEC,
                UpdateRecipeMultiplier::pos,
                ByteBufCodecs.INT,
                UpdateRecipeMultiplier::limit,
                UpdateRecipeMultiplier::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(UpdateRecipeMultiplier message, IPayloadContext access) {
            var player = access.player();
            if (player == null || player.isSpectator() || AdventureUtil.isAdventure(player))
                return;

            var pos = message.pos;

            var level = player.level();
            if (!level.isLoaded(pos.pos()))
                return;

            if (!(level.getBlockEntity(pos.pos()) instanceof FactoryPanelBlockEntity be))
                return;

            var behavior = be.panels.get(pos.slot());
            if (!(behavior instanceof RecipeMultiplier ipl))
                return;

            boolean changed = false;

            if (ipl.CPP$getRecipeMultiplier() != message.limit) {
                changed = true;
                ipl.CPP$setRecipeMultiplier(message.limit);
            }
            if (changed)
                be.notifyUpdate();
        }
        public void send() {
            PacketDistributor.sendToServer(this);
        }
    }
}