package com.cumulocity.agent.server.servers.binary;

import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

public interface BinaryServerConfigurator {
    
    public void configure(TCPNIOTransportBuilder server);
}