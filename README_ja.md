# SLR1 日本語化パッチMOD(伴走MOD)

## これは何か
SLR1 (Solo Craft: Reawakening) 内で `Component.literal("...")` として
直接ハードコードされている英語テキスト(スキル名・説明・戦闘メッセージ等)を、
SLR1本体のコードを一切改変せずに日本語へ差し替えるための **別MOD** です。
Mixinを使って `LiteralContents`(テキストの最終格納クラス)をフックし、
辞書(`translations_ja.json`)に一致する英文を日本語に置き換えます。

## 重要な前提・制限
- 私(Claude)の実行環境はネットワークが制限されており、Forge/Minecraftの
  ライブラリ配布元にアクセスできないため、**このプロジェクトを実際にビルド・動作確認まではできていません。**
  お手元のPC(Forge MDK + JDK17 + Gradle)でビルドしてください。
- Mixinのターゲット(`LiteralContents`のフィールド名など)はMinecraftの
  マッピングバージョンにより変わることがあります。IDE(IntelliJ IDEA等)で
  Forge開発環境をセットアップした際に、補完で正しいクラス/フィールド名を
  確認・修正してください。
- `translations_ja.json` は自動抽出した英語原文をキーにした**空のテンプレート**です。
  値(日本語訳)はご自身で埋める必要があります(下記参照)。
- 抽出処理はバイナリ(.classファイル)から文字列を機械的に拾っているため、
  一部の行に余分な文字が混入していたり(例: 文頭に1文字だけ余計な文字がある)、
  文が分割されて別キーになっている場合があります。使う前に軽く目視確認してください。
- Component.literal を経由しないテキスト(例: 独自のGUI描画で直接文字を描いている、
  ネットワークパケットで送られる特殊な文字列等)には効きません。その場合は
  README内の「代替案」を参照してください。

## 一番簡単なビルド方法: GitHub Actions(インストール不要)
1. https://github.com で無料アカウントを作成
2. 新しいリポジトリを作成(Newボタン)
3. このzipの中身(`ja_patch_mod`フォルダの中身)をリポジトリにアップロード
   (リポジトリページの「Add file」→「Upload files」でドラッグ&ドロップ可能)
4. アップロード後、リポジトリ上部の「Actions」タブを開く
5. 自動でビルドが始まります(数分)。終わったら実行結果を開き、
   ページ下部の「Artifacts」から `slr1-ja-patch` をダウンロード
6. 中に入っている `.jar` ファイルをSLR1本体と一緒に `mods` フォルダへ

## 手元でビルドする方法(上級者向け)
1. Forge公式サイトから **Minecraft 1.20.1 用 Forge MDK** をダウンロード
   https://files.minecraftforge.net/net/minecraftforge/forge/
2. MDKを展開し、本プロジェクトの `build.gradle` / `src` フォルダで上書き
3. `translations_ja.json` に日本語訳を記入(下記フォーマット参照)
4. ターミナルで `./gradlew build`(初回はライブラリDLで時間がかかります)
5. `build/libs/slr1-ja-patch-1.0.0.jar` が生成されるので、SLR1本体のjarと
   一緒に `mods` フォルダへ入れて起動

## translations_ja.json のフォーマット
```json
{
  "Awakening Approaches": "覚醒の兆し",
  "Ghost Step": "ゴーストステップ",
  "Not enough MP to sustain Frost Spiritualization.": "MPが不足しており氷結化を維持できません。"
}
```
キー(左側)は**MOD内の英語原文と完全一致**させる必要があります。
1文字でも違うと置換されません。

## 代替案(このMixinがうまく動かない場合)
1. `LiteralContents` のコンストラクタではなく、`Component.literal` の
   静的メソッドそのものへ `@Inject` する方法(build.gradle内のコメント参照)
2. ハードコードが集中している主要クラス(ShopMenu、各種スキルクラス等)を
   個別に特定し、クラス単位でMixinする方法(手間は大きいが確実)
3. SLR1が今後アップデートされ、langキー対応が進む可能性もあるため、
   作者への要望(GitHub Issue等)も並行して出しておくと良いです
