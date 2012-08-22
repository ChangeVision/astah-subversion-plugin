package com.change_vision.astah.extension.plugin.svn_prototype.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.change_vision.astah.extension.plugin.svn_prototype.Messages;

public class SvnDiffInputStreamThread extends Thread {
    private BufferedReader br;

    private List<String> list = new ArrayList<String>();

    /** コンストラクター */
    public SvnDiffInputStreamThread(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is));
    }

    /** コンストラクター */
    public SvnDiffInputStreamThread(InputStream is, String charset) {
        try {
            br = new BufferedReader(new InputStreamReader(is, charset));
        } catch (UnsupportedEncodingException e) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
        }
    }

    @Override
    public void run() {
        try {
            for (;;) {
                String line = br.readLine();
                if (line == null)     break;
                list.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, Messages.getMessage("err_message.common_svn_error"));
            }
        }
    }

    /** 文字列取得 */
    public List<String> getStringList() {
        return list;
    }
}
