package ru.csc.vindur.bitset;

//TODO лишний класс, лучше удалить
// возможно будет удален при отказе от forceCreate
// хотя планировалось добавить сюда дополнительную логику, общую для всех битсетов
public abstract class BitSetBase implements BitSet {

	@Override
	public BitSet and(BitSet other) {
		return and(other, false);
	}
	
	@Override
	public BitSet or(BitSet other) {
		return or(other, false);
	}
	
	@Override
	public BitSet set(int index) {
		return set(index, false);
	}

}
