package com.change_vision.astah.extension.plugin.svn_prototype;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;

import java.util.Enumeration;
import java.util.Locale;

import org.junit.Test;

public class MessagesTest {

    @Test
    public void testMessages() {
        Messages message = new Messages();
        assertThat(message, is(notNullValue()));
    }

    @Test
    public void testGetMessage() {
        String path = "com.change_vision.astah.extension.plugin.svn_prototype.message.message";
        Messages.setupForTest(path, this.getClass());
        String message = Messages.getMessage("info_message.message_variation", "TEST", "test", "テスト", "てすと", "ﾃｽﾄ");
        assertThat(message, not("テストメッセージ:TEST, test, テスト, てすと, ﾃｽﾄ"));
        assertThat(message, is("テストメッセージ:TEST,test,テスト,てすと,ﾃｽﾄ"));
    }

    @Test
    public void testProvideMessage() {
        Messages messages = new Messages();
        String message = messages.provideMessage("progress_default_message");
        assertThat(message, is("しばらくお待ちください"));
    }

    @Test
    public void testGetKeys() {
        Enumeration<String> keys = Messages.getKeys();
        int cnt = 0;
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            assertThat(key, is(notNullValue()));
            System.out.println((cnt + 1) + ". key:" + key);
            cnt++;
        }
        assertThat(cnt, is(75));
    }

    @Test
    public void testGetLocal() {
        Locale locale = Messages.getLocale();
        assertThat(locale, is(Locale.JAPANESE));
    }
}
