package io.gridgo.core.support;

import io.gridgo.core.support.template.ProducerTemplate;

public interface ProducerTemplateAware<T> {

	public T setProducerTemplate(ProducerTemplate producerTemplate);
}
