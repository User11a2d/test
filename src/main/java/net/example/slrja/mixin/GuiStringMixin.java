package net.example.slrja.mixin;

import net.example.slrja.SlrJaPatch;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * drawString(生文字列描画)経路の翻訳。
 * 1) まず文字列全体で辞書を引く(完全一致)
 * 2) 一致しなければ「ラベル: 値」形式とみなし、コロンまでのラベル部分だけ辞書を引いて
 *    訳せたらラベルのみ差し替える(例: "LVL: 7" -> "レベル: 7")
 */
@Mixin(GuiGraphics.class)
public abstract class GuiStringMixin {

    @ModifyVariable(
            method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
            at = @At("HEAD"), argsOnly = true)
    private String slrjapatch$translateDrawString(String text) {
        if (text == null || text.isEmpty()) return text;

        // 1) 完全一致
        String ja = SlrJaPatch.TranslationDictionary.get(text);
        if (ja != null) return ja;

        // 2) ラベル部分一致 ("XXX:" までを辞書で引く)
        int idx = text.indexOf(':');
        if (idx > 0 && idx < 24) {
            String label = text.substring(0, idx + 1); // 例 "LVL:"
            String jaLabel = SlrJaPatch.TranslationDictionary.get(label);
            if (jaLabel != null) {
                return jaLabel + text.substring(idx + 1);
            }
        }
        return text;
    }
}
