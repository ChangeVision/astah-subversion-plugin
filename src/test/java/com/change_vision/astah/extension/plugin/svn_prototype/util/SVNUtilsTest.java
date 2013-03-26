package com.change_vision.astah.extension.plugin.svn_prototype.util;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.crypto.Cipher;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;

import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotConfigurationException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

public class SVNUtilsTest {
    private final String SVN_FILE_PATH = "src/test/resources/sample.asta";
//    private final String SVN_REPOSITORY_URL = "file:///C:/svn_repository";
//    private final String SVN_USER = "kasaba";
//    private final String SVN_PASSWORD = "";
    private ISVNKitUtils kitUtils;
    private ProjectAccessor pjAccessor;

    @Before
    public void before() {
        pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        this.kitUtils = mock(ISVNKitUtils.class);
    }

    @Test
    public void testSVNUtils() {
        SVNUtils utils = new SVNUtils();
        assertThat(utils, is(notNullValue()));
    }

    @Test
    @Ignore("実リポジトリを作らないといけないのでスキップ")
    public void testGetPreferencesInfo() {
        try {
            String pjPath = pjAccessor.getProjectPath();
            Mockito.when(kitUtils.getDefaultRepositoryURL(pjPath)).thenReturn(SVN_FILE_PATH);
            SVNUtils utils = new SVNUtils();
            utils.setSVNKitUtils(kitUtils);
            boolean result;
            result = utils.getPreferencesInfo("キャンセルメッセージ");
            assertThat(result, is(true));
        } catch (SVNNotConfigurationException e) {
            e.printStackTrace();
            fail("throw SVNNotConfigurationException!");
        } catch (SVNException e) {
            e.printStackTrace();
            fail("throw SVNException!");
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
            fail("throw ProjectNotFoundException!");
        }
    }

    @Test
    public void testChkNotSaveProject() {
        boolean result = SVNUtils.isSaveProject(SVN_FILE_PATH);
        assertThat(result, is(true));
    }

    @Test
    public void testChkNullString1() {
        boolean result = SVNUtils.isNullString("String");
        assertThat(result, is(false));
    }

    @Test
    public void testChkNullString2() {
        boolean result = SVNUtils.isNullString(null);
        assertThat(result, is(true));
    }

    @Test
    public void testReadFileByte() {
        byte[] b = null;
        try {
            SVNUtils utils = new SVNUtils();
            b = utils.readFileByte(SVN_FILE_PATH);
        } catch (IOException e) {
            fail("Not yet implemented");
        }
        assertThat(b, is(notNullValue()));
    }
//
//    @Test
//    public void testGetRepos() {
//        SVNRepository repos = null;
//        try {
//            SVNUtils utils = new SVNUtils();
//            repos = utils.getRepos(SVN_REPOSITORY_URL, SVN_USER, SVN_PASSWORD);
//        } catch (SVNException e) {
//            fail("Not yet implemented");
//        }
//        assertThat(repos, is(notNullValue()));
//    }
//
//    @Test
//    public void testGetViewManager() {
//        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
//        if (pjAccessor == null) {
//            fail("ProjectAccessor = null!");
//            return;
//        }
//
//        IViewManager vm = SVNUtils.getViewManager();
//        assertThat(vm, is(notNullValue()));
//    }

    @Test
    public void testChkLoginError1() {
        SVNException se = new SVNException(SVNErrorMessage.create(SVNErrorCode.RA_NOT_AUTHORIZED , "Authentication required for AAA"));
        MessageDialog messageDialog = mock(MessageDialog.class);
        System.out.println(se.getMessage());
        SVNUtils utils = new SVNUtils();
        SVNUtils.setMessageDialog(messageDialog);
        boolean result = utils.isLoginError(se);
        
        assertThat(result, is(true));
    }

    @Test
    public void testChkLoginError2() {
        SVNException se = new SVNException(SVNErrorMessage.create(SVNErrorCode.FS_NOT_ID , "String does not represent a node or node-rev-id"));
        System.out.println(se.getMessage());
        SVNUtils utils = new SVNUtils();
        boolean result = utils.isLoginError(se);
        
        assertThat(result, is(false));
    }

    @Test
    public void testEncript() {
        byte[] b = SVNUtils.encript("encript");
        assertThat(b, is(notNullValue()));
    }

    @Test
    public void testDecript() {
        byte[] b = SVNUtils.encript("encript");
        String result = SVNUtils.decript(b);
        assertThat(result, is("encript"));
    }

    @Test
    public void testCreateCipher() {
        Cipher cipher = SVNUtils.createCipher(Cipher.ENCRYPT_MODE);
        assertThat(cipher, is(notNullValue()));
    }

    @Test
    public void testGetDefaultRepositoryURL1() {
        String url = null;
        try {
            String pjPath = pjAccessor.getProjectPath();
            Mockito.when(kitUtils.getDefaultRepositoryURL(pjPath)).thenReturn(SVN_FILE_PATH);

            SVNUtils utils = new SVNUtils();
            utils.setSVNKitUtils(kitUtils);
            url = utils.getDefaultRepositoryURL();
        } catch (SVNException e) {
            fail("Not yet implemented");
        } catch (ClassNotFoundException e) {
            fail("Not yet implemented");
        } catch (ProjectNotFoundException e) {
            fail("Not yet implemented");
        }
        assertThat(url, is(notNullValue()));
    }

    @Test
    public void testEscapeSpaceForMac() {
        SVNUtils utils = new SVNUtils();
        String str = utils.escapeSpaceForMac("orig in");
        assertThat(str, is("orig in"));
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
