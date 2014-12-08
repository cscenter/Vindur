#Документация Vindur
##Введение
Vindur - это встраиваемая NoSQL база данных.

Особенности:

- Позволяет выполнять быстрый поиск по нескольким критериям
- Все операции в оперативной памяти
- Легко установить и настроить
- Полностью написана на Java

##Обзор
###Установка
**TODO** Как скачать, куда распаковать...
###Настройка
Для начала нужно подготовить хранилища. Хранилище состоит из имени аттрибута и типа значения, ассоциируемого с этим именем. Пример простого хранилища:

    Map<String, StorageType> storages = new HashMap<>();
    storages.put("Price", StorageType.STRING);
    storages.put("Manufacturer", StorageType.STRING);
    storages.put("Model", StorageType.STRING);

Теперь можно создать движок и настроить его для работы с нашим хранилищем:

    Engine engine = new Engine.EngineBuilder(EWAHBitSet::new)
            .setStorages(storages)
            .createEngine();
###Заполнение
Перед тем как добавить объект в базу данных, нужно создать новый документ:  

    int docId = engine.createDocument();
    
Теперь, обращаясь к документу по id, можно установить его атрибуты, например:

    engine.setAttributeByDocId(docId, "Price", "399");
    engine.setAttributeByDocId(docId, "Manufacturer", "Nokia");
    engine.setAttributeByDocId(docId, "Model", "3310");

###Запросы
