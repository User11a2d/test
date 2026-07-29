package net.example.slrja.mixin;

import net.example.slrja.SlrJaPatch;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * LiteralContents は record クラスであり、フィールドの書き換えは
 * リフレクションでも Unsafe でも拒否されるため不可能。
 * 代わりに、テキストが読み出される visit メソッドをフックし、
 * 辞書にヒットした場合は日本語訳を代わりに消費者へ渡す。
 */
@Mixin(LiteralContents.class)
public abstract class ComponentLiteralMixin {

    private static int slrjapatch$translateCount = 0;

    @Inject(method = "visit(Lnet/minecraft/network/chat/FormattedText$ContentConsumer;)Ljava/util/Optional;",
            at = @At("HEAD"), cancellable = true)
    private <T> void slrjapatch$visitPlain(FormattedText.ContentConsumer<T> consumer,
                                           CallbackInfoReturnable<Optional<T>> cir) {
        String text = ((LiteralContents) (Object) this).text();
        String ja = SlrJaPatch.TranslationDictionary.get(text);
        if (ja != null) {
            if (slrjapatch$translateCount < 5) {
                slrjapatch$translateCount++;
                System.out.println("[slrjapatch] TRANSLATED: \"" + text + "\" -> \"" + ja + "\"");
            }
            cir.setReturnValue(consumer.accept(ja));
        }
    }

    @Inject(method = "visit(Lnet/minecraft/network/chat/FormattedText$StyledContentConsumer;Lnet/minecraft/network/chat/Style;)Ljava/util/Optional;",
            at = @At("HEAD"), cancellable = true)
    private <T> void slrjapatch$visitStyled(FormattedText.StyledContentConsumer<T> consumer,
                                            Style style,
                                            CallbackInfoReturnable<Optional<T>> cir) {
        String text = ((LiteralContents) (Object) this).text();
        String ja = SlrJaPatch.TranslationDictionary.get(text);
        if (ja != null) {
            cir.setReturnValue(consumer.accept(style, ja));
        }
    }
}
