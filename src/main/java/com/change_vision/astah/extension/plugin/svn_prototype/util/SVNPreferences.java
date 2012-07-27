package com.change_vision.astah.extension.plugin.svn_prototype.util;

import java.util.prefs.Preferences;

public abstract class SVNPreferences extends Preferences {

    // プロパティ保存キー
    public static final String KEY_REPOSITORY_URL = "svn_repository_url";
    public static final String KEY_USER_NAME      = "svn_user";
    public static final String KEY_PASSWORD       = "svn_password";
    public static final String KEY_MERGE_KIND     = "svn_merge_kind";
    public static final String KEY_ASTAH_HOME     = "svn_astah_home";
    public static final String KEY_KEYFILE_PATH   = "svn_keyfile_path";
    public static final String KEY_LOGIN_KIND     = "svn_login_kind";

    private static Preferences instance = null;

    public static Preferences getInstace(Class<?> clazz) {
        if (instance == null) {
            instance = Preferences.userNodeForPackage(clazz);
        }

        return instance;
    }

    public static Preferences getInstance() {
        return instance;
    }
}
