package net.example.slrja.mixin;

import net.example.slrja.SlrJaPatch;
import net.minecraft.network.chat.contents.LiteralContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

/**
 * すべてのリテラルテキスト(Component.literal("..."))は、最終的に
 * LiteralContents というクラスの内部フィールドに文字列を保持する形になる。
 *
 * 前バージョンは @Shadow でそのフィールド名を "text" と決め打ちしていたが、
 * 実行環境(難読化名)によってフィールド名が一致せずクラッシュしていた。
 *
 * このバージョンでは、コンストラクタ実行直後にリフレクションで
 * 「String型のフィールドを名前を問わず」探して書き換えるため、
 * フィールド名のズレ・refmapの有無に影響されず安定して動作する。
 */
@Mixin(LiteralContents.class)
public abstract class ComponentLiteralMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void slrjapatch$translate(String text, CallbackInfo ci) {
        if (text == null) return;
        String ja = SlrJaPatch.TranslationDictionary.get(text);
        if (ja == null) return;
        try {
            for (Field f : LiteralContents.class.getDeclaredFields()) {
                if (f.getType() == String.class) {
                    f.setAccessible(true);
                    f.set(this, ja);
                    break;
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // 書き換えに失敗した場合は元の英語のまま表示される(安全側に倒す)
        }
    }
}
