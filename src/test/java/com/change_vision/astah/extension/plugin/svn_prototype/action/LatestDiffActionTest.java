package com.change_vision.astah.extension.plugin.svn_prototype.action;

import static org.junit.Assert.*;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.Test;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class LatestDiffActionTest {
    private final String SVN_FILE_PATH = "C:\\Documents and Settings\\kasaba\\デスクトップ\\svn_repository\\sample.asta";

    @Test
    public void testGetProjectPath() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }
        LatestDiffAction lda = new LatestDiffAction();
        String pjPath = lda.getProjectPath();
        assertThat(pjPath, is(notNullValue()));
    }

    @Test
    public void testGetFileName() {
        LatestDiffAction lda = new LatestDiffAction();
        String fileName = lda.getFileName("C:\\tmp\\test.asta");
        assertThat(fileName, is("test.asta"));
    }

    @Test
    public void testGetFilePath() {
        LatestDiffAction lda = new LatestDiffAction();
        String filePath = lda.getFilePath("C:\\tmp\\test.asta");
        assertThat(filePath, is("C:\\tmp\\"));
    }

    @Test
    public void testInitializeSVN() {
        LatestDiffAction lda = new LatestDiffAction();
        SVNUtils su = lda.initializeSVN();
        assertThat(su, is(notNullValue()));
    }

    @Test
    public void testGetSVNWCClient1() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        LatestDiffAction lda = new LatestDiffAction();
        SVNUtils su = lda.initializeSVN();
        SVNWCClient swc = lda.getSVNWCClient(su);
        assertThat(swc, is(notNullValue()));
    }

    @Test
    public void testGetSVNWCClient2() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        LatestDiffAction lda = new LatestDiffAction();
        SVNWCClient swc = lda.getSVNWCClient(null);
        assertThat(swc, is(nullValue()));
    }

    @Test
    public void testGetLatestFile1() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        LatestDiffAction lda = new LatestDiffAction();
        String fileName = lda.getFileName(SVN_FILE_PATH);
        SVNUtils su = lda.initializeSVN();
        SVNWCClient swc = lda.getSVNWCClient(su);

        String baseFile = lda.getLatestFile(lda.getFilePath(SVN_FILE_PATH), fileName, su, swc);
        assertThat(baseFile, is(notNullValue()));
    }

    @Test
    public void testGetLatestFile2() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        LatestDiffAction lda = new LatestDiffAction();

        String baseFile = lda.getLatestFile(null, null, null, null);
        assertThat(baseFile, is(nullValue()));
    }

//    @Test
//    public void testDisplayDiff() {
//        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
//        if (pjAccessor == null) {
//            fail("ProjectAccessor = null!");
//            return;
//        }
//
//        if (SVNUtils.getViewManager() == null) {
//            fail("viewManager = null!");
//            return;
//        } else if ((SVNUtils.getViewManager()).getMainFrame() == null) {
//            fail("mainFrame = null!");
//            return;
//        }
//
//        LatestDiffAction lda = new LatestDiffAction();
//        try {
//            String pjPath = lda.getProjectPath();
//
//            String fileName = lda.getFileName(pjPath);
//            String filePath = lda.getFilePath(pjPath);
//            SVNUtils utils = lda.initializeSVN();
//            SVNWCClient client = lda.getSVNWCClient(utils);
//            String workFile = lda.getLatestFile(filePath, fileName, utils, client);
//            lda.displayDiff(pjPath, workFile);
//        } catch (IllegalArgumentException e) {
//            fail("Not yet implemented");
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Not yet implemented");
//        }
//    }

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
