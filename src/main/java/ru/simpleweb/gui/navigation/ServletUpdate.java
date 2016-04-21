package ru.simpleweb.gui.navigation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import ru.simpleweb.gui.util.builder.MapBuilder;


@SuppressWarnings("serial")
@WebServlet(name = "BpmUpdate", urlPatterns = { "/update" }, loadOnStartup=1)
public class ServletUpdate extends HttpServlet {

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.debug("delete not implemented");
		super.doDelete(req, resp);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.debug("head not implemented");
		super.doHead(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.debug("options not implemented");
		super.doOptions(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final Map<String, Object> params = getSimpleParams(req);
/*
		Long actionId = CommonObject.getLong(params.get("actionId"));
		ActionInstance actionInstance = CommonBean.getActionDao().getActionInstanceById(actionId);
*/
		RequestDispatcher view = req.getRequestDispatcher( "/view"); //BpmView.actionViewUrl(actionInstance ));
      view.forward(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.debug("put not implemented");
		super.doPut(req, resp);
	}

	private final static Logger log = Logger.getLogger(ServletUpdate.class.getCanonicalName());
	@EJB ErrorProcessor errors;

	/**
	 * используется паттерн POST-REDIRECT-GET (PRG) PATTERN (IN SERVLET)
	 * */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {

			// Create path components to save the file
			final Map<String, Object> params = getSimpleParams(request);

			if ( params.containsKey(ServletView.CONST.EXIT)) {
				response.sendRedirect(ServletView.ASSIGNED_TASKS_URL);
				return;
			}

			//response.sendRedirect(BpmView.ASSIGNED_TASKS_URL);


		} catch (Exception e) {
			errors.push(e);
			response.sendRedirect(ServletView.ERROR_PAGE_URL);
		}
	}

	public static Map<String, Object> getSimpleParams(HttpServletRequest request) {
		return new MapBuilder<String, Object>().putAll(simplify(request.getParameterMap())).build();
	}

	public static Collection<Entry<String, Object>> simplify(Map<String, String[]> parameterMap) {
		return Collections2.transform(parameterMap.entrySet(), new Function<Entry<String, String[]>, Entry<String, Object>>() {
			@Override
			public Entry<String, Object> apply(Entry<String, String[]> arg0) {
				return MapBuilder.entry(arg0.getKey(), (Object)(arg0.getValue() == null ? null : decodePostParamValueToUtf8(arg0.getValue()[0])));
			}
		});
	}
	
	public static String decodePostParamValueToUtf8(String string) {
		String result = StringEscapeUtils.unescapeHtml4(string);
		byte[] b = result.getBytes(StandardCharsets.ISO_8859_1); // the default encoding of a HTTP POST is ISO-8859-1.
		return new String(b, StandardCharsets.UTF_8);
	}

}