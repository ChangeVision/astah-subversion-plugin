package com.change_vision.astah.extension.plugin.svn_prototype.ui.swing;

import java.io.IOException;
import java.util.List;

import javax.swing.SwingWorker;

import org.tmatesoft.svn.core.SVNException;

import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNConflictException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;
import com.change_vision.astah.extension.plugin.svn_prototype.task.SVNMergeTask;
import com.change_vision.astah.extension.plugin.svn_prototype.util.ISVNKitUtils;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNUtils;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class SVNMergeSwingWorker extends SwingWorker<List<Integer>, Integer> {
    private SVNMergeTask task;

    public SVNMergeSwingWorker(String pjPath, ISVNKitUtils kitUtils, ProjectAccessor projectAccessor) {
        task = new SVNMergeTask(pjPath, kitUtils, projectAccessor);
    }

    @Override
    protected List<Integer> doInBackground() throws InvalidEditingException,  SVNException,
                                                    LicenseNotFoundException, ProjectNotFoundException,
                                                    NonCompatibleException,   IOException,
                                                    ClassNotFoundException,   ProjectLockedException,
                                                    SVNConflictException, SVNPluginException {
        List<Integer> list = task.doInBackground();
        setProgress(100);
        return list;
    }

    public void setLatestRevision(long revision) {
        task.setLatestRevision(revision);
    }

    public void setSVNInfo(SVNUtils utils) {
        task.setSVNInfo(utils);
    }

    public void setSelected(int selected) {
        task.setSelected(selected);
    }

    public int getSelected() {
        return task.getSelected();
    }

}
