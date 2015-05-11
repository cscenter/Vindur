package ru.csc.vindur.service.model;

import ru.csc.vindur.service.model.DocumentModel;

import java.util.List;

/**
 * @author Andrey Kokorev
 *         Created on 01.05.2015.
 *
 * Example:
    {"documents": [
        {"values": {"Int" :[1], "Str": ["Petya", "Lesha"]}},
        {"values": {"Int" :[2], "Str": ["Vasya"]}},
        {"values": {"Int" :[3], "Str": ["Alex", "Nikita"]}},
        {"values": {"Str": ["I can not be found using \"Int\" attribute"]}}
    ]}
 */
public class InsertionRequest
{
    private List<DocumentModel> documents;

    public List<DocumentModel> getDocuments()
    {
        return documents;
    }

    public void setQuery(List<DocumentModel> documents)
    {
        this.documents = documents;
    }
}
