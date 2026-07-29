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
        System.out.println("[slrjapatch] DICTIONARY LOADED: " + TranslationDictionary.size() + " entries");
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
                    // 空の訳(未翻訳)は除外して登録
                    for (Map.Entry<String, String> e : loaded.entrySet()) {
                        if (e.getValue() != null && !e.getValue().isEmpty()) {
                            MAP.put(e.getKey(), e.getValue());
                        }
                    }
                }
            } catch (Throwable t) {
                System.out.println("[slrjapatch] FAILED TO LOAD DICTIONARY: " + t);
            }
        }

        public static String get(String english) {
            if (english == null) return null;
            // Mixinがmod初期化より先に動く場合に備え、未ロードならここでロード
            if (!loadAttempted) {
                load();
                System.out.println("[slrjapatch] LAZY DICTIONARY LOAD: " + MAP.size() + " entries");
            }
            return MAP.get(english);
        }

        public static int size() {
            return MAP.size();
        }
    }
}
