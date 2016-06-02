package com.cja.app.dao;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class BaseDao extends HibernateDaoSupport {
    @Resource(name = "sessionFactory")
    public void setInjectSessionFactory(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }
}
