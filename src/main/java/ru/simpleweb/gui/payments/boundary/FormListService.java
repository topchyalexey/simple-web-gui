package ru.simpleweb.gui.payments.boundary;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ru.simpleweb.gui.payments.model.Field;
import ru.simpleweb.gui.payments.model.Form;

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

/*  // Get with query params, e.g.  app/rest/payment/do?query=aa&granularity=1
@GET
@Produces({MediaType.APPLICATION_JSON})
@Path("/xml/search")
public Object searchXML(@QueryParam("query") String query,
        @QueryParam("granularity") String granularity) {
    return search(query, granularity);
}
*/
	@GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/fieldsFor/{id}")
    public Collection<Field> fieldsFor(@PathParam("id") int id) {
		return read(id).fields();
		// return Response.ok().entity(read(id).fields()).build();
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
