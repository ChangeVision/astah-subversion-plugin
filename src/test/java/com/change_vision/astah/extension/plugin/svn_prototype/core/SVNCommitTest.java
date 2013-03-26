package com.change_vision.astah.extension.plugin.svn_prototype.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNCommit;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNCommitCancelException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class SVNCommitTest {
    private final String SVN_FILE_PATH = "src/test/resources/sample.asta";
    private ProjectAccessor pjAccessor;
    private MessageDialog messageDialog;

    @Before
    public void before() {
        pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
        }

        messageDialog = mock(MessageDialog.class);
    }

    @Test
    public void testIsConflict1() {
        long latestRevision = 1;
        long baseRevision   = 1;

        SVNCommit ca = new SVNCommit(null, null);
        assertThat(ca.isConflict(baseRevision, latestRevision), is(false));
    }

    @Test
    public void testIsConflict2() {
        long latestRevision = 1;
        long baseRevision   = 2;

        SVNCommit ca = new SVNCommit(null, null);
        ca.setMessageDialog(messageDialog);
        assertThat(ca.isConflict(baseRevision, latestRevision), is(true));
    }

    @Test
    public void testDisplayCommitComment1() {
        SVNComment comment = mock(SVNComment.class);
        Mockito.when(comment.isCommit()).thenReturn(true);
        Mockito.when(comment.getCommitComment()).thenReturn("Commit Message!!");

        SVNCommit ca = new SVNCommit(comment, null);
        ca.setMessageDialog(messageDialog);
        try {
            assertThat(ca.displayCommitComment(), is("Commit Message!!"));
        } catch (SVNCommitCancelException e) {
            e.printStackTrace();
            fail("throw SVNCommitCancelException! " + e.getMessage());
        }
    }

    @Test(expected = SVNCommitCancelException.class)
    public void testDisplayCommitComment2() throws SVNCommitCancelException {
        MessageDialog dialog = mock(MessageDialog.class);
        SVNComment comment = mock(SVNComment.class);
        Mockito.when(comment.isCommit()).thenReturn(false);
        Mockito.when(comment.getCommitComment()).thenReturn("Commit Message!!");

        SVNCommit ca = new SVNCommit(comment, null);
        ca.setMessageDialog(dialog);
        ca.displayCommitComment();
    }

    private ProjectAccessor openProject(String path) {
        try {
            ProjectAccessor prjAccessor = ProjectAccessorFactory.getProjectAccessor();
            prjAccessor.open(new File(path).getAbsolutePath(), true, false, true);
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

    @After
    public void after() {
        pjAccessor.close();
    }
}
