package net.example.slrja.mixin;

import net.example.slrja.SlrJaPatch;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * SLR1のデイリークエスト画面などは、Component(LiteralContents)を経由せず
 * GuiGraphics.drawString(Font, String, ...) で生の文字列を直接描画している。
 * この経路でも辞書を引けるよう、drawStringのString引数を差し替える。
 */
@Mixin(GuiGraphics.class)
public abstract class GuiStringMixin {

    @ModifyVariable(
            method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
            at = @At("HEAD"), argsOnly = true)
    private String slrjapatch$translateDrawString(String text) {
        if (text == null) return null;
        String ja = SlrJaPatch.TranslationDictionary.get(text);
        return ja != null ? ja : text;
    }
}
