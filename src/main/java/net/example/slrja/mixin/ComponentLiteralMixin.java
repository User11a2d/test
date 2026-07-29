package net.example.slrja.mixin;

import net.example.slrja.SlrJaPatch;
import net.minecraft.network.chat.contents.LiteralContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(LiteralContents.class)
public abstract class ComponentLiteralMixin {

    private static sun.misc.Unsafe slrjapatch$unsafe;
    private static long slrjapatch$textOffset = -1L;
    private static int slrjapatch$hitCount = 0;
    private static int slrjapatch$translateCount = 0;
    private static boolean slrjapatch$announced = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void slrjapatch$translate(String text, CallbackInfo ci) {
        // フックが生きていることを最初の1回だけログに出す
        if (!slrjapatch$announced) {
            slrjapatch$announced = true;
            System.out.println("[slrjapatch] MIXIN HOOK IS ALIVE (first LiteralContents created)");
        }
        slrjapatch$hitCount++;

        if (text == null) return;
        String ja = SlrJaPatch.TranslationDictionary.get(text);
        if (ja == null) return;

        try {
            if (slrjapatch$unsafe == null) {
                Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                slrjapatch$unsafe = (sun.misc.Unsafe) theUnsafe.get(null);
                for (Field f : LiteralContents.class.getDeclaredFields()) {
                    if (f.getType() == String.class) {
                        slrjapatch$textOffset = slrjapatch$unsafe.objectFieldOffset(f);
                        break;
                    }
                }
                System.out.println("[slrjapatch] UNSAFE READY, field offset=" + slrjapatch$textOffset);
            }
            if (slrjapatch$textOffset != -1L) {
                slrjapatch$unsafe.putObject(this, slrjapatch$textOffset, ja);
                slrjapatch$translateCount++;
                if (slrjapatch$translateCount <= 5) {
                    System.out.println("[slrjapatch] TRANSLATED: \"" + text + "\" -> \"" + ja + "\"");
                }
            }
        } catch (Throwable t) {
            System.out.println("[slrjapatch] WRITE FAILED: " + t);
        }
    }
}
