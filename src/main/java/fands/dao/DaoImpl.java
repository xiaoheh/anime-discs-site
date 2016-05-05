package fands.dao;

import org.hibernate.*;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Consumer;

import static org.hibernate.criterion.Restrictions.eq;

@Repository
public class DaoImpl implements Dao {

    private SessionFactory sessionFactory;

    @Resource
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public Long save(Object object) {
        return (Long) session().save(object);
    }

    @Transactional
    public void saveOrUpdate(Object object) {
        session().saveOrUpdate(object);
    }

    @Transactional
    public <T> T get(Class<T> klass, Long id) {
        return (T) session().get(klass, id);
    }

    @Transactional
    public void refresh(Object object) {
        session().refresh(object);
    }

    @Transactional
    public void update(Object object) {
        session().update(object);
    }

    @Transactional
    public void delete(Object object) {
        session().delete(object);
    }

    @Transactional
    public <T> List<T> findAll(Class<T> klass) {
        return create(klass).list();
    }

    @Transactional
    public <T> List<T> findBy(Class<T> klass, String name, Object value) {
        return create(klass).add(eq(name, value)).list();
    }

    @Transactional
    public <T> T lookup(Class<T> klass, String name, Object value) {
        return (T) create(klass).add(eq(name, value)).uniqueResult();
    }

    @Transactional
    public <T> T query(HibernateCallback<T> action) {
        return action.doInHibernate(session());
    }

    @Transactional
    public void execute(Consumer<Session> action) {
        action.accept(session());
    }

    public Criteria create(Class<?> klass) {
        return session().createCriteria(klass);
    }

    public Session session() {
        return sessionFactory.getCurrentSession();
    }

}
