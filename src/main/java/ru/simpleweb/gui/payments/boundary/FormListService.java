package ru.simpleweb.gui.payments.boundary;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.jdbc.core.JdbcTemplate;

import ru.simpleweb.gui.payments.model.Form;
import ru.softlogic.server.entity.processing.Service;

@Stateless
@Path("/forms")
public class FormListService {

	//@Resource(lookup = "jdbc/pg")
    //private DataSource dataSource;
	
	//@Resource(mappedName="jdbc/pg")
	//DataSource ds;

	@PersistenceContext(unitName="testPU")
	private EntityManager em;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Form> getAll() {
		return em.createQuery("select f from Form as f").getResultList();
	}

	@GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public Form read(@PathParam("id") int id) {
        return em.find(Form.class, id);
     }
    
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void add(Form f) {

	}

	@DELETE
	@Path("{id}")
	public void delete(@PathParam("id") long id) {

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(Form f) {
		
	}    
}
