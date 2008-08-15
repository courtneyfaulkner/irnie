package rdacardenas.tutorial;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import rdacardenas.tutorial.entidad.Departamento;
import rdacardenas.tutorial.entidad.Empleado;

public class Cliente
{
    
    public Cliente() { }
    
    public static void main(String[] args)
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("tutorialPU");
        EntityManager em = emf.createEntityManager();
        
        Empleado emp = em.find(Empleado.class, 2);
        em.getTransaction().begin();
        /*
        Empleado emp = em.find(Empleado.class, 2);
        System.out.println(emp);
        emp.setNombre("Victoriano");
        
        Departamento dep = emp.getDepartamento();
        dep.setNombre("Gerencia");
        */
        em.getTransaction().commit();
        em.close();
        
        System.out.println(emp.getDepartamento());
        Departamento dep2 = new Departamento();
        dep2.setId(30);
        dep2.setNombre("Recursos Humanos");
        emp.setDepartamento(dep2);
        
        em = emf.createEntityManager();
        em.getTransaction().begin();
        
        emp = em.merge(emp);
        System.out.println(emp);
        
        em.getTransaction().commit();
        em.close();
        
        
        
        
    }
     
}
