package com.cja.util;

import java.sql.Connection;
import java.util.Date;

import org.hibernate.Session;

import com.cja.app.model.SqlLog;
import com.cja.persistence.HibernateUtil;

public class SqlLogUtil{
	
	public static void LogIt(String sql, Connection conn ){
		
		Session tempSession = HibernateUtil.getSessionFactory().openSession(conn);
			
		try {
			SqlLog sqlLog = new SqlLog();
			sqlLog.setSql(sql);
			sqlLog.setStatus("0");
			sqlLog.setCreateTime(new Date());
			sqlLog.setUpdateTime(new Date());
			tempSession.save(sqlLog);
			tempSession.flush();
			
		} finally {	
			tempSession.close();
			
		}
			
	}
	
	public static void LogIt(String sql){
		
		Session tempSession = HibernateUtil.getSessionFactory().openSession();
			
		try {
			SqlLog sqlLog = new SqlLog();
			sqlLog.setSql(sql);
			sqlLog.setStatus("0");
			sqlLog.setCreateTime(new Date());
			sqlLog.setUpdateTime(new Date());
			tempSession.save(sqlLog);
			tempSession.flush();
			
		} finally {	
			tempSession.close();
		}
	}
}