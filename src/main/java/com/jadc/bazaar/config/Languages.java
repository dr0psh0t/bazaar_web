package com.jadc.bazaar.config;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Languages {
	en, ja;

	public static Set<String> getNames() {
		return Arrays.stream(Languages.values())
				.map(Enum::name)
				.collect(Collectors.toSet());
	}
}
