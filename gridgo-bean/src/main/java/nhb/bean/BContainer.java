package nhb.bean;

import nhb.bean.impl.BFactoryAware;

public interface BContainer extends BElement, BFactoryAware {

	int size();

	boolean isEmpty();

	void clear();
}
