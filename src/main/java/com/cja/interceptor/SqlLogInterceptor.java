package com.cja.interceptor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.type.Type;

import com.cja.util.SqlLogUtil;

public class SqlLogInterceptor extends EmptyInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Session session;
	//TODO注意线程安全
	private Set<String> dbSql = new HashSet<String>();
	public void setSession(Session session) {
		this.session = session;
	}

	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) throws CallbackException {
		System.out.println("onSave");

		Class clas = entity.getClass();
		String tableName = getTableName(clas);

		StringBuffer insertSql = new StringBuffer(30);
		insertSql.append("insert into ").append(tableName).append(" (");

		StringBuffer values = new StringBuffer(30);
		values.append(" values (");
		
		int propertySize = propertyNames.length;
		for (int i = 0; i < propertySize; i++) {
			String propertyName = propertyNames[i];
			Type type = types[i];
			
			String cloumnName = getColumnName(clas, propertyName);
			String value = getColumnValue(clas, propertyNames[i],entity,type);
			
			insertSql.append(cloumnName);
			values.append(value);
			
			if (i < propertySize - 1) {
				insertSql.append(",");
				values.append(",");
			} else {
				insertSql.append(")");
				values.append(")");
			}
		}
		
		insertSql.append(values);
		
		System.out.println(" insert sql:"+insertSql.toString());
		
		dbSql.add(insertSql.toString());

		return false;

	}
	
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) throws CallbackException {

		System.out.println("onFlushDirty");
		Class clas = entity.getClass();
		String tableName = getTableName(clas);

		StringBuffer updateSql = new StringBuffer(30);
		updateSql.append("update ").append(tableName).append(" set ");
		
		int propertySize = propertyNames.length;
		for (int i = 0; i < propertySize; i++) {
			String propertyName = propertyNames[i];
			Type type = types[i];
			
			String cloumnName = getColumnName(clas, propertyName);
			String value = getColumnValue(clas, propertyNames[i],entity,type);
			updateSql.append( cloumnName + "=" + value );
			if(i<propertySize-1){
				updateSql.append(",");
			}
		}
		
		String idCloumnName = getIdColumnName(clas);
		updateSql.append(" where ").append(idCloumnName).append(" = ").append(id);
		
		System.out.println(" update sql:"+updateSql.toString());
		
		dbSql.add(updateSql.toString());

		return false;

	}

	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) throws CallbackException {

		System.out.println("onDelete");

	}

	// called before commit into database
	public void preFlush(Iterator iterator) {
		System.out.println("preFlush");
	}

	// called after committed into database
	public void postFlush(Iterator iterator) {
		System.out.println("postFlush");
		try {
			for (Iterator<String> it = dbSql.iterator(); it.hasNext();) {
				String sql = it.next();
				System.out.println("postFlush - database");
				
				SqlLogUtil.LogIt(sql, session.connection());
			}	
		} finally {
			dbSql.clear();
		}
	}

	@SuppressWarnings("unchecked")
	private String getTableName(@SuppressWarnings("rawtypes") Class clas) {
		String result = null;
		Table table = (Table) clas.getAnnotation(Table.class);
		result = table.name();
		System.out.println("table name :" + result);
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
		
		return result;
	}
 /**
  * 获取
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

}
