package com.change_vision.astah.extension.plugin.svn_prototype.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNCommit;
import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNCommitCancelException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class SVNCommitTest {
    private final String SVN_FILE_PATH = "src/test/resources/sample.asta";
//    private final String NEW_FILE_PATH = "src/test/resources/sample3.asta";
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
    @Ignore("GUIテスト")
    public void testGetOpenProjectPath1() {
        SVNCommit ca = new SVNCommit(null, null);
        ca.setMessageDialog(messageDialog);
        String path;
        try {
            path = (new SVNUtils()).getOpenProjectPath(ProjectAccessorFactory.getProjectAccessor());
            assertThat(path, is(nullValue()));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException! " + e.getMessage());
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
            fail("throw ProjectNotFoundException! " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("throw ClassNotFoundException! " + e.getMessage());
        }
    }

    @Test
    @Ignore("GUIテスト")
    public void testGetOpenProjectPath2() {
        SVNCommit ca = new SVNCommit(null, null);
        ca.setMessageDialog(messageDialog);
        String path;
        try {
            path = (new SVNUtils()).getOpenProjectPath(ProjectAccessorFactory.getProjectAccessor());
            assertThat(path, is(notNullValue()));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException! " + e.getMessage());
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
            fail("throw ProjectNotFoundException! " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("throw ClassNotFoundException! " + e.getMessage());
        }
    }

    @Test
    public void testGetFileName() {
        SVNCommit ca = new SVNCommit(null, null);
        ca.setMessageDialog(messageDialog);
        String fileName = SVNUtils.getFileName("C:\\tmp\\bbbbb.asta");
        assertThat(fileName, is("bbbbb.asta"));
    }

    @Test
    @Ignore("getSVNUtils delete")
    public void testGetSVNUtils() {
        SVNCommit ca = new SVNCommit(null, null);
        ca.setMessageDialog(messageDialog);
//        SVNUtils su = ca.getSVNUtils();
//        assertThat(su, is(notNullValue()));
    }

    @Test
    public void testCheckConflict1() {
        long latestRevision = 1;
        long baseRevision   = 1;

        SVNCommit ca = new SVNCommit(null, null);
        ca.setMessageDialog(messageDialog);
        boolean result = ca.checkConflict(baseRevision, latestRevision);
        assertThat(result, is(false));
    }

    @Test
    public void testCheckConflict2() {
        long latestRevision = 1;
        long baseRevision   = 2;

        SVNCommit ca = new SVNCommit(null, null);
        ca.setMessageDialog(messageDialog);
        boolean result = ca.checkConflict(baseRevision, latestRevision);
        assertThat(result, is(true));
    }

    @Test
    @Ignore("GUIテスト")
    public void testDisplayCommitComment() {
        SVNCommit ca = new SVNCommit(null, null);
        ca.setMessageDialog(messageDialog);
        String comment;
        try {
            comment = ca.displayCommitComment();
            assertThat(comment, is(notNullValue()));
        } catch (SVNCommitCancelException e) {
            e.printStackTrace();
            fail("throw SVNCommitCancelException!");
        }
    }

//    @Test
//    @Ignore("実リポジトリを作らないといけないのでスキップ")
//    public void testNewRegistration() {
//        SVNCommit ca = new SVNCommit(null, null);
//        ca.setMessageDialog(messageDialog);
//        boolean result;
//        try {
//            result = ca.newRegistration(NEW_FILE_PATH
//                                              , SVNUtils.getFileName(NEW_FILE_PATH)
//                                              , "JUnitテスト");
//            assertThat(result, is(true));
//        } catch (SVNPluginException e) {
//            e.printStackTrace();
//            fail("throw SVNPluginException! " + e.getMessage());
//        }
//        
//    }

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
