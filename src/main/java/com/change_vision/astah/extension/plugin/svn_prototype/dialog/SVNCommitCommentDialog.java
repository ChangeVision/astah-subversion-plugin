package com.change_vision.astah.extension.plugin.svn_prototype.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.core.SVNComment;

public class SVNCommitCommentDialog extends KeyDialog implements SVNComment {

    private static final long serialVersionUID = -5780332918780882297L;

    private boolean runCommitFlg = false;
    private JTextArea textArea;
    private JScrollPane scrollpane = null;

    public SVNCommitCommentDialog(Frame frame) {
        super(frame, true);

        // ウィンドウのリサイズを禁止
        setResizable(false);

        setTitle(Messages.getMessage("commit_comment_dialog.title"));
        setSize(new Dimension(450, 300));
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel headerPanel  = new JPanel(new BorderLayout());
        JPanel contentPanel = new JPanel(new BorderLayout());
        JPanel footerPanel  = new JPanel(new BorderLayout());

        JLabel lblHeader = new JLabel(Messages.getMessage("commit_comment_dialog.comment_title"));
        headerPanel.add(lblHeader, BorderLayout.WEST);

        textArea = new JTextArea(18, 80);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent e){
                if (textArea.getLineCount() > textArea.getRows()){
                    textArea.setRows(textArea.getLineCount() + 2);
                    textArea.invalidate();
                    if (scrollpane != null){
                        scrollpane.updateUI();
                        (scrollpane.getVerticalScrollBar()).setValue(100);
                    }
                }
            }
        });

        scrollpane = new JScrollPane(textArea);
        scrollpane.setWheelScrollingEnabled(true);
        contentPanel.add(scrollpane, BorderLayout.CENTER);

        // OKボタンの設定
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton(Messages.getMessage("ok"));
        okButton.setToolTipText(Messages.getMessage("ok"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommitFlg = true;

                // ウィンドウを閉じる処理
                dispose();
                return;
            }
        });
        buttonPanel.add(okButton);

        // キャンセルボタンの設定
        JButton cancelButton = new JButton(Messages.getMessage("cancel"));
        cancelButton.setToolTipText(Messages.getMessage("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommitFlg = false;

                // ウィンドウを閉じる処理
                dispose();
                return;
            }
        });
        buttonPanel.add(cancelButton);
        footerPanel.add(buttonPanel, BorderLayout.CENTER);

        // Frameに各パーツをセット
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        pack();

        if(frame != null) {
            setLocationRelativeTo(frame.getParent());
        }
        setVisible(true);
    }

    @Override
    public boolean isCommit(){
        return runCommitFlg;
    }

    @Override
    public String getCommitComment(){
        return textArea.getText();
    }
}
