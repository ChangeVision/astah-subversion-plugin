package com.change_vision.astah.extension.plugin.svn_prototype;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import com.change_vision.jude.api.inf.ui.IMessageProvider;

public class Messages implements IMessageProvider {

    /**
     * デフォルトのプロパティファイルの配置場所
     */
    public static final String DEFAULT_BUNDLE = "com.change_vision.astah.extension.plugin.svn_prototype.message.message";

    /**
     * メッセージリソースバンドル
     */
    private static ResourceBundle INTERNAL_MESSAGES = ResourceBundle.getBundle(DEFAULT_BUNDLE, Locale.getDefault(),
            Messages.class.getClassLoader());

    /**
     * コンストラクタ
     */
    public Messages() {

    }

    /**
     * 別のプロパティファイルを読み込ませるテスト用のメソッド
     * 
     * @param bundlePath
     *            テスト用のプロパティファイル
     * @param clazz
     *            プロパティファイルが配置されているディレクトリに存在するクラス
     */
    static void setupForTest(String bundlePath, Class<?> clazz) {
        INTERNAL_MESSAGES = ResourceBundle.getBundle(bundlePath, Locale.getDefault(), clazz.getClassLoader());
    }

    /**
     * メッセージを取得します。
     * 
     * @param key
     *            プロパティファイルに記述したメッセージのキー
     * @param parameters
     *            メッセージに埋め込むプレースフォルダの値
     * @return　メッセージ
     */
    public static String getMessage(String key, Object... parameters) {
        String entry = INTERNAL_MESSAGES.getString(key);
        return MessageFormat.format(entry, parameters);
    }
    
    @Override
    public String provideMessage(String key, Object... parameters) {
        return getMessage(key, parameters);
    }

    static Enumeration<String> getKeys() {
        return INTERNAL_MESSAGES.getKeys();
    }

    static Locale getLocale() {
        return INTERNAL_MESSAGES.getLocale();
    }

}
