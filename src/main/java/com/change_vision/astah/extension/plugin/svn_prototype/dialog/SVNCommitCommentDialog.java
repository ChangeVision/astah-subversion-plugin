package com.change_vision.astah.extension.plugin.svn_prototype.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.event.ComponentEvent;
//import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;

@SuppressWarnings("serial")
public class SVNCommitCommentDialog extends KeyDialog {

    private boolean runCommitFlg = false;
//    private boolean resizeFlg = false;
    private JTextArea textArea;
    private JScrollPane scrollpane = null;
//    private Dimension beforeWindowSize = null;

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
//        lblHeader.setPreferredSize(new Dimension(400, 15));
        headerPanel.add(lblHeader, BorderLayout.WEST);

//        textArea = new JTextArea(9, 55);
//        textArea.setPreferredSize(new Dimension(390, 100));
//        textArea = new JTextArea();
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
//        scrollpane.setPreferredSize(new Dimension(390, 180));
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

//        // ウィンドウリサイズ時の動作を設定
//        addComponentListener(new ComponentListener(){
//			@Override
//			public void componentHidden(ComponentEvent arg0) {
//				// 何もしない
//			}
//
//			@Override
//			public void componentMoved(ComponentEvent arg0) {
//				// 何もしない
//			}
//
//			@Override
//			public void componentResized(ComponentEvent arg0) {
//				System.out.println("call componentResized!");
//				// ウィンドウリサイズ時の動作を設定
//
//				boolean taFlg = false;
//				// リサイズ後のウィンドウの大きさを取得
//				double afterWidth  = (double)(arg0.getComponent().getWidth());
//				double afterHeight = (double)(arg0.getComponent().getHeight());
//
//				if (resizeFlg) {
//					resizeFlg = false;
//				} else {
//					System.out.println("before width:" + beforeWindowSize.getWidth());
//					System.out.println("before height:" + beforeWindowSize.getHeight());
//					System.out.println("after width:" + afterWidth);
//					System.out.println("after height:" + afterHeight);
//					if ((afterHeight / beforeWindowSize.getHeight()) > 1.01 || (afterHeight / beforeWindowSize.getHeight()) < 0.99){
//						textArea.setRows(Math.round(Math.round(textArea.getRows() * (afterHeight / beforeWindowSize.getHeight()))));
//						taFlg = true;
//					}
//					if ((afterWidth / beforeWindowSize.getWidth()) > 1.01 || (afterWidth / beforeWindowSize.getWidth()) < 0.99){
//						textArea.setColumns(Math.round(Math.round(textArea.getColumns() * (afterWidth / beforeWindowSize.getWidth()))));
//						taFlg = true;
//					}
//					if (taFlg) {
//						pack();
//						taFlg = false;
//					}
//					beforeWindowSize.setSize(afterWidth, afterHeight);
//					resizeFlg = true;
//				}
//			}
//
//			@Override
//			public void componentShown(ComponentEvent arg0) {
//				// 何もしない
//			}
//        });

        // Frameに各パーツをセット
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        pack();

//        beforeWindowSize = getSize();
        if(frame != null) {
            setLocationRelativeTo(frame.getParent());
        }
    }

    public boolean getRunCommitFlg(){
        return runCommitFlg;
    }

    public String getCommitComment(){
        return textArea.getText();
    }
}
