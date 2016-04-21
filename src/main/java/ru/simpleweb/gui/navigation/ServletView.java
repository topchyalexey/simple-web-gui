package ru.simpleweb.gui.navigation;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import ru.simpleweb.gui.Application;
import ru.simpleweb.gui.service.database.files.FileStore;
import ru.simpleweb.gui.service.http.DynamicPages;
import ru.simpleweb.gui.util.builder.MapBuilder;

@WebFilter(filterName = "BpmView", urlPatterns = { "/view" })
public class ServletView implements Filter {

	private final static Logger log = Logger.getLogger(ServletView.class);

	public static final String TASKS_ASSIGNED = "TASKS.ASSIGNED";
	public static final String ERROR_PAGE = "ERROR.PAGE";
	public static final String MAIN_MENU = "MAIN.MENU";

	public static final String THIS_URL = ServletView.class.getAnnotation(WebFilter.class).urlPatterns()[0];
	public static final String ASSIGNED_TASKS_URL = URL(THIS_URL, CONST.PAGE, TASKS_ASSIGNED);
	public static final String ERROR_PAGE_URL = URL(THIS_URL, CONST.PAGE, ERROR_PAGE);
	public static final String MAIN_MENU_URL = URL(THIS_URL, CONST.PAGE, MAIN_MENU);

	public static class CONST {
		public static final String EXIT = "exit";
		public static final String PAGE = "page";
		public static final String VIEW = "view";
		public final static String JSON = "text/json";
		public final static String FILE_VIEW = "fileView";
		public static final String RAW = "raw";
	};

	@EJB DynamicPages pages;
	@EJB ErrorProcessor errors;
	@EJB FileStore fileStore;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain) throws IOException, ServletException {
		processRequest((HttpServletRequest) request, (HttpServletResponse) response, filterchain);
	}

	public static String URL(String base, Object... params) {
		StringBuilder result = new StringBuilder(base.replace("/", ""));
		String delimiter = "?";
		for (Object o : params) {
			result.append(delimiter).append(o);
			if (delimiter.equals("=")) {
				delimiter = "&";
			} else {
				delimiter = "=";
			}
		}
		return result.toString();

	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterchain) throws ServletException, IOException {

		Application.PROPS.initWithHttpRequest(request);

		final Map<String, Object> params = ServletUpdate.getSimpleParams(request);

		try {

			if (params.containsKey(CONST.VIEW)) {
				/*
				log.debug("showing visual action: action=" + actionAlgCode);
				params.putAll(DynamicPages.getInputParams(actionAlgCode, params));
				showPage(response, actionAlgCode, params); */
				return;
			}

			if (params.containsKey(CONST.FILE_VIEW)) {
				/*
				Long id = CommonObject.getLong(params.get(CONST.FILE_VIEW));
				try {
					showBytes(fileStore.getByID(id), response, null);
				} catch (EmptyResultDataAccessException e) {
					showBytes("File not found".getBytes(StandardCharsets.UTF_8), response, null);
				} */
				return;

			} else if (params.containsKey(CONST.PAGE)) {
				String templateCode = (String) params.get(CONST.PAGE);
				params.putAll(DynamicPages.getInputParams(templateCode, params));
				if (params.containsKey(CONST.RAW)) { // ajax or new page with file stream
					Entry<String,String> raw = (Entry<String, String>) params.get(CONST.RAW);
					showRawContent(raw.getValue(), response, raw.getKey());
					return;
				}				
				showPage(response, templateCode, params);

			} else {
				String redirectTo = detectRedirectUrl(params);
				response.sendRedirect(redirectTo);
			}

		} catch (Throwable ex) {
			errors.push(ex);
			showPage(response, ERROR_PAGE);
		}
	}

	private String detectRedirectUrl(Map<String, Object> params) {
		//if ( actionId == null ) {
			return MAIN_MENU_URL;
		//}
	}

	private void showPage(HttpServletResponse response, String templateCode) {
		showContent(pages.getTemplate(templateCode, new HashMap<String,Object>()), response);
	}

	private void showPage(HttpServletResponse response, String templateCode, Map<String, Object> params) {

		showContent(DynamicPages.applyTemplate(templateCode, params).toString(), response);
	}

	public void showContent(String content, HttpServletResponse response) {
		showRawContent(pages.addHeaderAndFooter(content), response, "text/html");
	}

	public void showRawContent(String content, HttpServletResponse response, String type) {
		try {
			
			if ( type.startsWith("attachment;")) {
				String[] types = StringUtils.splitByWholeSeparator(type,";type=");
				String[] typeDetails = StringUtils.splitByWholeSeparator(types[1],"charset=");
				String charset = typeDetails[1];

				response.setContentType(types[1]);			
				response.setHeader("Content-Disposition", types[0] );
				response.setContentLength(content.length());
				//response.setCharacterEncoding(charset);

				ServletOutputStream os = response.getOutputStream();
				os.write(content.getBytes(charset));
				os.flush();
				os.close();
				
			} else {
				String encoding = StandardCharsets.UTF_8.name();			
				response.setCharacterEncoding(encoding);
			
				response.setHeader("Content-Type", type + "; charset=" + encoding);
				response.setContentType( type + ";charset=" + encoding);
				PrintWriter w = response.getWriter();
				w.write(content);
				w.flush();
				w.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void showBytes(byte[] content, HttpServletResponse response, String type) {
		if ( type != null ) {
			response.setHeader("Content-Type", type);
			response.setContentType( type);
		}

		try {
			ServletOutputStream out = response.getOutputStream();
			out.write(content);
			out.flush();
			out.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}	
	

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FilterConfig filterconfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	public static Map<String,Entry<String,String>> RAW(String type, String val) {
		return new MapBuilder<String,Entry<String,String>>(ServletView.CONST.RAW, MapBuilder.entry(type, val)).build();
	}

}