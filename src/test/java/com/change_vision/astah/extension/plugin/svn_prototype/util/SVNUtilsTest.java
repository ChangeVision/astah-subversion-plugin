package com.change_vision.astah.extension.plugin.svn_prototype.util;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.crypto.Cipher;

import org.junit.Test;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.view.IViewManager;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

public class SVNUtilsTest {
    private final String SVN_FILE_PATH = "C:\\Documents and Settings\\kasaba\\デスクトップ\\svn_repository\\sample.asta";
    private final String SVN_REPOSITORY_URL = "file:///C:/svn_repository";
    private final String SVN_USER = "kasaba";
    private final String SVN_PASSWORD = "";

    @Test
    public void testSVNUtils() {
        SVNUtils utils = new SVNUtils();
        assertThat(utils, is(notNullValue()));
    }

    @Test
    public void testGetPreferencesInfo() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        SVNUtils utils = new SVNUtils();
        boolean result = utils.getPreferencesInfo("キャンセルメッセージ");
        assertThat(result, is(true));
    }

    @Test
    public void testChkNotSaveProject() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        boolean result = SVNUtils.chkNotSaveProject(SVN_FILE_PATH);
        assertThat(result, is(false));
    }

    @Test
    public void testChkNullString1() {
        boolean result = SVNUtils.chkNullString("String");
        assertThat(result, is(false));
    }

    @Test
    public void testChkNullString2() {
        boolean result = SVNUtils.chkNullString(null);
        assertThat(result, is(true));
    }

    @Test
    public void testReadFileByte() {
        byte[] b = null;
        try {
            b = SVNUtils.readFileByte(SVN_FILE_PATH);
        } catch (IOException e) {
            fail("Not yet implemented");
        }
        assertThat(b, is(notNullValue()));
    }

    @Test
    public void testGetRepos() {
        SVNRepository repos = null;
        try {
            repos = SVNUtils.getRepos(SVN_REPOSITORY_URL, SVN_USER, SVN_PASSWORD);
        } catch (SVNException e) {
            fail("Not yet implemented");
        }
        assertThat(repos, is(notNullValue()));
    }

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
        System.out.println(se.getMessage());
        boolean result = SVNUtils.chkLoginError(se);
        
        assertThat(result, is(true));
    }

    @Test
    public void testChkLoginError2() {
        SVNException se = new SVNException(SVNErrorMessage.create(SVNErrorCode.FS_NOT_ID , "String does not represent a node or node-rev-id"));
        System.out.println(se.getMessage());
        boolean result = SVNUtils.chkLoginError(se);
        
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
    public void testGetAuthManager() {
        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
        if (pjAccessor == null) {
            fail("ProjectAccessor = null!");
            return;
        }

        SVNUtils utils = new SVNUtils();
        utils.getPreferencesInfo("キャンセルメッセージ");
        ISVNAuthenticationManager manager = utils.getAuthManager();
        assertThat(manager, is(notNullValue()));
    }

    @Test
    public void testGetDefaultRepositoryURL1() {
        String url = null;
        try {
            url = SVNUtils.getDefaultRepositoryURL();
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
    public void testGetDefaultRepositoryURL2() {
        String url = null;
        try {
            url = SVNUtils.getDefaultRepositoryURL(SVN_FILE_PATH);
        } catch (SVNException e) {
            fail("Not yet implemented");
        }
        assertThat(url, is(notNullValue()));
    }

//    @Test
//    public void testChkEditingProject() {
//        ProjectAccessor pjAccessor = openProject(SVN_FILE_PATH);
//        if (pjAccessor == null) {
//            fail("ProjectAccessor = null!");
//            return;
//        }
//
//        boolean result = SVNUtils.chkEditingProject();
//        assertThat(result, is(false));
//    }

    @Test
    public void testEscapeSpaceForMac() {
        String str = SVNUtils.escapeSpaceForMac("orig in");
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

}
