package ru.csc.vindur.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.csc.vindur.Value;

public abstract class DocumentGeneratorBase implements Iterable<Map<String, List<Value>>> {

	private static final String GET_SAVED_UNSUPPORTED = "Generator was created without saveDocuments option enabled";
	private final boolean saveDocuments;
	private final int documentsCount;
	private final List<Map<String, List<Value>>> documentsList;

	/**
	 * 
	 * @param saveDocuments if true generated entities will be saved into a list
	 * @param documentsCount entities count to be generated
	 */
	public DocumentGeneratorBase(boolean saveDocuments, int documentsCount) {
		this.saveDocuments = saveDocuments;
		this.documentsCount = documentsCount;
		if(saveDocuments) {
			documentsList = new ArrayList<>(documentsCount);
		} else {
			documentsList = null;
		}
	}
	
	protected abstract Map<String, List<Value>> generateDocument();
	
	/**
	 * If there was no iterating over this object entities will be created here
	 * @throws UnsupportedOperationException if saveEntities option is not enabled
	 * @return saved Entities
	 */
	public List<Map<String, List<Value>>> getSavedDocuments() {
		if(!saveDocuments) {
			throw(new UnsupportedOperationException(GET_SAVED_UNSUPPORTED));
		}
		if(documentsList.size() < documentsCount) {
			for(int i = documentsList.size(); i < documentsCount; i ++) {
				documentsList.add(generateDocument());
			}
		}
		return Collections.unmodifiableList(documentsList);
	}

	@Override
	public Iterator<Map<String, List<Value>>> iterator() {
		return new Iterator<Map<String, List<Value>>>() {
			private int documentsReturned = 0;
			
			@Override
			public Map<String, List<Value>> next() {
				if(!hasNext()) {
					throw(new IllegalStateException("getting next from itterator when it hasn't next"));
				}
				documentsReturned ++;
				if(saveDocuments) {
					if(documentsReturned <= documentsList.size()) {
						return documentsList.get(documentsReturned - 1);
					}
					Map<String, List<Value>> document = generateDocument();
					documentsList.add(document);
					return document;
				}
				return generateDocument();
			}
			
			@Override
			public boolean hasNext() {
				return documentsReturned < documentsCount;
			}
		};
	}

	public int getDocumentsCount() {
		return documentsCount;
	}
}
