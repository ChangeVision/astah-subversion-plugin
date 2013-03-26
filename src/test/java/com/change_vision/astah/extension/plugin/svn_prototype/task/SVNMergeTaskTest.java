package com.change_vision.astah.extension.plugin.svn_prototype.task;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNConflictException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.task.SVNMergeTask;
import com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class SVNMergeTaskTest {
    private final String OLD_FILE_PATH = "./svn_prototype/src/test/resources/com/change_vision/astah/extension/plugin/svn_prototype/newFile.asta";

    @Test
    public void testSVNMergeTask() {
        SVNMergeTask smt = new SVNMergeTask(null, null, null);
        assertThat(smt, is(notNullValue()));
    }

    @Test
    public void testDoInBackground() {
        try {
            SVNMergeTask smt = new SVNMergeTask(OLD_FILE_PATH, mock(ISVNKitUtils.class), mock(ProjectAccessor.class));
            SVNUtils utils = mock(SVNUtils.class);
            smt.setSVNInfo(utils);
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
            } catch (SVNConflictException e) {
                e.printStackTrace();
                fail("throw SVNConflictException!");
            } catch (SVNPluginException e) {
                e.printStackTrace();
                fail("throw SVNPluginException!");
            }
        } catch (SVNException e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }
}
