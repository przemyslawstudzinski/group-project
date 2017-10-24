package server.utils;

import javafx.util.converter.LongStringConverter;
import org.apache.commons.lang3.math.NumberUtils;

public class MyLongStringConverter extends LongStringConverter {

	@Override
	public Long fromString(final String value) {
		return value.isEmpty() || !NumberUtils.isCreatable(value) ? null : super.fromString(value);
	}
}
