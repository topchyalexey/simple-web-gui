package ru.simpleweb.gui.payments.boundary;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.StringMap;

import ru.simpleweb.gui.Application;
import ru.simpleweb.gui.exception.boundary.FunctionCallException;
import ru.simpleweb.gui.payments.model.Field;
import ru.simpleweb.gui.payments.model.Form;
import ru.simpleweb.gui.util.builder.MapBuilder;
import ru.softlogic.remote.AdvancedProcessor;
import ru.softlogic.remote.advanced.Data;
import ru.softlogic.remote.advanced.InputElement;
import ru.softlogic.remote.advanced.RequestData;
import ru.softlogic.remote.advanced.Response;

@Stateless
@Path("/payment")
// @Path("/test/{personCode}/{resourceName}")
public class PaymentService {

	@Inject 
	private FormListService formService;
	private Gson gson = new Gson();
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/do")
	public String commit( @FormParam("form") Integer idForm, @FormParam("fields") String fields, 
							@FormParam("service") String idService, @FormParam("point") String idPoint, 
							@FormParam("provider") String idProvider, @Context HttpServletRequest req ) {
		try {
			Form f =  formService.read(idForm);
			Response<Data> vr = remoteCall(Short.parseShort(idPoint), Short.parseShort(idService), Short.parseShort(idProvider), fieldValues(fields), f.getFuncCode());
			Map<String, String> response = parseItems(vr);
			// Map<String, String> response = new MapBuilder<String,String>().put("fields", fieldValues(fields).toString()).put("idService", idService)
			//			.put("idPoint", idPoint).put("idProvider", idProvider).put("idForm", idForm.toString()).build();
			
	        return gson.toJson(response); 
		} catch (Exception e) {
			return gson.toJson(new JsonPrimitive(Throwables.getStackTraceAsString(e)));
		}
	}  
	
    private Map<String, String> fieldValues(String fields) {
    	Collection<StringMap<String>> coll = new ArrayList<>();
    	coll = gson.fromJson(fields, coll.getClass());
    	Map<String, String> result = Maps.newLinkedHashMap();
    	for (StringMap<String> stringMap : coll) {
			result.put(stringMap.get("code"), stringMap.get("value"));
		}
		return result;
	}
    
	private Response<Data> remoteCall(short idPoint, short serviceId, short providerId, Map<String, String> form, String function) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(Application.PROPS.get("gates-host"), Integer.parseInt(Application.PROPS.get("gates-port")));
        AdvancedProcessor rf = (AdvancedProcessor) registry.lookup("Advanced");
        RequestData rd = new RequestData();
        rd.setFunction(function);
        rd.setParams(form);
        return rf.requestByProvider(idPoint, serviceId, providerId, rd);
    }
    
    private Map<String, String> parseItems(Response<Data> vr) {
        Map<String, String> result = new HashMap<String, String>();
        if (vr.getData() != null && vr.getData().getElements() != null) {
            for (InputElement item : vr.getData().getElements()) {
                result.put(item.getKey(), item.getValue());
            }
        }
        return result;
    }
}
