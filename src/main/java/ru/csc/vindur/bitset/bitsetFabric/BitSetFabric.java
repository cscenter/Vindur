package ru.csc.vindur.bitset.bitsetFabric;

import java.util.Collection;

import ru.csc.vindur.bitset.BitSet;

public interface BitSetFabric {
	BitSet newInstance();

    //TODO как минимум до EWH это дорогая функция, но ее стоимость "спрятна" в фабрике, может это лишнее?
    //А если ее убрать и убрать копирующий конструктор внутрь BitSet (где ему место), то фабрика вообще будет не нужна,
    // достаточно будет передавать в Engine очевидную лямбду-конструктор )
	BitSet newInstance(Collection<Integer> intCollection);

	BitSet newInstance(BitSet resultSet);
}
