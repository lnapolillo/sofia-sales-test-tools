package com.despegar.javatemplate.util.newrelic;

import com.newrelic.api.agent.NewRelic;
import org.springframework.stereotype.Component;

@Component
public class NewRelicTransactionManager {

    public void setTransactionName(String name) {
        NewRelic.setTransactionName(null, name);
    }

    public void ignoreTransaction() {
        NewRelic.ignoreTransaction();
    }

    public void addCustomParameter(String key, String value) {
        NewRelic.addCustomParameter(key, value);
    }

}
