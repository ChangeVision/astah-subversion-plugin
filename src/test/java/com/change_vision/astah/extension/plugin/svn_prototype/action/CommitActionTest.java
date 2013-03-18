package com.change_vision.astah.extension.plugin.svn_prototype.action;

import static org.junit.Assert.*;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.Test;

import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class CommitActionTest {
    private final String SVN_FILE_PATH = "C:\\Documents and Settings\\kasaba\\デスクトップ\\svn_repository\\sample.asta";
    private final String NEW_FILE_PATH = "C:\\Documents and Settings\\kasaba\\デスクトップ\\svn_repository\\sample3.asta";

    @Test
    public void testRun() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetOpenProjectPath1() {
        CommitAction ca = new CommitAction();
        String path = ca.getOpenProjectPath();
        assertThat(path, is(nullValue()));
    }

    @Test
    public void testGetOpenProjectPath2() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        CommitAction ca = new CommitAction();
        String path = ca.getOpenProjectPath();
        assertThat(path, is(notNullValue()));
    }

    @Test
    public void testGetFileName() {
        CommitAction ca = new CommitAction();
        String fileName = ca.getFileName("C:\\tmp\\bbbbb.asta");
        assertThat(fileName, is("bbbbb.asta"));
    }

    @Test
    public void testInitializeSVNKit() {
        CommitAction ca = new CommitAction();
        try {
        ca.initializeSVNKit();
        } catch (Exception e) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testGetSVNUtils() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        CommitAction ca = new CommitAction();
        SVNUtils su = ca.getSVNUtils();
        assertThat(su, is(notNullValue()));
    }

    @Test
    public void testGetLatestRevision1() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        CommitAction ca = new CommitAction();
        String fileName = ca.getFileName(SVN_FILE_PATH);
        SVNUtils su = ca.getSVNUtils();
        int latest = (int)(ca.getLatestRevision(fileName, su));
        assertThat(latest, is(6));
    }

    @Test
    public void testGetLatestRevision2() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        CommitAction ca = new CommitAction();
        String fileName = ca.getFileName(SVN_FILE_PATH);
        SVNUtils su = ca.getSVNUtils();
        su.repository = null;
        su.user = null;
        int latest = (int)(ca.getLatestRevision(fileName, su));
        assertThat(latest, is(-1));
    }

    @Test
    public void testCheckConflict1() {
        long latestRevision = 1;
        long baseRevision   = 1;

        CommitAction ca = new CommitAction();
        boolean result = ca.checkConflict(baseRevision, latestRevision);
        assertThat(result, is(false));
    }

    @Test
    public void testCheckConflict2() {
        long latestRevision = 1;
        long baseRevision   = 2;

        CommitAction ca = new CommitAction();
        boolean result = ca.checkConflict(baseRevision, latestRevision);
        assertThat(result, is(true));
    }

    @Test
    public void testDisplayCommitComment() {
        CommitAction ca = new CommitAction();
        String comment = ca.displayCommitCommentWindow();
        assertThat(comment, is(notNullValue()));
        
    }

    @Test
    public void testNewRegistration() {
        ProjectAccessor pjAccessor = openProject(NEW_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        CommitAction ca = new CommitAction();
        boolean result = ca.newRegistration(NEW_FILE_PATH
                                          , ca.getFileName(NEW_FILE_PATH)
                                          , "JUnitテスト"
                                          , ca.getSVNUtils());
        assertThat(result, is(true));
        
    }

    private ProjectAccessor openProject(String path) {
        try {
            ProjectAccessor prjAccessor = ProjectAccessorFactory.getProjectAccessor();
            prjAccessor.open(path, true, false, true);
            return prjAccessor;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (LicenseNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NonCompatibleException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ProjectLockedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
