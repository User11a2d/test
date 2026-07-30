package net.example.slrja;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.common.Mod;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod("slrjapatch")
public class SlrJaPatch {

    public static final String MODID = "slrjapatch";

    public SlrJaPatch() {
        TranslationDictionary.load();
    }

    public static class TranslationDictionary {
        private static final Map<String, String> MAP = new HashMap<>();
        // "LVL:" のような、末尾がコロンのラベルだけを集めた軽量リスト
        private static final List<Map.Entry<String, String>> LABELS = new ArrayList<>();
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
                            if (e.getKey().endsWith(":") && e.getKey().length() <= 24) {
                                LABELS.add(e);
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        /**
         * 1) 完全一致
         * 2) 見えない制御文字などが先頭に紛れ込んでいる場合に備え、
         *    文字列の先頭付近(0〜3文字目)に既知のラベル("LVL:"など)が
         *    含まれていないか検索し、見つかればそのラベル部分だけ翻訳する。
         */
        public static String get(String english) {
            if (english == null || english.isEmpty()) return null;
            if (!loadAttempted) {
                load();
            }
            String full = MAP.get(english);
            if (full != null) return full;

            for (Map.Entry<String, String> label : LABELS) {
                String key = label.getKey();
                int idx = english.indexOf(key);
                if (idx >= 0 && idx <= 3) {
                    return english.substring(0, idx) + label.getValue()
                            + english.substring(idx + key.length());
                }
            }
            return null;
        }

        public static int size() {
            return MAP.size();
        }
    }
}
