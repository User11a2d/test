package net.example.slrja.mixin;

import net.example.slrja.SlrJaPatch;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiGraphics.class)
public abstract class GuiStringMixin {

    private static int slrjapatch$rawLogCount = 0;

    @ModifyVariable(
            method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
            at = @At("HEAD"), argsOnly = true)
    private String slrjapatch$translateDrawString(String text) {
        if (text == null || text.isEmpty()) return text;

        // LVL/Name/Title/Job/FTGを含む行は、上限を無視して必ず記録する
        boolean isTarget = text.contains("LVL") || text.contains("Name:")
                || text.contains("Title:") || text.contains("Job:") || text.contains("FTG");

        String ja = SlrJaPatch.TranslationDictionary.get(text);
        if (ja != null) {
            System.out.println("[slrjapatch] drawString TRANSLATED: \"" + text + "\" -> \"" + ja + "\"");
            return ja;
        }

        if (isTarget) {
            System.out.println("[slrjapatch] TARGET STRING SEEN (no match): \"" + text + "\"");
        } else if (slrjapatch$rawLogCount < 5) {
            slrjapatch$rawLogCount++;
            System.out.println("[slrjapatch] drawString RAW (no match): \"" + text + "\"");
        }
        return text;
    }
}
