package net.example.slrja.mixin;

import net.example.slrja.SlrJaPatch;
import net.minecraft.network.chat.contents.LiteralContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * すべてのリテラルテキスト(Component.literal("..."))は、最終的に
 * LiteralContents というクラスの text フィールドを保持する形になる。
 * ここへコンストラクタ注入し、辞書に一致する英語原文を日本語訳に
 * 差し替える。
 *
 * これにより SLR1 本体のクラスには一切手を加えず、
 * GUIタイトル・ツールチップ・スキル説明・戦闘メッセージなど
 * Component.literal 経由で表示されるテキストをまとめて日本語化できる。
 *
 * ※ LiteralContents のフィールド名・シグネチャはMCPバージョンにより
 *   変わる場合があるため、実際にコンパイルする際はIDEの補完で
 *   正しいフィールド名を確認してください(1.20.1 official mappingsでは "text")。
 */
@Mixin(LiteralContents.class)
public abstract class ComponentLiteralMixin {

    @Mutable
    @Shadow
    private String text;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void slrjapatch$translate(String text, CallbackInfo ci) {
        String ja = SlrJaPatch.TranslationDictionary.get(text);
        if (ja != null) {
            this.text = ja;
        }
    }
}
