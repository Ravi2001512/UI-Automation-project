package com.ucsc.tutionplatform.database;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.ucsc.tutionplatform.config.ConfigReader;

import java.util.Properties;

public final class SshTunnelManager {

    private static final String SSH_TUNNEL_ENABLED = "ssh.tunnel.enabled";
    private static final String SSH_HOST = "ssh.host";
    private static final String SSH_PORT = "ssh.port";
    private static final String SSH_USERNAME = "ssh.username";
    private static final String SSH_PASSWORD = "ssh.password";
    private static final String SSH_LOCAL_PORT = "ssh.local.port";
    private static final String SSH_REMOTE_HOST = "ssh.remote.host";
    private static final String SSH_REMOTE_PORT = "ssh.remote.port";
    private static final int DEFAULT_SSH_PORT = 22;
    private static final int DEFAULT_DB_PORT = 5432;

    private static Session session;

    private SshTunnelManager() {
    }

    public static synchronized void startIfEnabled() {
        if (!ConfigReader.getBooleanProperty(SSH_TUNNEL_ENABLED, false)) {
            return;
        }

        if (session != null && session.isConnected()) {
            return;
        }

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(
                    ConfigReader.getProperty(SSH_USERNAME),
                    ConfigReader.getProperty(SSH_HOST),
                    ConfigReader.getIntProperty(SSH_PORT, DEFAULT_SSH_PORT)
            );
            session.setPassword(ConfigReader.getProperty(SSH_PASSWORD));
            session.setConfig(sessionConfig());
            session.connect();

            session.setPortForwardingL(
                    ConfigReader.getIntProperty(SSH_LOCAL_PORT, DEFAULT_DB_PORT),
                    ConfigReader.getProperty(SSH_REMOTE_HOST, "localhost"),
                    ConfigReader.getIntProperty(SSH_REMOTE_PORT, DEFAULT_DB_PORT)
            );
        } catch (JSchException exception) {
            throw new IllegalStateException("Unable to start SSH tunnel for database connection", exception);
        }
    }

    public static synchronized void stop() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }

        session = null;
    }

    private static Properties sessionConfig() {
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", ConfigReader.getProperty("ssh.strict.host.key.checking", "no"));
        return config;
    }
}
