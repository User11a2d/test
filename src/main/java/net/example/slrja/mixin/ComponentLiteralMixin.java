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
 * SLR1日本語化パッチ(最終版)
 * テキスト読み出し(visit)の瞬間に辞書を引き、日本語訳を代わりに渡す。
 */
@Mixin(LiteralContents.class)
public abstract class ComponentLiteralMixin {

    @Inject(method = "visit(Lnet/minecraft/network/chat/FormattedText$ContentConsumer;)Ljava/util/Optional;",
            at = @At("HEAD"), cancellable = true)
    private <T> void slrjapatch$visitPlain(FormattedText.ContentConsumer<T> consumer,
                                           CallbackInfoReturnable<Optional<T>> cir) {
        String ja = SlrJaPatch.TranslationDictionary.get(((LiteralContents) (Object) this).text());
        if (ja != null) {
            cir.setReturnValue(consumer.accept(ja));
        }
    }

    @Inject(method = "visit(Lnet/minecraft/network/chat/FormattedText$StyledContentConsumer;Lnet/minecraft/network/chat/Style;)Ljava/util/Optional;",
            at = @At("HEAD"), cancellable = true)
    private <T> void slrjapatch$visitStyled(FormattedText.StyledContentConsumer<T> consumer,
                                            Style style,
                                            CallbackInfoReturnable<Optional<T>> cir) {
        String ja = SlrJaPatch.TranslationDictionary.get(((LiteralContents) (Object) this).text());
        if (ja != null) {
            cir.setReturnValue(consumer.accept(style, ja));
        }
    }
}
