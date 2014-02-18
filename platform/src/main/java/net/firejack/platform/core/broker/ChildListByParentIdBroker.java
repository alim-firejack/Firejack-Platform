/*
 * Firejack Open Flame - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.broker;

import net.firejack.platform.core.domain.AbstractDTO;
import net.firejack.platform.core.domain.SimpleIdentifier;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.LookupModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.lookup.ILookupStore;

import java.util.List;

public abstract class ChildListByParentIdBroker<M extends LookupModel, RSDTO extends AbstractDTO>
        extends FilteredListBroker<M, RSDTO, SimpleIdentifier<Long>> {

    protected abstract ILookupStore<M, Long> getStore();

    protected List<M> getModelList(ServiceRequest<SimpleIdentifier<Long>> request) throws BusinessFunctionException {
        Long id = request.getData().getIdentifier();
        return getStore().findAllByParentIdWithFilter(id, getFilter());
    }

}