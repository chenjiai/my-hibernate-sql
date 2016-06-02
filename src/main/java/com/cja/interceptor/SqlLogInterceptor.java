package com.cja.interceptor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.stereotype.Service;

import com.cja.app.dao.SqlLogDao;

@Service("sqlLogInterceptor")
public class SqlLogInterceptor extends EmptyInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Log log = LogFactory.getLog(this.getClass());
	private static ThreadLocal<Set<String>> localDbSql = new ThreadLocal<Set<String>>();
	private Session session;
	@Resource(name="sqlLogDao")
	private SqlLogDao sqlLogDao;
	
	public void setSession(Session session) {
		this.session = session;
	}

	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) throws CallbackException {
		log.info("onSave");

		Class clas = entity.getClass();
		String tableName = getTableName(clas);

		StringBuffer insertSql = new StringBuffer(30);
		insertSql.append("insert into ").append(tableName).append(" (");

		StringBuffer values = new StringBuffer(30);
		values.append(" values (");
		
		int propertySize = propertyNames.length;
		for (int i = 0; i < propertySize; i++) {
			String propertyName = propertyNames[i];
			Object propertyValue = state[i];
			Type type = types[i];
			
			String cloumnName = getColumnName(clas, propertyName);
			String cloumnValue = convertToString(propertyValue,type);
			
			insertSql.append(cloumnName);
			values.append(cloumnValue);
			
			if (i < propertySize - 1) {
				insertSql.append(",");
				values.append(",");
			} else {
				insertSql.append(")");
				values.append(")");
			}
		}
		
		insertSql.append(values);
		
		log.info(" insert sql:"+insertSql.toString());
		
		Set<String> localSet = getLocalSet();
		localSet.add(insertSql.toString());
		localDbSql.set(localSet);
		return false;

	}
	
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) throws CallbackException {

		log.info("onFlushDirty");
		Class clas = entity.getClass();
		String tableName = getTableName(clas);

		StringBuffer updateSql = new StringBuffer(30);
		updateSql.append("update ").append(tableName).append(" set ");
		
		int propertySize = propertyNames.length;
		for (int i = 0; i < propertySize; i++) {
			String propertyName = propertyNames[i];
			Object propertyValue = currentState[i];
			Type type = types[i];
			
			String cloumnName = getColumnName(clas, propertyName);
			String cloumnValue = convertToString(propertyValue,type);
			updateSql.append( cloumnName + "=" + cloumnValue );
			
			if(i<propertySize-1){
				updateSql.append(",");
			}
		}
		
		String idCloumnName = getIdColumnName(clas);
		updateSql.append(" where ").append(idCloumnName).append(" = ").append(id);
		
		log.info(" update sql:"+updateSql.toString());
		
		Set<String> localSet = getLocalSet();
		localSet.add(updateSql.toString());
		localDbSql.set(localSet);

		return false;

	}

	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) throws CallbackException {

		log.info("onDelete");
		Class clas = entity.getClass();
		String tableName = getTableName(clas);
		StringBuffer deleteSql = new StringBuffer(30);
		deleteSql.append("delete from  ").append(tableName).append(" where ");
		
		String idCloumnName = getIdColumnName(clas);
		deleteSql.append(idCloumnName).append(" = ").append(id);
		
		log.info(" delete sql:"+deleteSql.toString());
		
		Set<String> localSet = getLocalSet();
		localSet.add(deleteSql.toString());
	}

	// called before commit into database
	public void preFlush(Iterator iterator) {
		System.out.println("preFlush");
	}

	// called after committed into database
	public void postFlush(Iterator iterator) {
		log.info("postFlush");
		Set<String> localSqlSet = getLocalSet();
		try { 
			for (Iterator<String> it = localSqlSet.iterator(); it.hasNext();) {
				String sql = it.next();
				System.out.println("postFlush - database");
				
				//SqlLogUtil.LogIt(sql,session.connection());
				sqlLogDao.addSqlLog(sql);
			}	
		}catch(Exception e){
			log.error("插入操作日志异常:",e);
		}finally {
			localSqlSet.clear();
		}
	}

	public void afterTransactionBegin(Transaction tx) {
		log.info("afterTransactionBegin");
	}
	public void afterTransactionCompletion(Transaction tx) {
		log.info("afterTransactionCompletion");
	}
	public void beforeTransactionCompletion(Transaction tx) {
		log.info("beforeTransactionCompletion");
	}
	
	@SuppressWarnings("unchecked")
	private String getTableName(@SuppressWarnings("rawtypes") Class clas) {
		String result = null;
		Table table = (Table) clas.getAnnotation(Table.class);
		result = table.name();
		log.info("table name :" + result);
		return result;
	}
	
	/**
	 * 获取持久化对象id列名称
	 * @param clas
	 * @return
	 */
	private String getIdColumnName(Class clas){
		String result = null;
		Field[] fields =  clas.getDeclaredFields();
		Field idField = null;
		
		int fSize = fields.length;
		for(int i=0;i<fSize-1;i++){
			Field field = fields[i];
			Id id = field.getAnnotation(Id.class);
			if(id!=null){
				idField=field;
				break;
			}
		}
		
		if(idField!=null){
			Column column = idField.getAnnotation(Column.class);
			if(column!=null){
				result = column.name();
			}
			
		}
		log.info("table id name :" + result);
		return result;
	}
 /**
  * 获取属性名称对应列名
  * @param clas
  * @param propertyName
  * @return
  */
	@SuppressWarnings("rawtypes")
	private String getColumnName(Class clas, String propertyName) {
		String result = null;
		try {
			Field field = clas.getDeclaredField(propertyName);
			Column column = field.getAnnotation(Column.class);
			result = column.name();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 获取列值
	 * @param clas
	 * @param propertyName
	 * @param obj
	 * @param type
	 * @return
	 */
	private String getColumnValue(Class clas, String propertyName, Object obj,Type type) {
		String result = null;
		try {
			Field field;
			field = clas.getDeclaredField(propertyName);
			Object value = field.get(obj);
			result = convertToString(value,type);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 转为String
	 * @param value
	 * @param type
	 * @return
	 */
	private String convertToString(Object value, Type type) {
		String result = null;
		if (value == null) {
			result = "NULL";
		} else {
			if (type instanceof org.hibernate.type.TimestampType) {
				result = ((org.hibernate.type.TimestampType)type).toString(value);
			} else if (type instanceof org.hibernate.type.BigDecimalType) {
				result = ((BigDecimal)value).setScale(2,java.math.BigDecimal.ROUND_HALF_UP).toString();
			} else if (type instanceof org.hibernate.type.IntegerType) {
				result = value.toString();
			} else {
				result = value.toString();
			}
		}
		//result = value.toString();
		return result;
	}
	
	/**
	 *  获取当前线程变量值
	 * @return
	 */
	private Set<String> getLocalSet(){
		Set<String> localSet = localDbSql.get();
		if(localSet==null){
			localSet = new HashSet<String>();
		}
		return localSet;
	}
}
