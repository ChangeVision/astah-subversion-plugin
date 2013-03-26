package com.change_vision.astah.extension.plugin.svn_prototype.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.mock;

import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotCommitException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class SVNLatestDiffExecutorTest {
    private final String SVN_FILE_PATH = "src/test/resources/sample.asta";

    private ProjectAccessor pjAccessor;
    private SVNUtils utils;
    private ISVNKitUtils kitUtils;

    @Before
    public void before() {
        pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
        }
        utils = new SVNUtils();
        kitUtils = mock(ISVNKitUtils.class);
    }

//    @Test
//    public void testGetSVNWCClient1() {
//        SVNLatestDiffExecutor lda = new SVNLatestDiffExecutor(pjAccessor, null);
//        try {
//            lda.initializeSVN();
//        } catch (SVNNotConfigurationException e) {
//            e.printStackTrace();
//            fail("throw SVNNotConfigurationException!");
//        } catch (SVNPluginException e) {
//            e.printStackTrace();
//            fail("throw SVNPluginException!");
//        }
//        SVNWCClient swc = lda.getSVNWCClient();
//        assertThat(swc, is(notNullValue()));
//    }
//
//    @Test
//    public void testGetSVNWCClient2() {
//        SVNLatestDiffExecutor lda = new SVNLatestDiffExecutor(pjAccessor, null);
//        SVNWCClient swc = lda.getSVNWCClient();
//        assertThat(swc, is(nullValue()));
//    }
//
    @Test
    @Ignore("実リポジトリを作らないといけないのでスキップ")
    public void testGetLatestFile1() {
        SVNLatestDiffExecutor lda = new SVNLatestDiffExecutor(pjAccessor, null);
        String fileName = SVNUtils.getFileName(SVN_FILE_PATH);
        try {
            lda.initializeSVN();
        } catch (SVNNotConfigurationException e) {
            e.printStackTrace();
            fail("throw SVNNotConfigurationException!");
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        }
        String baseFile;
        try {
            baseFile = kitUtils.getLatestFile(SVNUtils.getFilePath(SVN_FILE_PATH), fileName);
            assertThat(baseFile, is(notNullValue()));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        } catch (SVNNotCommitException e) {
            e.printStackTrace();
            fail("throw SVNNotCommitException!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("throw FileNotFoundException!");
        }
    }

    @Test
    public void testGetLatestFile2() {
        String baseFile;
        try {
            baseFile = kitUtils.getLatestFile(null, null);
            assertThat(baseFile, is(nullValue()));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        } catch (SVNNotCommitException e) {
            e.printStackTrace();
            fail("throw SVNNotCommitException!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("throw FileNotFoundException!");
        }
    }

    @Test
    @Ignore("GUIテスト")
    public void testDisplayDiff() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        if (utils.getViewManager() == null) {
            fail("viewManager = null!");
            return;
        } else if ((utils.getViewManager()).getMainFrame() == null) {
            fail("mainFrame = null!");
            return;
        }

        SVNLatestDiffExecutor lda = new SVNLatestDiffExecutor(pjAccessor, null);
        try {
            String pjPath = utils.getProjectPath(ProjectAccessorFactory.getProjectAccessor());

            String fileName = SVNUtils.getFileName(pjPath);
            String filePath = SVNUtils.getFilePath(pjPath);
            lda.initializeSVN();
            String workFile = kitUtils.getLatestFile(filePath, fileName);
            lda.displayDiff(pjPath, workFile);
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
