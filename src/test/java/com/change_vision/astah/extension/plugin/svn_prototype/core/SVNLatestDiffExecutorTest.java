package com.change_vision.astah.extension.plugin.svn_prototype.core;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

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
import com.change_vision.jude.api.inf.view.IViewManager;

public class SVNLatestDiffExecutorTest {
    private final String SVN_FILE_PATH = "src/test/resources/sample.asta";
    private final String SVN_FILE_PATH1 = "src/test/resources/sample.asta";
    private final String SVN_FILE_PATH2 = "src/test/resources/sample3.asta";

    private ProjectAccessor pjAccessor;
    private ISVNKitUtils kitUtils;

    @Before
    public void before() {
        pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
        }
        kitUtils = mock(ISVNKitUtils.class);
    }

    @Test
    public void testInitializeSVN() {
            SVNLatestDiffExecutor diffExec = new SVNLatestDiffExecutor(pjAccessor, kitUtils);
            try {
                diffExec.initializeSVN();
            } catch (SVNNotConfigurationException e) {
                e.printStackTrace();
                fail("throw SVNNotConfigurationException! " + e.getMessage());
            } catch (SVNPluginException e) {
                e.printStackTrace();
                fail("throw SVNPluginException! " + e.getMessage());
            }
    }

    @Test
    @Ignore("GUIのテスト")
    public void testDisplayDiff() {
        ISVNKitUtils kitUtils = mock(ISVNKitUtils.class);
        SVNUtils utils = mock(SVNUtils.class);
        IViewManager view = mock(IViewManager.class);
        Mockito.when(utils.getViewManager()).thenReturn(view);

        SVNLatestDiffExecutor diffExec = new SVNLatestDiffExecutor(pjAccessor, kitUtils);
        diffExec.setUtils(utils);

        try {
            diffExec.displayDiff(SVN_FILE_PATH1, SVN_FILE_PATH2);
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException! " + e.getMessage());
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
