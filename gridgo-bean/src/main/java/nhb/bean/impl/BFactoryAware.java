package nhb.bean.impl;

public interface BFactoryAware {

	void setFactory(BFactory factory);

	default BFactory getFactory() {
		return BFactory.DEFAULT;
	}
}
