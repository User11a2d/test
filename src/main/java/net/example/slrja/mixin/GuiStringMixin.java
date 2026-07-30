package net.example.slrja.mixin;

import net.example.slrja.SlrJaPatch;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * drawString(生文字列描画)経路の翻訳。
 */
@Mixin(GuiGraphics.class)
public abstract class GuiStringMixin {

    @ModifyVariable(
            method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
            at = @At("HEAD"), argsOnly = true)
    private String slrjapatch$translateDrawString(String text) {
        if (text == null || text.isEmpty()) return text;
        String ja = SlrJaPatch.TranslationDictionary.get(text);
        return ja != null ? ja : text;
    }
}
