package com.seblong.okr.interceptors;

import com.seblong.okr.entities.Company;
import com.seblong.okr.repositories.CompanyRepository;
import com.seblong.okr.utils.ResponseUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CompanyInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private CompanyRepository companyRepo;
	
	@Override
	public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) {
		String companyId = request.getParameter( "companyId" );
		if(StringUtils.isEmpty( companyId )){
			ResponseUtil.outputJson( 403, "require-companyId", response );
			return false;
		}
		Company company = companyRepo.findById( new ObjectId(companyId)).orElse(null);
		if(company == null){
			ResponseUtil.outputJson( 401, "error-companyId", response );
			return false;
		}
		long current = System.currentTimeMillis();
		if(current > company.getEnd()){
			ResponseUtil.outputJson( 401, "expired-auth", response );
			return false;
		}
		return true;
	}
}
