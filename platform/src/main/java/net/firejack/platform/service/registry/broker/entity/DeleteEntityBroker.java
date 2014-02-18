package net.firejack.platform.service.registry.broker.entity;

import net.firejack.platform.core.model.registry.domain.EntityModel;
import net.firejack.platform.core.store.IStore;
import net.firejack.platform.core.store.registry.IEntityStore;
import net.firejack.platform.service.aop.annotation.Changes;
import net.firejack.platform.service.registry.broker.DeleteRegistryNodeBroker;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

@TrackDetails
@Component("deleteEntityBroker")
@Changes("'Entity '+ name +' has been deleted'")
public class DeleteEntityBroker extends DeleteRegistryNodeBroker<EntityModel> {

	@Autowired
	private IEntityStore store;

	@Override
	protected String getSuccessMessage() {
		return "Entity has deleted successfully";
	}

	@Override
	protected IStore<EntityModel, Long> getStore() {
		return store;
	}

}
