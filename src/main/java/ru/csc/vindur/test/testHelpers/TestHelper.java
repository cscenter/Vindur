package ru.csc.vindur.test.testHelpers;

import java.util.List;
import java.util.Map;

import ru.csc.vindur.EngineConfig;
import ru.csc.vindur.Request;
import ru.csc.vindur.document.Value;
import ru.csc.vindur.test.GeneratorBase;

public interface TestHelper {

	EngineConfig getEngineConfig();

	GeneratorBase<Map<String, List<Value>>> getDocumentGenerator();

	GeneratorBase<Request> getRequestGenerator();

}
