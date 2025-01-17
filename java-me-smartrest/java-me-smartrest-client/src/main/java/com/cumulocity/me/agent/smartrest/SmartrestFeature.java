package com.cumulocity.me.agent.smartrest;

import com.cumulocity.me.agent.AgentTemplates;
import com.cumulocity.me.agent.config.ConfigurationService;
import com.cumulocity.me.agent.config.model.ConfigurationKey;
import com.cumulocity.me.agent.feature.BaseFeature;
import com.cumulocity.me.agent.plugin.impl.InternalAgentApi;
import com.cumulocity.me.agent.smartrest.impl.RequestBuffer;
import com.cumulocity.me.agent.smartrest.impl.SmartrestManager;
import com.cumulocity.me.agent.smartrest.impl.SmartrestManagerTask;
import com.cumulocity.me.agent.util.PeriodicExecutor;
import com.cumulocity.me.smartrest.client.impl.SmartHttpConnection;

public class SmartrestFeature extends BaseFeature{
    private final RequestBuffer buffer;
    
    private SmartrestService service;
    private SmartHttpConnection connection;
    private SmartrestManager manager;
    private PeriodicExecutor executor;

    public SmartrestFeature() {
        this.buffer = new RequestBuffer();
    }

    public void init(InternalAgentApi agentApi) {
        super.init(agentApi);
        this.service = new SmartrestService(buffer, agentApi.getConfigurationService(), agentApi.getInternalAgentInfo());
        agentApi.setSmartrestService(service);
    }

    public void start() {
        ConfigurationService config = agentApi.getConfigurationService();
        setupConnection();
        manager = new SmartrestManager(buffer, connection);
        executor = new PeriodicExecutor(config.getInt(ConfigurationKey.AGENT_BUFFER_SEND_INTERVAL).intValue(), new SmartrestManagerTask(manager));
        executor.start();
    }
    
    private void setupConnection(){
        ConfigurationService config = agentApi.getConfigurationService();
        String credentials = agentApi.getInternalAgentInfo().getCredentials();
        connection = new SmartHttpConnection(config.get(ConfigurationKey.CONNECTION_HOST_URL), AgentTemplates.XID, credentials);
        connection.setupConnection(config.get(ConfigurationKey.CONNECTION_SETUP_PARAMS_STANDARD));
        connection.setAddXIdHeader(false);
    }

    public void stop() {
        executor.stop();
        connection.closeConnection();
    }
}
