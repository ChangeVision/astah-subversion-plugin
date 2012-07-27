package com.change_vision.astah.extension.plugin.svn_prototype.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;

@SuppressWarnings("serial")
public class SVNProgressDialog extends KeyDialog {

	public boolean interruptFlg = false;

	private JProgressBar progress = null;;
    private JLabel       lblMessage   = null;

    public SVNProgressDialog(JFrame frame) {
        // ダイアログをモーダルで開く設定
        super(frame, true);

        String title = Messages.getMessage("progress_default_title");
        String message = Messages.getMessage("progress_default_message");
        makeProgressDialog(title, message);
    }

    public SVNProgressDialog(JFrame frame, String title, String Message) {
        // ダイアログをモーダルで開く設定
        super(frame, true);
        setLocationRelativeTo(frame);
        makeProgressDialog(title, Message);
    }

    private void makeProgressDialog(String title, String message){
        setTitle(title);
//        setSize(new Dimension(200, 150));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) {
				// 何もしない
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// 何もしない
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				interruptFlg = true;
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// 何もしない
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// 何もしない
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// 何もしない
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// 何もしない
			}
        });
        JPanel progressPanel = new JPanel();

        progress = new JProgressBar(0, 100);
        progress.setPreferredSize(new Dimension(300, 20));
        progress.setIndeterminate(true);
        progressPanel.add(progress);

        JPanel messagePanel = new JPanel();

        lblMessage = new JLabel(message);
        lblMessage.setPreferredSize(new Dimension(300, 20));
        messagePanel.add(lblMessage);
//        Container contentPane = getContentPane();
        JPanel ProgressPanel = new JPanel(new BorderLayout());
        ProgressPanel.add(progressPanel, BorderLayout.NORTH);
        ProgressPanel.add(messagePanel, BorderLayout.SOUTH);
        add(ProgressPanel);
        pack();
    }

    public void setProgressValue(int n) {
        if (progress != null){
            progress.setValue(n);
        }
    }

    public void setMessage(String message){
        if (lblMessage != null){
            lblMessage.setText(message);
        }
    }
}
