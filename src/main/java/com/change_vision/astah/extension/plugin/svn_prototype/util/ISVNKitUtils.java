package com.change_vision.astah.extension.plugin.svn_prototype.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNConflictException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNNotCommitException;
import com.change_vision.astah.extension.plugin.svn_prototype.exception.SVNPluginException;

public interface ISVNKitUtils {

    public void setProjectPath(String pjPath);

    public abstract void setSVNUtils(SVNUtils utils);

    public void initialize();

    public abstract SVNClientManager getSVNClientManager();

    public abstract SVNWCClient getSVNWCClient() throws SVNException, SVNPluginException;

    public SVNUpdateClient getSVNUpdateClient() throws SVNException, SVNPluginException;

    public abstract long getBaseRevision(String destination) throws SVNException;

    public abstract long getBaseRevision(File destination) throws SVNException;

    public abstract long getLatestRevision(String fileName) throws SVNPluginException;

    public abstract void doCommit(String fileName, String comment, long baseRevision) throws FileNotFoundException, SVNException, IOException, SVNPluginException;

    public void doRevert(String fileName) throws SVNException, SVNPluginException;

    public boolean doUpdate(String path) throws SVNException, SVNConflictException, SVNPluginException;

    public boolean doUpdate(Long revision, String path) throws SVNException, SVNConflictException, SVNPluginException;

    public String getBaseFile(String filePath, String fileName) throws SVNPluginException, FileNotFoundException;

    public String getLatestFile(String filePath, String fileName) throws SVNPluginException, SVNNotCommitException, FileNotFoundException;

    public String getDefaultRepositoryURL(String pjPath) throws SVNException;
}