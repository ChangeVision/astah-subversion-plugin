package com.change_vision.astah.extension.plugin.svn_prototype.action;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IWindow;

public class UpdateActionTest {
    private final String SVN_FILE_PATH = "C:\\Documents and Settings\\kasaba\\デスクトップ\\svn_repository\\sample.asta";

//    @Test
//    public void testSvnUpdateMerge() {
//        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
//        if (pjAccessor == null) {
//            fail("ProjectAccessor = null!");
//            return;
//        }
//
//        UpdateAction ua = new UpdateAction();
//        String pjPath = ua.getProjectPath();
//        SVNUtils utils = ua.initializeSVN();
//        SVNClientManager scm = ua.getSVNClientManager(utils);
//
//        boolean result = UpdateAction.svnUpdateMerge(mock(IWindow.class), utils, pjPath, scm);
//        assertThat(result, is(true));
//    }

    @Test
    public void testGetProjectPath() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }
        UpdateAction ua = new UpdateAction();
        String pjPath = ua.getProjectPath();
        assertThat(pjPath, is(notNullValue()));
    }

    @Test
    public void testInitializeSVN() {
        UpdateAction ua = new UpdateAction();
        SVNUtils su = ua.initializeSVN();
        assertThat(su, is(notNullValue()));
    }

    @Test
    public void testGetSVNClientManager() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        UpdateAction ua = new UpdateAction();
        SVNUtils su = ua.initializeSVN();
        SVNClientManager scm = ua.getSVNClientManager(su);
        assertThat(scm, is(notNullValue()));
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
