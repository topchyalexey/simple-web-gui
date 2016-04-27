package ru.simpleweb.gui.util;

import java.util.List;

public class Collections {

    public static <T> T getFirstOrNull(List<T> list) {
		if(list == null || list.isEmpty()) {
	    	return null;
	    }
	    return (T) list.get(0);
	}
}
