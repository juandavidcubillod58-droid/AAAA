package com.tuserver.dupemod.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class DropDupeMixin {

    @Unique
    private static final Set<String> PERMITIDOS = Set.of("TuNombre", "NombreAmigo");

    @Unique
    private ItemStack dupemod$antes = ItemStack.EMPTY;

    @Inject(method = "dropSelectedItem", at = @At("HEAD"))
    private void antesDeTirar(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ItemStack mano = player.getMainHandStack();
        if (mano.isEmpty()) {
            dupemod$antes = ItemStack.EMPTY;
            return;
        }
        dupemod$antes = mano.copyWithCount(entireStack ? mano.getCount() : 1);
    }

    @Inject(method = "dropSelectedItem", at = @At("RETURN"))
    private void despuesDeTirar(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ItemStack copia = dupemod$antes;
        dupemod$antes = ItemStack.EMPTY;

        if (!cir.getReturnValue() || copia.isEmpty()) return;
        if (!player.isSneaking()) return;
        if (!PERMITIDOS.contains(player.getName().getString())) return;

        player.getInventory().offerOrDrop(copia);
    }
}
