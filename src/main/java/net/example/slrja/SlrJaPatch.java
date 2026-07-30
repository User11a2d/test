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

@Mod("slrjapatch")
public class SlrJaPatch {

    public static final String MODID = "slrjapatch";

    public SlrJaPatch() {
        TranslationDictionary.load();
    }

    public static class TranslationDictionary {
        private static final Map<String, String> MAP = new HashMap<>();
        private static boolean loadAttempted = false;

        public static void load() {
            loadAttempted = true;
            try (Reader reader = new InputStreamReader(
                    SlrJaPatch.class.getResourceAsStream("/translations_ja.json"),
                    StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> loaded = new Gson().fromJson(reader, type);
                if (loaded != null) {
                    for (Map.Entry<String, String> e : loaded.entrySet()) {
                        if (e.getValue() != null && !e.getValue().isEmpty()) {
                            MAP.put(e.getKey(), e.getValue());
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        /**
         * 1) 完全一致で辞書を引く
         * 2) 一致しなければ「ラベル: 値」形式とみなし、コロンまでのラベル部分のみ翻訳
         *    (例: "LVL: 7" -> "レベル: 7")
         * どちらも該当しなければ null(=翻訳しない)
         */
        public static String get(String english) {
            if (english == null || english.isEmpty()) return null;
            if (!loadAttempted) {
                load();
            }
            String full = MAP.get(english);
            if (full != null) return full;

            int idx = english.indexOf(':');
            if (idx > 0 && idx < 24 && idx < english.length()) {
                String label = english.substring(0, idx + 1);
                String jaLabel = MAP.get(label);
                if (jaLabel != null) {
                    return jaLabel + english.substring(idx + 1);
                }
            }
            return null;
        }

        public static int size() {
            return MAP.size();
        }
    }
}
