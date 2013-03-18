package com.change_vision.astah.extension.plugin.svn_prototype;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class SVNMergeTaskTest {
    private final String OLD_FILE_PATH = "C:\\pj_astah\\svn_prototype\\src\\test\\resources\\com\\change_vision\\astah\\extension\\plugin\\svn_prototype\\newFile.asta";

    @Test
    public void testSVNMergeTask() {
        try {
            SVNMergeTask smt = new SVNMergeTask(null, null, null, null);
            assertThat(smt, is(notNullValue()));
        } catch (SVNException e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    public void testSetSelected() {
        try {
            SVNMergeTask smt = new SVNMergeTask(null, null, null, null);
            smt.setSelected(0);
        } catch (SVNException e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    public void testSetLatestRevision() {
        try {
            SVNMergeTask smt = new SVNMergeTask(null, null, null, null);
            smt.setLatestRevision(0);
        } catch (SVNException e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    public void testSetSVNInfo() {
        try {
            SVNMergeTask smt = new SVNMergeTask(null, null, null, null);
            smt.setSVNInfo(null);
        } catch (SVNException e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    public void testDoInBackground() {
        try {
            SVNMergeTask smt = new SVNMergeTask(OLD_FILE_PATH, null, mock(SVNWCClient.class), mock(ProjectAccessor.class));
            try {
                smt.doInBackground();
            } catch (InvalidEditingException e) {
                e.printStackTrace();
                fail("Not yet implemented");
            } catch (LicenseNotFoundException e) {
                e.printStackTrace();
                fail("Not yet implemented");
            } catch (ProjectNotFoundException e) {
                e.printStackTrace();
                fail("Not yet implemented");
            } catch (NonCompatibleException e) {
                e.printStackTrace();
                fail("Not yet implemented");
            } catch (IOException e) {
                e.printStackTrace();
                fail("Not yet implemented");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                fail("Not yet implemented");
            } catch (ProjectLockedException e) {
                e.printStackTrace();
                fail("Not yet implemented");
            }
        } catch (SVNException e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    public void testGetSelected() {
        try {
            SVNMergeTask smt = new SVNMergeTask(null, null, null, null);
            int selected = smt.getSelected();
            assertThat(selected, is(0));
            smt.setSelected(2);
            selected = smt.getSelected();
            assertThat(selected, is(2));
        } catch (SVNException e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

}
