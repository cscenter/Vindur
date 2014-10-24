package ru.csc.vindur.bitset;

import java.util.List;

//DPH:
//Мне не очень нравится параметр forceCreate, кривовато. И точно ли нужны все эти методы?
//И название интерфейса совпадает с названием класса в util, это сбивает.
//И слишком много методов в интерфейсе. Реально хватило бы только and/set/or (в варианте false) и copy.
//skrivohatskiy 24.10.14 Название интерфейса должно быть коротким и отражать суть. 
//То, что в util есть класс с таким- же названием не должно сбивать с толку т.к. он не используется
//(не считая JavaBitSet)
//Метод cardinality нужен или хотя-бы isEmpty. При этом первый вариант выглядит предпочтительнее т.к. 
//позволит оптимизатору знать о текущем размере результата и кое- кому еще
//Метод toIntList нужен т.к. больше никто, кроме реализации класса не знает о том, как преобразовывать 
//битсет к списку
//Параметр forceCreate планировалось использовать для реализации многопоточности(вроде copyOnWrite). 
//Хотя и правда выглядит не очень

public interface BitSet {
	/**
	 * Same as <code>and(other, false)</code>
	 * @param other other BitSet. Should have the same type
	 * @return BitSet containing the result of and operation
	 */
	public BitSet and(BitSet other);
	
	/**
	 * @param other other BitSet. Should have the same type
	 * @param forceCreate if true the method create a new BitSet to store result
	 * @return BitSet containing the result of the and operation
	 */
	public BitSet and(BitSet other, boolean forceCreate);

	/**
	 * Same as <code>set(index, false)</code>
	 * @param index
	 * @return BitSet containing the result of the and operation
	 */
	public BitSet set(int index);
	
	/**
	 * @param index
	 * @param forceCreate if true the method create a new BitSet to store result
	 * @return BitSet containing the result of the and operation
	 */
	public BitSet set(int index, boolean forceCreate);
	
	/**
	 * Collection is guaranteed to be sorted
	 * @return sorted collection of setted bits
	 */
	public List<Integer> toIntList();

	public int cardinality();

	public BitSet or(BitSet docsBitSet);

	public BitSet or(BitSet other, boolean forceCreate);
}
