package com.cja.app;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cja.app.model.User;
import com.cja.interceptor.SqlLogInterceptor;
import com.cja.persistence.HibernateUtil;

public class App {
	public static void main(String[] args) {

		Session session = null;
		Transaction tx = null;

		try {

			SqlLogInterceptor interceptor = new SqlLogInterceptor();
			
			session = HibernateUtil.getSessionFactory().openSession(interceptor);
			interceptor.setSession(session);
			
			//test insert
			tx = session.beginTransaction();
			
			User user = new User();
			user.setUserName("cja");
			user.setAge(32);
			user.setAmount(new BigDecimal(99999999.99));
			user.setStatus("1");
			user.setCreateTime(new Date());
			user.setUpdateTime(new Date());
			
			session.saveOrUpdate(user);
			tx.commit();
			
			//test update
						tx = session.beginTransaction();
						Query query = session.createQuery("from User where userName = 'cja'");
						User userUpdate = 	(User) query.list().get(0);
						
						userUpdate.setStatus("1");
						userUpdate.setUpdateTime(new Date());
						
						session.saveOrUpdate(userUpdate);
						tx.commit();
						
						//test delete
						tx = session.beginTransaction();
						session.delete(userUpdate);
						tx.commit();

		} catch (RuntimeException e) {
			try {
				tx.rollback();
			} catch (RuntimeException rbe) {
				// log.error("Could not roll back transaction", rbe);
			}
			throw e;
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

}
