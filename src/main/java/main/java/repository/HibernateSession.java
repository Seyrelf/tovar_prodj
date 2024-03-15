package main.java.repository;

import main.java.model.Tovar;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateSession {
    private static SessionFactory sessionFactory;

    private HibernateSession(){}

    public static SessionFactory getSessionFactory(){
        if(sessionFactory==null){
            try{
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(Tovar.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            }
            catch(Exception e){
                System.out.println("Error!) " + e);
            }
        }
        return sessionFactory;
    }

}