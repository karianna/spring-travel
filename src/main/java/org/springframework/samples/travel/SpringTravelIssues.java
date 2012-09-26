package org.springframework.samples.travel;

import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.management.MBeanServer;
import javax.management.ObjectName;

@Singleton
public class SpringTravelIssues implements SpringTravelIssuesMXBean {

    @Inject
    Logger log;
    private boolean inefficientQuery;
    private boolean evenLessEfficientQuery;
    private boolean pointlessWrite;

    @PostConstruct
    private void registerMBean() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name;
        try {
            name = new ObjectName(SpringTravelIssues.OBJECT_NAME);
            mbs.registerMBean(this, name);
        } catch (Exception e) {
            log.severe("Error while registering mx bean");
        }
    }

    @Override
    public boolean getInefficientQuery() {
        return inefficientQuery;
    }

    @Override
    public void setInefficientQuery(boolean enable) {
        this.inefficientQuery = enable;
    }

    @Override
    public boolean getPointlessWrite() {
        return pointlessWrite;
    }

    @Override
    public void setPointlessWrite(boolean enable) {
        this.pointlessWrite = enable;
    }

    @Override
    public boolean getEvenLessEfficientQuery() {
        return evenLessEfficientQuery;
    }

    @Override
    public void setEvenLessEfficientQuery(boolean enable) {
        this.evenLessEfficientQuery = enable;
    }
}
