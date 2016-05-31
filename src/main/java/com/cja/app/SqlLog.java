package com.cja.app;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_sql_log")
public class SqlLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_sql_log", updatable = false)
	private Integer sqlLogId;
	
	@Column(name = "sql_content")
	private String sql;
	
	@Column(name = "sql_status")
	private String status;
	
	@Column(name = "cre_time")
	private Date createTime;
	
	@Column(name = "upd_time")
	private Date updateTime;

	public Integer getSqlLogId() {
		return sqlLogId;
	}

	public void setSqlLogId(Integer sqlLogId) {
		this.sqlLogId = sqlLogId;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
