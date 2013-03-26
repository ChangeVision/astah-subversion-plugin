package com.change_vision.astah.extension.plugin.svn_prototype.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;

import static org.mockito.Mockito.mock;

import com.change_vision.astah.extension.plugin.svn_prototype.dialog.MessageDialog;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.view.IViewManager;

public class SVNUpdateTest {
    private final String SVN_FILE_PATH = "src/test/resources/sample.asta";
    private ISVNKitUtils kitUtils;
    private SVNUtils utils;
    private MessageDialog messageDialog;

    @Before
    public void before() {
        kitUtils = mock(ISVNKitUtils.class);
        utils = mock(SVNUtils.class);
        messageDialog = mock(MessageDialog.class);
        SVNUpdate.setMessageDialog(messageDialog);
    }

    @Test
    public void testSvnUpdateMerge() {
        SVNUpdate ua = new SVNUpdate();

        SVNDirEntry entry = mock(SVNDirEntry.class);
        IViewManager view = mock(IViewManager.class);
        try {
            Mockito.when(utils.getSVNDirEntry(SVN_FILE_PATH)).thenReturn(entry);
            Mockito.when(utils.getViewManager()).thenReturn(view);
            ua.setKitUtils(kitUtils);
            ua.setUtils(utils);

            boolean result = ua.svnUpdateMerge(SVN_FILE_PATH);
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
        } catch (SVNException e) {
            e.printStackTrace();
            fail("throw SVNException!");
        }
    }
}
