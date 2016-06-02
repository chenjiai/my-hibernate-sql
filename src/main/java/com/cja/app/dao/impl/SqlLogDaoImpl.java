package com.cja.app.dao.impl;

import java.sql.Connection;
import java.util.Date;

import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.cja.app.dao.BaseDao;
import com.cja.app.dao.SqlLogDao;
import com.cja.app.model.SqlLog;

@Service("sqlLogDao")
public class SqlLogDaoImpl extends BaseDao implements SqlLogDao {

	public void addSqlLog(final String sql) {
		// SqlLog sqlLog = new SqlLog();
		// sqlLog.setSql(sql);
		// sqlLog.setUpdateTime(new Date());
		// sqlLog.setCreateTime(new Date());
		// sqlLog.setStatus("0");
		// super.getHibernateTemplate().saveOrUpdate(sqlLog);
		
		Session newSession = null;
		try{
			Connection conn = super.getSession().connection();
			newSession = super.getSessionFactory().openSession(conn);
			SqlLog sqlLog = new SqlLog();
			sqlLog.setSql(sql);
			sqlLog.setUpdateTime(new Date());
			sqlLog.setCreateTime(new Date());
			sqlLog.setStatus("0");
			newSession.save(sqlLog);
		}finally{
			newSession.close();
		}

	}
}
