package net.example.slrja.mixin;

import net.example.slrja.SlrJaPatch;
import net.minecraft.network.chat.contents.LiteralContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

/**
 * LiteralContents は record クラスであり、通常のリフレクション
 * (Field.set) では final フィールドを書き換えられない。
 * そのため sun.misc.Unsafe を用いてメモリ上の値を直接書き換える。
 */
@Mixin(LiteralContents.class)
public abstract class ComponentLiteralMixin {

    private static sun.misc.Unsafe slrjapatch$unsafe;
    private static long slrjapatch$textOffset = -1L;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void slrjapatch$translate(String text, CallbackInfo ci) {
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
            }
            if (slrjapatch$textOffset != -1L) {
                slrjapatch$unsafe.putObject(this, slrjapatch$textOffset, ja);
            }
        } catch (Throwable ignored) {
            // 失敗した場合は英語のまま表示(安全側)
        }
    }
}
