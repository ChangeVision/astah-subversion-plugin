package com.change_vision.astah.extension.plugin.svn_prototype.exception;

public class SVNPluginException extends Exception {

    private static final long serialVersionUID = -1437420139177104809L;

    public SVNPluginException() {
        super();
    }

    public SVNPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public SVNPluginException(String message) {
        super(message);
    }

    public SVNPluginException(Throwable cause) {
        super(cause);
    }
}
