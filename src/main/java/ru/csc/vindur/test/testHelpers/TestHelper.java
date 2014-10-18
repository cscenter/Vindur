package ru.csc.vindur.test.testHelpers;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.test.DocumentGeneratorBase;
import ru.csc.vindur.test.RequestGeneratorBase;

public interface TestHelper {

	EngineConfig getEngineConfig();

	DocumentGeneratorBase getDocumentGenerator();

	RequestGeneratorBase getRequestGenerator();

}
