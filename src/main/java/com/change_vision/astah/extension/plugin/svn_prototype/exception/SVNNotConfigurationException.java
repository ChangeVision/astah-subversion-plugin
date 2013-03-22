package com.change_vision.astah.extension.plugin.svn_prototype.exception;

public class SVNNotConfigurationException extends Exception {

    private static final long serialVersionUID = -1621752739992988356L;

    public SVNNotConfigurationException() {
        super();
    }

    public SVNNotConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SVNNotConfigurationException(String message) {
        super(message);
    }

    public SVNNotConfigurationException(Throwable cause) {
        super(cause);
    }
}
