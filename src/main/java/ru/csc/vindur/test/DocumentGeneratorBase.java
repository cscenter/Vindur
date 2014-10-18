package ru.csc.vindur.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.csc.vindur.Document;

public abstract class DocumentGeneratorBase implements Iterable<Document> {

	private static final String GET_SAVED_UNSUPPORTED = "Generator was created without saveEntities option enabled";
	private final boolean saveDocuments;
	private final int documentsCount;
	private final List<Document> documentsList;

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
	
	protected abstract Document generateDocument();
	
	/**
	 * If there was no iterating over this object entities will be created here
	 * @throws UnsupportedOperationException if saveEntities option is not enabled
	 * @return saved Entities
	 */
	public List<Document> getSavedDocuments() {
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
	public Iterator<Document> iterator() {
		return new Iterator<Document>() {
			private int documentsReturned = 0;
			
			@Override
			public Document next() {
				if(!hasNext()) {
					throw(new IllegalStateException("getting next from itterator when it hasn't next"));
				}
				documentsReturned ++;
				if(saveDocuments) {
					if(documentsReturned <= documentsList.size()) {
						return documentsList.get(documentsReturned - 1);
					}
					Document document = generateDocument();
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
}
