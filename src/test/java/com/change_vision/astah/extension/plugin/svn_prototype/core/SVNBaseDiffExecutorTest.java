package com.change_vision.astah.extension.plugin.svn_prototype.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

//import static org.hamcrest.core.Is.is;
//import static org.hamcrest.core.IsNull.nullValue;
//import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNBaseDiffExecutor;
import com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class SVNBaseDiffExecutorTest {
    private final String SVN_FILE_PATH = "src/test/resources/sample.asta";

    private ProjectAccessor pjAccessor;

    @Before
    public void before() {
        pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
        }
    }

//    @Test
//    @Ignore("実リポジトリを作らないといけないのでスキップ")
//    public void testGetBaseFile1() {
//        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor, null);
//        String fileName = SVNUtils.getFileName(SVN_FILE_PATH);
////        SVNUtils su = bda.initializeSVN();
//        try {
//            ISVNKitUtils kitUtils = new SVNKitUtils();
//            kitUtils.setSVNUtils(new SVNUtils());
//            SVNWCClient swc = kitUtils.getSVNWCClient();
//
//            String baseFile;
//            baseFile = bda.getBaseFile(SVNUtils.getFilePath(SVN_FILE_PATH), fileName, swc);
//            assertThat(baseFile, is(notNullValue()));
//        } catch (SVNPluginException e) {
//            e.printStackTrace();
//            fail("throw SVNPluginException!");
//        } catch (SVNException e) {
//            e.printStackTrace();
//            fail("throw SVNException!");
//        }
//    }
//
//    @Test
//    public void testGetBaseFile2() {
//        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor, null);
//
//        String baseFile;
//        try {
//            baseFile = bda.getBaseFile(null, null, null);
//            assertThat(baseFile, is(nullValue()));
//        } catch (SVNPluginException e) {
//            e.printStackTrace();
//            fail("throw SVNPluginException!");
//        }
//    }
//
    @Test
    @Ignore
    public void testDisplayDiff() {
        if ((new SVNUtils()).getViewManager() == null) {
            fail("viewManager = null!");
            return;
        } else if (((new SVNUtils()).getViewManager()).getMainFrame() == null) {
            fail("mainFrame = null!");
            return;
        }

        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor, null);
        try {
            String pjPath = (new SVNUtils()).getProjectPath(ProjectAccessorFactory.getProjectAccessor());

            String fileName = SVNUtils.getFileName(pjPath);
            String filePath = SVNUtils.getFilePath(pjPath);
            ISVNKitUtils kitUtils = new SVNKitUtils();
            kitUtils.setSVNUtils(new SVNUtils());
            String workFile = kitUtils.getBaseFile(filePath, fileName);
            bda.displayDiff(pjPath, workFile);
        } catch (IllegalArgumentException e) {
            fail("Not yet implemented");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
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
