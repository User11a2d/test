package net.example.slrja.mixin;

import net.example.slrja.SlrJaPatch;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * visit メソッドをフックし、辞書ヒット時は日本語訳を渡す。
 * 辞書にない英文は untranslated_strings.txt に記録する(採取モード)。
 */
@Mixin(LiteralContents.class)
public abstract class ComponentLiteralMixin {

    private static final Set<String> slrjapatch$missLogged = ConcurrentHashMap.newKeySet();
    private static final Path slrjapatch$missFile = Paths.get("untranslated_strings.txt");

    private static String slrjapatch$lookup(String text) {
        String ja = SlrJaPatch.TranslationDictionary.get(text);
        if (ja == null) {
            slrjapatch$logMiss(text);
        }
        return ja;
    }

    private static void slrjapatch$logMiss(String text) {
        if (text == null || text.length() < 4) return;
        // 英字を含む文章のみ記録(数字だけ・記号だけは除外)
        if (!text.matches(".*[A-Za-z]{3,}.*")) return;
        if (!slrjapatch$missLogged.add(text)) return; // 同じ文は一度だけ
        try {
            Files.writeString(slrjapatch$missFile, text.replace("\n", "\\n") + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
    }

    @Inject(method = "visit(Lnet/minecraft/network/chat/FormattedText$ContentConsumer;)Ljava/util/Optional;",
            at = @At("HEAD"), cancellable = true)
    private <T> void slrjapatch$visitPlain(FormattedText.ContentConsumer<T> consumer,
                                           CallbackInfoReturnable<Optional<T>> cir) {
        String text = ((LiteralContents) (Object) this).text();
        String ja = slrjapatch$lookup(text);
        if (ja != null) {
            cir.setReturnValue(consumer.accept(ja));
        }
    }

    @Inject(method = "visit(Lnet/minecraft/network/chat/FormattedText$StyledContentConsumer;Lnet/minecraft/network/chat/Style;)Ljava/util/Optional;",
            at = @At("HEAD"), cancellable = true)
    private <T> void slrjapatch$visitStyled(FormattedText.StyledContentConsumer<T> consumer,
                                            Style style,
                                            CallbackInfoReturnable<Optional<T>> cir) {
        String text = ((LiteralContents) (Object) this).text();
        String ja = slrjapatch$lookup(text);
        if (ja != null) {
            cir.setReturnValue(consumer.accept(style, ja));
        }
    }
}
