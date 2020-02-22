package com.seblong.okr.listener;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.seblong.okr.services.OKRPeriodService;

@Component
public class ApplicationContextListener implements ApplicationListener<ContextRefreshedEvent>{

	@Autowired
	private OKRPeriodService okrPeriodService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if( event.getApplicationContext().getParent() == null ) {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			if( !okrPeriodService.exist(year) ) {
				okrPeriodService.create(year);
			}
		}
	}

}
