package com.change_vision.astah.extension.plugin.svn_prototype.task;

import static org.junit.Assert.*;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.Ignore;
import org.junit.Test;


public class SVNProgressTaskTest {

    @Test
    public void testSVNProgressTask() {
        SVNProgressTask spt = new SVNProgressTask(null);
        assertThat(spt, is(notNullValue()));
    }

    @Test
    @Ignore("GUIテスト")
    public void testDoInBackground() {
        SVNProgressTask spt = new SVNProgressTask(null);
        try {
            List<Integer> list = spt.doInBackground();
            assertThat(list, is(nullValue()));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    public void testFinishProgress() {
        SVNProgressTask spt = new SVNProgressTask(null);
        spt.finishProgress();
    }

}
