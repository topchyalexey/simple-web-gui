package ru.simpleweb.gui.service.http;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Preconditions;

import ru.simpleweb.gui.Application;
import ru.simpleweb.gui.util.builder.MapBuilder;

@Stateless
public class DynamicPages {

	private final static Logger LOGGER = Logger.getLogger(DynamicPages.class);

	public static class LAZY {

		private static final Map<String, String> APP_CONSTANTS = Application.PROPS.getAll();
		public static final Map<String, Object> VISUAL_FORM_CONSTANTS = new MapBuilder<String, Object>()
				.put("app_header", APP_CONSTANTS.get("HEADER"))
				.put("app_footer", APP_CONSTANTS.get("FOOTER") + " / Powered by " + System.getProperty("java.version"))
				.put("java.version", System.getProperty("java.version")).build();

	}

	public String getTemplate(String code, Map<String, Object> map) {
		return applyTemplate(code, getInputParams(code, map));
	}

	public static String applyTemplate(String code, Map<String, Object> inputParams) {
		try {
			StringWriter writer = new StringWriter();
		    MustacheFactory mf = new DefaultMustacheFactory();
		    
		    Mustache mustache = mf.compile(templateReader(code), code);
		    mustache.execute(writer, inputParams);
		    writer.flush();
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Reader templateReader(String code) {
		String resource = templateResource(code);
		InputStream stream = Application.class.getClassLoader().getResourceAsStream(resource);
		Reader r = new InputStreamReader(stream, StandardCharsets.UTF_8);
		return r;
	}

	public static String applySimpleTemplates(String str, Map<String, Object> params) {
		for (Entry<String, Object> param : params.entrySet()) {
			str = StringUtils.replace(StringUtils.replace(str, "$" + param.getKey(), String.valueOf(param.getValue())),
					"$!" + param.getKey(), String.valueOf(param.getValue()));
		}
		return str;
	}

	public static void main(String[] args) {
		System.out.println(applySimpleTemplates(" ASDASD $app_he $!app_he asda",
				new MapBuilder<String, Object>().put("app_he", "AA").build()));
	}

	public static String removeComments(String str) {
		return str.replaceAll("(?s)#\\*\\*.*?\\*#", "");
	}

	public static String safeGetVmTemplate(String code) {
		try {
			return IOUtils.toString(templateReader(code));
		} catch (Exception e) {
			return StringUtils.EMPTY;
		}
	}

	private static String templateResource(String code) {
		return "webix-pages/" + code + ".vm";
	}

	public String addHeaderAndFooter(String content) {

		MapBuilder<String, Object> templateValues = new MapBuilder<String, Object>();

		String rawFooter = removeComments(safeGetVmTemplate("common/BASE.FOOTER"));
		String rawHeader = removeComments(safeGetVmTemplate("common/BASE.HEADER"));
		String stdHeader = StringUtils.remove(applySimpleTemplates(rawHeader, LAZY.VISUAL_FORM_CONSTANTS), "$!customScripts");
		String stdFooter = applySimpleTemplates(rawFooter, LAZY.VISUAL_FORM_CONSTANTS);

		Entry<String, String> content2customJS = putCustomHeaderDataAs(content, templateValues, "script", "customScripts");
		Entry<String, String> content2customCSS = putCustomHeaderDataAs(content2customJS.getKey(), templateValues, "style", "customStyles");
		Entry<String, String> content2customHTM = putCustomHeaderDataAs(content2customCSS.getKey(), templateValues,	"html", "customHtml");

		if (StringUtils.isNotEmpty(content2customJS.getValue()) || StringUtils.isNotEmpty(content2customCSS.getValue())
				|| StringUtils.isNotEmpty(content2customHTM.getValue())) {

			putEmptyContentValues(templateValues, "customScripts", "customStyles", "customHtml");

			templateValues.addAll(LAZY.VISUAL_FORM_CONSTANTS);
			String result = applySimpleTemplates(rawHeader, templateValues.build()) + content2customHTM.getKey()+ stdFooter;
			if (content.contains("<simpleContent/>")) {
				return replaceContentOfWholeWebixScriptByPassedContent(result, content2customCSS.getKey());
			}
			return result;

		}

		return applySimpleTemplates(stdHeader, templateValues.build()) + content + stdFooter;
	}

	private void putEmptyContentValues(MapBuilder<String, Object> templateValues, String... keys) {
		for (String key : keys) {
			if (!templateValues.build().containsKey(key)) {
				templateValues.put(key, "");
			}
		}
	}

	private String replaceContentOfWholeWebixScriptByPassedContent(String page, String newContent) {
		String[] parts = StringUtils.splitByWholeSeparator(page, "<script type");
		String start = parts[0];
		String end = StringUtils.splitByWholeSeparator(parts[1], "</script>")[1];
		return start + newContent + end;

	}

	public Entry<String, String> putCustomHeaderDataAs(String content, MapBuilder<String, Object> templateValues,
			String tag, String key) {
		Entry<String, String> cleanContent2customData = extractCustomHeaderElementsFromContent(content, tag);
		if (StringUtils.isNotEmpty(cleanContent2customData.getValue())) {
			templateValues.put(key, cleanContent2customData.getValue());
		}
		return cleanContent2customData;
	}

	public static Entry<String, String> extractCustomHeaderElementsFromContent(String content, String tag) {
		String[] start = StringUtils.splitByWholeSeparator(" " + content, "<" + tag + ">");
		if (start.length == 1) {
			return MapBuilder.entry(content, StringUtils.EMPTY);
		}
		String endTag = "</" + tag + ">";
		String[] end = StringUtils.splitByWholeSeparator(start[1], endTag);
		Preconditions.checkElementIndex(1, end.length, "End tag not found: " + endTag);
		return MapBuilder.entry(start[0] + end[1], end[0]);
	}

	public static Map<String, Object> getInputParams(String code, Map<String, Object> params) {
		return params;
	}

}
