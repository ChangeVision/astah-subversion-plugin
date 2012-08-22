package com.change_vision.astah.extension.plugin.svn_prototype.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;
import com.change_vision.astah.extension.plugin.svn_prototype.util.SVNPreferences;

@SuppressWarnings("serial")
public class SVNSelectMergeDialog extends KeyDialog {

    public static final int CURRENT_PROJECT = 1;     // 編集中のプロジェクトをベースにマージ
    public static final int REPOSITORY_PROJECT = 2;  // リポジトリ内にある最新版のプロジェクトをベースにマージ
    public static final int NO_MERGE = 3;            // マージ処理をやらない(更新前の状態に戻す)
    public static final int GET_LATEST_PROJECT = 4;  // 編集中の内容を破棄し、最新版のプロジェクトを取得
    public static final int GET_LATEST_REVISION = 5; // 編集中の内容をそのままにし、リビジョンだけ最新にする

    private boolean selectFlg = false;

    JRadioButton radio1;
    JRadioButton radio2;
    JRadioButton radio3;
    JRadioButton radio4;
    JRadioButton radio5;

    public SVNSelectMergeDialog(JFrame frame) {
        // ダイアログをモーダルで開く設定
        super(frame, true);

        // Preferences のインスタンスを取得
        SVNPreferences.getInstace(this.getClass());
        Preferences preferences = SVNPreferences.getInstance();

        setTitle(Messages.getMessage("merge_dialog.title"));
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPanel  = new JPanel(new BorderLayout());
        JPanel contentPanel1 = new JPanel(new BorderLayout());
        JPanel contentPanel2 = new JPanel(new BorderLayout());

        String strMergeKind = preferences.get(SVNPreferences.KEY_MERGE_KIND, null);
        int mergeKind = 0;
        if (strMergeKind != null) {
            mergeKind = Integer.valueOf(strMergeKind);
        }

        // 前回選択したものをONにする(初回は全てOFF)
        boolean currentFlg        = false;
        boolean repositoryFlg     = false;
        boolean latestProjectFlg  = false;
        boolean latestRevisionFlg = false;
        if (mergeKind == CURRENT_PROJECT) {
            currentFlg = true;
        } else if (mergeKind == REPOSITORY_PROJECT) {
            repositoryFlg = true;
        } else if (mergeKind == GET_LATEST_PROJECT) {
            latestProjectFlg = true;
        } else if (mergeKind == GET_LATEST_REVISION) {
            latestRevisionFlg = true;
        }

        ButtonGroup bGroup = new ButtonGroup();
        radio1 = new JRadioButton(Messages.getMessage("merge_dialog.merge_current_label"), currentFlg);
        radio2 = new JRadioButton(Messages.getMessage("merge_dialog.merge_repository_label"), repositoryFlg);
        radio3 = new JRadioButton(Messages.getMessage("merge_dialog.get_new_project"), latestProjectFlg);
        radio4 = new JRadioButton(Messages.getMessage("merge_dialog.update_revision"), latestRevisionFlg);

        // パネルにラジオボタンを追加
        contentPanel1.add(radio1, BorderLayout.NORTH);
        contentPanel1.add(radio2, BorderLayout.SOUTH);
        contentPanel2.add(radio3, BorderLayout.NORTH);
        contentPanel2.add(radio4, BorderLayout.SOUTH);
        contentPanel.add(contentPanel1, BorderLayout.NORTH);
        contentPanel.add(contentPanel2, BorderLayout.SOUTH);

        // ラジオボタンをグループ化
        bGroup.add(radio1);
        bGroup.add(radio2);
        bGroup.add(radio3);
        bGroup.add(radio4);

        JPanel buttonPanel = new JPanel();
        // 保存ボタンの設定
        JButton okButton = new JButton(Messages.getMessage("ok"));
        okButton.setToolTipText(Messages.getMessage("ok"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Preferences のインスタンスを取得
                Preferences preferences = SVNPreferences.getInstance();

                // 選択項目の保存処理
                try {
                    int Selected = 0;
                    if (radio1.isSelected()) {
                        Selected = CURRENT_PROJECT;
                    } else if (radio2.isSelected()) {
                        Selected = REPOSITORY_PROJECT;
                    } else if (radio3.isSelected()){
                        Selected = GET_LATEST_PROJECT;
                    } else if (radio4.isSelected()){
                        Selected = GET_LATEST_REVISION;
                    } else {
                        JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.merge_no_choice"));
                        return;
                    }
                    preferences.put(SVNPreferences.KEY_MERGE_KIND, String.valueOf(Selected));
                    preferences.flush();
                    // ウィンドウを閉じる処理
                    dispose();
                    selectFlg = true;
                    return;
                } catch (BackingStoreException bse) {
                    JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_fails_to_save"));
                }
            }
        });
        buttonPanel.add(okButton);

        // キャンセルボタンの設定
        JButton cancelButton = new JButton(Messages.getMessage("cancel"));
        cancelButton.setToolTipText(Messages.getMessage("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Preferences のインスタンスを取得
                Preferences preferences = SVNPreferences.getInstance();

                // 選択項目の保存処理
                try {
                    int Selected = NO_MERGE;
                    preferences.put(SVNPreferences.KEY_MERGE_KIND, String.valueOf(Selected));
                    preferences.flush();
                    // ウィンドウを閉じる処理
                    dispose();
                    return;
                } catch (BackingStoreException bse) {
                    JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.config_fails_to_save"));
                }
            }
        });
        buttonPanel.add(cancelButton);

        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();

        if(frame != null) {
            setLocationRelativeTo(frame.getParent());
        }
    }

    public boolean getSelectFlg(){
        return selectFlg;
    }
}
