package com.change_vision.astah.extension.plugin.svn_prototype.exception;

public class SVNCommitCancelException extends Exception {

    private static final long serialVersionUID = -21347529744331920L;

    public SVNCommitCancelException() {
        super();
    }

    public SVNCommitCancelException(String message, Throwable cause) {
        super(message, cause);
    }

    public SVNCommitCancelException(String message) {
        super(message);
    }

    public SVNCommitCancelException(Throwable cause) {
        super(cause);
    }
}
