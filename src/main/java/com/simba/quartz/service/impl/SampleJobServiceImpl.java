package com.simba.quartz.service.impl;

import com.simba.quartz.service.SampleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SampleJobServiceImpl implements SampleJobService {

    @Override
    public void executeSampleJob() {
        log.info("SampleJobServiceImpl executeSampleJob is running");
    }
}
