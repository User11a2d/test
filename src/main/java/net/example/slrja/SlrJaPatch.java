package net.example.slrja;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.common.Mod;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * SLR1 日本語化パッチMOD 本体。
 * 起動時に translations_ja.json (英語→日本語 の辞書) を読み込み、
 * TranslationDictionary に保持する。
 * 実際の文字列置換は mixin.ComponentLiteralMixin が行う。
 */
@Mod("slrjapatch")
public class SlrJaPatch {

    public static final String MODID = "slrjapatch";
    private static final Logger LOGGER = Logger.getLogger(MODID);

    public SlrJaPatch() {
        TranslationDictionary.load();
        LOGGER.info("[slrjapatch] loaded " + TranslationDictionary.size() + " translation entries");
    }

    /**
     * 英語原文 -> 日本語訳 の辞書を保持するクラス。
     * src/main/resources/translations_ja.json から読み込む。
     * JSON形式: { "英語の原文そのまま": "日本語訳", ... }
     */
    public static class TranslationDictionary {
        private static final Map<String, String> MAP = new HashMap<>();

        public static void load() {
            try (Reader reader = new InputStreamReader(
                    SlrJaPatch.class.getResourceAsStream("/translations_ja.json"),
                    StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> loaded = new Gson().fromJson(reader, type);
                if (loaded != null) {
                    MAP.putAll(loaded);
                }
            } catch (Exception e) {
                LOGGER.severe("[slrjapatch] failed to load translations_ja.json: " + e);
            }
        }

        /** 完全一致する翻訳があれば返す。無ければ null。 */
        public static String get(String english) {
            if (english == null) return null;
            return MAP.get(english);
        }

        public static int size() {
            return MAP.size();
        }
    }
}
