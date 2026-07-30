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

    private static int slrjapatch$callCount = 0;
    private static boolean slrjapatch$announced = false;

    @ModifyVariable(
            method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
            at = @At("HEAD"), argsOnly = true)
    private String slrjapatch$translateDrawString(String text) {
        if (!slrjapatch$announced) {
            slrjapatch$announced = true;
            System.out.println("[slrjapatch] GUISTRING MIXIN IS ALIVE");
        }
        slrjapatch$callCount++;

        if (text == null || text.isEmpty()) return text;

        String ja = SlrJaPatch.TranslationDictionary.get(text);
        if (ja != null) {
            if (slrjapatch$callCount <= 10) {
                System.out.println("[slrjapatch] drawString TRANSLATED: \"" + text + "\" -> \"" + ja + "\"");
            }
            return ja;
        }

        if (slrjapatch$callCount <= 30) {
            System.out.println("[slrjapatch] drawString RAW (no match): \"" + text + "\"");
        }
        return text;
    }
}
