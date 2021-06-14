package de.bublitz.balancer.server.service;

import de.bublitz.balancer.server.components.BalancerComponent;
import de.bublitz.balancer.server.model.ConfigurationItem;
import de.bublitz.balancer.server.repository.ConfigurationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Siehe auch: https://github.com/mustafabayar/DynamicSchedulingTutorial
 */
@Service
@Log4j2
public class DynamicScheduler implements SchedulingConfigurer {
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private BalancerComponent balancerComponent;

    private int balancingRate = 15;

    @PostConstruct
    public void setUp() {
        if (!configurationRepository.existsByConfigKey("balancingRate")) {
            ConfigurationItem configurationItem = new ConfigurationItem("balancingRate", "15");
            configurationRepository.save(configurationItem);
        } else {
            balancingRate = Integer.parseInt(configurationRepository.getByConfigKey("balancingRate").getConfigValue());
        }
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(() -> balancerComponent.triggerBalance(), t -> {
            Calendar nextExecutionTime = new GregorianCalendar();
            Date lastActualExecutionTime = t.lastActualExecutionTime();
            nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
            balancingRate = getBalancingRate();
            nextExecutionTime.add(Calendar.SECOND, balancingRate);
            return nextExecutionTime.getTime();
        });
    }

    @Transactional
    public int getBalancingRate() {
        return Integer.parseInt(configurationRepository.getByConfigKey("balancingRate").getConfigValue());
    }

    @Transactional
    public void setBalancingRate(int balancingRate) {
        this.balancingRate = balancingRate;
        configurationRepository.getByConfigKey("balancingRate").setConfigValue(String.valueOf(balancingRate));
    }

    @Bean
    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("BalancerPool");
        scheduler.setPoolSize(5);
        scheduler.initialize();
        return scheduler;
    }


}
