package com.change_vision.astah.extension.plugin.svn_prototype.task;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;

import java.util.List;
import java.util.prefs.Preferences;

import org.junit.Test;

import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNConfigurationDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.task.SVNDiffTask;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNPreferences;

public class SVNDiffTaskTest {
    private final String OLD_FILE_PATH = "./svn_prototype/src/test/resources/com/change_vision/astah/extension/plugin/svn_prototype/oldFile.asta";
    private final String NEW_FILE_PATH = "./svn_prototype/src/test/resources/com/change_vision/astah/extension/plugin/svn_prototype/newFile.asta";
    private final String ASTAH_PATH = "C:\\Program Files\\astah-professional\\";

    @Test
    public void testSVNDiffTaskStringString() {
        SVNDiffTask sdt = new SVNDiffTask(OLD_FILE_PATH, NEW_FILE_PATH);
        assertThat(sdt, is(notNullValue()));
    }

    @Test
    public void testSVNDiffTaskStringStringBoolean() {
        SVNDiffTask sdt = new SVNDiffTask(OLD_FILE_PATH, NEW_FILE_PATH, true);
        assertThat(sdt, is(notNullValue()));
    }

    @Test
    public void testDone1() {
        SVNDiffTask sdt = new SVNDiffTask(OLD_FILE_PATH, NEW_FILE_PATH, false);
        try {
            sdt.done();
        } catch (Exception e) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testDone2() {
        SVNDiffTask sdt = new SVNDiffTask(OLD_FILE_PATH, NEW_FILE_PATH, true);
        try {
            sdt.done();
        } catch (Exception e) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testDoInBackground() {
        Preferences sp = SVNPreferences.getInstace(SVNConfigurationDialog.class);
        sp.put(SVNPreferences.KEY_ASTAH_HOME, ASTAH_PATH);
        SVNDiffTask sdt = new SVNDiffTask(OLD_FILE_PATH, NEW_FILE_PATH, false);
        List<Integer> list;
        try {
            list = sdt.doInBackground();
            assertThat(list, is(nullValue()));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("throw ClassNotFoundException!");
        }
    }

    @Test
    public void testGetFinishFlg1() {
        SVNDiffTask sdt = new SVNDiffTask(OLD_FILE_PATH, NEW_FILE_PATH, false);
        boolean finishFlg = sdt.getFinishFlg();
        assertThat(finishFlg, is(false));
    }

    @Test
    public void testGetFinishFlg2() {
        Preferences sp = SVNPreferences.getInstace(SVNConfigurationDialog.class);
        sp.put(SVNPreferences.KEY_ASTAH_HOME, ASTAH_PATH);
        SVNDiffTask sdt = new SVNDiffTask(OLD_FILE_PATH, NEW_FILE_PATH, false);
        List<Integer> list;
        try {
            list = sdt.doInBackground();
            boolean finishFlg = sdt.getFinishFlg();
            assertThat(list, is(nullValue()));
            assertThat(finishFlg, is(true));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("throw ClassNotFoundException!");
        }
    }

    @Test
    public void testResetFinishFlg() {
        Preferences sp = SVNPreferences.getInstace(SVNConfigurationDialog.class);
        sp.put(SVNPreferences.KEY_ASTAH_HOME, ASTAH_PATH);
        SVNDiffTask sdt = new SVNDiffTask(OLD_FILE_PATH, NEW_FILE_PATH, false);
        List<Integer> list;
        try {
            list = sdt.doInBackground();
            boolean finishFlg = sdt.getFinishFlg();
            assertThat(list, is(nullValue()));
            assertThat(finishFlg, is(true));

            sdt.resetFinishFlg();

            finishFlg = sdt.getFinishFlg();
            assertThat(finishFlg, is(false));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("throw ClassNotFoundException!");
        }
    }
}
