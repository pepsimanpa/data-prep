// ============================================================================
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.configuration;

import javax.annotation.Resource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.core.task.AsyncListenableTaskExecutor;

/**
 * For UT, all event are synchrone
 */
@SuppressWarnings("InsufficientBranchCoverage")
@Configuration
public class DataPrepEvents {

    @Autowired
    private BeanFactory beanFactory;

    @Resource(name = "applicationEventMulticaster#executor")
    private AsyncListenableTaskExecutor executor;

    /**
     * @return The default application context ApplicationEventMulticaster.
     */
    // do NOT change the name as it is important to replace the default application context event multi caster
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster getDataPrepEventsCaster() {
        return new TestApplicationEventMultiCaster();
    }

    private class TestApplicationEventMultiCaster extends SimpleApplicationEventMulticaster {

        @Override
        public void multicastEvent(final ApplicationEvent event, ResolvableType eventType) {
            ResolvableType type = (eventType != null ? eventType : ResolvableType.forInstance(event));

            for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
                invokeListener(listener, event);
            }
        }
    }

}