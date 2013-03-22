package com.change_vision.astah.extension.plugin.svn_prototype.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNBaseDiffExecutor;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
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

    @Test
    public void testGetProjectPath() {
        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor);
        String pjPath;
        try {
            pjPath = bda.getProjectPath();
            assertThat(pjPath, is(notNullValue()));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        }
    }

    @Test
    public void testGetFileName() {
        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor);
        String fileName = bda.getFileName("C:\\tmp\\test.asta");
        assertThat(fileName, is("test.asta"));
    }

    @Test
    // TODO fileSeparator���g��
    public void testGetFilePath() {
        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor);
        String filePath = bda.getFilePath("C:\\tmp\\test.asta");
        assertThat(filePath, is("C:\\tmp\\"));
    }

    @Test
    // TODO ProjectAccessor�����b�N�ɂ���
    public void testInitializeSVN() {
        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor);
        SVNUtils su;
        try {
            su = bda.initializeSVN();
            assertThat(su, is(notNullValue()));
        } catch (SVNNotConfigurationException e) {
            e.printStackTrace();
            fail("throw SVNNotConfigurationException!");
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        }
        pjAccessor.close();
    }

    @Test
    @Ignore("実リポジトリを作らないといけないのでスキップ")
    public void testGetSVNWCClient1() {
        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor);
        String fileName = bda.getFileName(SVN_FILE_PATH);

        SVNWCClient swc;
        try {
            swc = bda.getSVNWCClient(fileName);
            assertThat(swc, is(notNullValue()));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        }
    }

    @Test
    public void testGetSVNWCClient2() {
        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor);
        SVNWCClient swc;
        try {
            swc = bda.getSVNWCClient(null);
            assertThat(swc, is(nullValue()));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        }
    }

    @Test
    @Ignore("実リポジトリを作らないといけないのでスキップ")
    public void testGetBaseFile1() {
        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor);
        String fileName = bda.getFileName(SVN_FILE_PATH);
//        SVNUtils su = bda.initializeSVN();
        try {
            SVNWCClient swc = bda.getSVNWCClient(fileName);

            String baseFile;
            baseFile = bda.getBaseFile(bda.getFilePath(SVN_FILE_PATH), fileName, swc);
            assertThat(baseFile, is(notNullValue()));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        }
    }

    @Test
    public void testGetBaseFile2() {
        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor);

        String baseFile;
        try {
            baseFile = bda.getBaseFile(null, null, null);
            assertThat(baseFile, is(nullValue()));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        }
    }

    @Test
    @Ignore
    public void testDisplayDiff() {
        if (SVNUtils.getViewManager() == null) {
            fail("viewManager = null!");
            return;
        } else if ((SVNUtils.getViewManager()).getMainFrame() == null) {
            fail("mainFrame = null!");
            return;
        }

        SVNBaseDiffExecutor bda = new SVNBaseDiffExecutor(pjAccessor);
        try {
            String pjPath = bda.getProjectPath();

            String fileName = bda.getFileName(pjPath);
            String filePath = bda.getFilePath(pjPath);
//            SVNUtils utils = bda.initializeSVN();
            SVNWCClient client = bda.getSVNWCClient( fileName);
            String workFile = bda.getBaseFile(filePath, fileName, client);
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
