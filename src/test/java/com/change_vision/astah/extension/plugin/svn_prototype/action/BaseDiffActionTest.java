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

public class BaseDiffActionTest {
    private final String SVN_FILE_PATH = "C:\\Documents and Settings\\kasaba\\デスクトップ\\svn_repository\\sample.asta";

    @Test
    public void testGetProjectPath() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }
        BaseDiffAction bda = new BaseDiffAction();
        String pjPath = bda.getProjectPath();
        assertThat(pjPath, is(notNullValue()));
    }

    @Test
    public void testGetFileName() {
        BaseDiffAction bda = new BaseDiffAction();
        String fileName = bda.getFileName("C:\\tmp\\test.asta");
        assertThat(fileName, is("test.asta"));
    }

    @Test
    public void testGetFilePath() {
        BaseDiffAction bda = new BaseDiffAction();
        String filePath = bda.getFilePath("C:\\tmp\\test.asta");
        assertThat(filePath, is("C:\\tmp\\"));
    }

    @Test
    public void testInitializeSVN() {
        BaseDiffAction bda = new BaseDiffAction();
        SVNUtils su = bda.initializeSVN();
        assertThat(su, is(notNullValue()));
    }

    @Test
    public void testGetSVNWCClient1() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        BaseDiffAction bda = new BaseDiffAction();
        String fileName = bda.getFileName(SVN_FILE_PATH);
        SVNUtils su = bda.initializeSVN();

        SVNWCClient swc = bda.getSVNWCClient(su, fileName);
        assertThat(swc, is(notNullValue()));
    }

    @Test
    public void testGetSVNWCClient2() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        BaseDiffAction bda = new BaseDiffAction();
        SVNWCClient swc = bda.getSVNWCClient(null, null);
        assertThat(swc, is(nullValue()));
    }

    @Test
    public void testGetBaseFile1() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        BaseDiffAction bda = new BaseDiffAction();
        String fileName = bda.getFileName(SVN_FILE_PATH);
        SVNUtils su = bda.initializeSVN();
        SVNWCClient swc = bda.getSVNWCClient(su, fileName);

        String baseFile = bda.getBaseFile(bda.getFilePath(SVN_FILE_PATH), fileName, swc);
        assertThat(baseFile, is(notNullValue()));
    }

    @Test
    public void testGetBaseFile2() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        BaseDiffAction bda = new BaseDiffAction();

        String baseFile = bda.getBaseFile(null, null, null);
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
//        BaseDiffAction bda = new BaseDiffAction();
//        try {
//            String pjPath = bda.getProjectPath();
//
//            String fileName = bda.getFileName(pjPath);
//            String filePath = bda.getFilePath(pjPath);
//            SVNUtils utils = bda.initializeSVN();
//            SVNWCClient client = bda.getSVNWCClient(utils, fileName);
//            String workFile = bda.getBaseFile(filePath, fileName, client);
//            bda.displayDiff(pjPath, workFile);
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
