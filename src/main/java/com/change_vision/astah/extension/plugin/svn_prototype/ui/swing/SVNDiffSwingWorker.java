package com.change_vision.astah.extension.plugin.svn_prototype.ui.swing;

import java.util.List;

import javax.swing.SwingWorker;

import com.change_vision.astah.extension.plugin.svn_prototype.task.SVNDiffTask;

public class SVNDiffSwingWorker extends SwingWorker<List<Integer>, Integer> {
    private SVNDiffTask task;
    

    public SVNDiffSwingWorker(String file1, String file2, boolean newFileDeleteFlg) {
        super();
        task = new SVNDiffTask(file1, file2, newFileDeleteFlg);
    }

    public SVNDiffSwingWorker(String file1, String file2) {
        super();
        task = new SVNDiffTask(file1, file2);
    }

    @Override
    protected List<Integer> doInBackground() throws Exception {
        List<Integer> list = task.doInBackground();
        setProgress(100);
        return list;
    }

    @Override
    protected void done() {
        super.done();
        task.done();
    }
    
}
