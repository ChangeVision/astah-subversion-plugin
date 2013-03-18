package com.change_vision.astah.extension.plugin.svn_prototype;

import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.Test;

public class SVNProgressTaskTest {

    @Test
    public void testSVNProgressTask() {
        SVNProgressTask spt = new SVNProgressTask(null);
        assertThat(spt, is(notNullValue()));
    }

    // �v���O���X�o�[��\������e�X�g
    // ��Ńv���O���X�o�[�̃_�C�A���O����Ȃ��Ɛ�ɐi�܂Ȃ�
//    @Test
//    public void testDoInBackground() {
//        SVNProgressTask spt = new SVNProgressTask(null);
//        try {
//            List<Integer> list = spt.doInBackground();
//            assertThat(list, is(nullValue()));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Not yet implemented");
//        }
//    }

    @Test
    public void testFinishProgress() {
        SVNProgressTask spt = new SVNProgressTask(null);
        spt.finishProgress();
    }

}
