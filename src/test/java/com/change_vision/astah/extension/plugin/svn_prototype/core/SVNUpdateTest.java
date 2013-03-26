package com.change_vision.astah.extension.plugin.svn_prototype.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.mock;

import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class SVNUpdateTest {
    private final String SVN_FILE_PATH = "src/test/resources/sample.asta";
    private ProjectAccessor pjAccessor;
    private ISVNKitUtils kitUtils;

    @Before
    public void before() {
        pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }
        kitUtils = mock(ISVNKitUtils.class);
    }

    @Test
    @Ignore("GUIテスト")
    public void testSvnUpdateMerge() {
        SVNUpdate ua = new SVNUpdate();
        String pjPath;
        try {
            pjPath = (new SVNUtils()).getProjectPath(ProjectAccessorFactory.getProjectAccessor());
            kitUtils = new SVNKitUtils();
            kitUtils.initialize();

            boolean result = ua.svnUpdateMerge(pjPath);
            assertThat(result, is(true));
        } catch (SVNPluginException e) {
            e.printStackTrace();
            fail("throw SVNPluginException!");
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
            fail("throw ProjectNotFoundException!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail("throw ClassNotFoundException!");
        } catch (LicenseNotFoundException e) {
            e.printStackTrace();
            fail("throw LicenseNotFoundException!");
        } catch (NonCompatibleException e) {
            e.printStackTrace();
            fail("throw NonCompatibleException!");
        } catch (ProjectLockedException e) {
            e.printStackTrace();
            fail("throw ProjectLockedException!");
        }
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

    @After
    public void after() {
        pjAccessor.close();
    }
}
