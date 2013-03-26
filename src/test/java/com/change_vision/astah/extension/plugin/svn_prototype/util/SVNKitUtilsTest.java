package com.change_vision.astah.extension.plugin.svn_prototype.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import static org.mockito.Mockito.mock;

public class SVNKitUtilsTest {

    private ISVNKitUtils kitUtils;
    private SVNUtils svnUtils;
    @Before
    public void before() {
        kitUtils = new SVNKitUtils();
        svnUtils = mock(SVNUtils.class);
    }

    @Test
    public void testInitialize() {
        try {
            kitUtils.initialize();
        } catch (Exception e) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testGetSVNClientManager1() {
        try {
            Mockito.when(svnUtils.getUser()).thenReturn("aaa");
            Mockito.when(svnUtils.getPassword()).thenReturn("aaa");
            kitUtils.setSVNUtils(svnUtils);
            SVNClientManager scm = kitUtils.getSVNClientManager();
            assertThat(scm, is(notNullValue()));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    public void testGetSVNClientManager2() {
        try {
            ISVNAuthenticationManager auth = mock(ISVNAuthenticationManager.class);
            Mockito.when(svnUtils.getAuthManager()).thenReturn(auth);
            kitUtils.setSVNUtils(svnUtils);
            SVNClientManager scm = kitUtils.getSVNClientManager();
            assertThat(scm, is(notNullValue()));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    public void testGetSVNWCClient1() {
        try {
            Mockito.when(svnUtils.getUser()).thenReturn("aaa");
            Mockito.when(svnUtils.getPassword()).thenReturn("aaa");
            kitUtils.setSVNUtils(svnUtils);
            SVNWCClient client = kitUtils.getSVNWCClient();
            assertThat(client, is(notNullValue()));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    public void testGetSVNWCClient2() {
        try {
            ISVNAuthenticationManager auth = mock(ISVNAuthenticationManager.class);
            Mockito.when(svnUtils.getAuthManager()).thenReturn(auth);
            kitUtils.setSVNUtils(svnUtils);
            SVNWCClient client = kitUtils.getSVNWCClient();
            assertThat(client, is(notNullValue()));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    @Ignore("実リポジトリを作らないといけないのでスキップ")
    public void testGetLatestRevision() {
        String fileName = "aaa.asta";
        try {
            kitUtils.setSVNUtils(svnUtils);
            long revision = kitUtils.getLatestRevision(fileName);
            assertThat(revision, is((long)-1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    @Ignore("実リポジトリを作らないといけないのでスキップ")
    public void testGetBaseRevision_String() {
        String fileName = "aaa.asta";
        try {
            kitUtils.setSVNUtils(svnUtils);
            long revision = kitUtils.getBaseRevision(fileName);
            assertThat(revision, is((long)-1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }

    @Test
    @Ignore("実リポジトリを作らないといけないのでスキップ")
    public void testGetBaseRevision_File() {
        String fileName = "aaa.asta";
        try {
            kitUtils.setSVNUtils(svnUtils);
            long revision = kitUtils.getBaseRevision(new File(fileName));
            assertThat(revision, is((long)-1));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Not yet implemented");
        }
    }
}
