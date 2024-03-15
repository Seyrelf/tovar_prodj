package main.java.repository;

import main.java.model.Tovar;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class TovarDao {
    public Tovar findById(int id){
        return HibernateSession.getSessionFactory().openSession().get(Tovar.class,id);
    }

    public List<Tovar> findAll(){
        List<Tovar> tovars = (List<Tovar>) HibernateSession.getSessionFactory().openSession().createQuery("From Tovar").list();
        return tovars;
    }

    public void save(Tovar tovar){
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(tovar);
        tx1.commit();
        session.close();
    }

    public void update(Tovar tovar){
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.merge(tovar);
        tx1.commit();
        session.close();
    }

    public void delete(Tovar tovar){
        Session session = HibernateSession.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.remove(tovar);
        tx1.commit();
        session.close();
    }
}
