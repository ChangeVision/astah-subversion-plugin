package com.change_vision.astah.extension.plugin.svn_prototype;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import com.change_vision.astah.extension.plugin.svn_prototype.dialog.SVNProgressDialog;

public class SVNProgressTask extends SwingWorker<List<Integer>, Integer> {

    private JFrame frame = null;
    private SVNProgressDialog dialog = null;

    public SVNProgressTask(JFrame f){
        frame = f;
    }

    @Override
    protected List<Integer> doInBackground() throws Exception {
        dialog = new SVNProgressDialog(frame);
        dialog.setVisible(true);
        return null;
    }

    public void finishProgress(){
        if (dialog != null){
            dialog.dispose();
        }
        return;
    }
}
