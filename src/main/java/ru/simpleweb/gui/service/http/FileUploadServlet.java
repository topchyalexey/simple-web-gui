package ru.simpleweb.gui.service.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONWriter;

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

import ru.simpleweb.gui.navigation.ErrorProcessor;
import ru.simpleweb.gui.navigation.ServletUpdate;
import ru.simpleweb.gui.service.database.files.FileStore;

@SuppressWarnings("serial")
@WebServlet(name = "FileUploadServlet", urlPatterns = { "/upload" })
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

	@EJB FileStore files;
	@EJB ErrorProcessor errors;

	private final static Logger LOGGER = Logger.getLogger(FileUploadServlet.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		try {
		// Create path components to save the file
		final Part filePart = request.getPart("upload");
		final String fileName = getFileName(filePart);

		Long id = files.save(IOUtils.toByteArray(filePart.getInputStream()), fileName);

		LOGGER.debug(String.format("File %s being uploaded as %s", new Object[] { fileName, id }));

			responseSuccess(response, id);
		} catch (Exception e) {

			responseError(response, errors.push(e));
		}
	}

	private void responseError(HttpServletResponse response, Exception ex) {
		try {
			PrintWriter out = response.getWriter();
			new JSONWriter(out).object().key("status").value("error").key("details").value(Throwables.getStackTraceAsString(ex)).endObject();
			Closeables.close(out, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

	}

	private void responseSuccess(HttpServletResponse response, Long fileId) {
		try {
			PrintWriter out = response.getWriter();
			new JSONWriter(out).object().key("status").value("server").key("fileId").value(fileId).endObject();
			Closeables.close(out, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	private String getFileName(final Part part) {
		final String partHeader = part.getHeader("content-disposition");
		LOGGER.debug(String.format("Part Header = %s", partHeader));
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return ServletUpdate.decodePostParamValueToUtf8(content.substring(content.indexOf('=') + 1).trim().replace("\"", ""));
			}
		}
		return null;
	}
}