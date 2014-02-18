package net.firejack.platform.generate.service;

import net.firejack.platform.core.config.meta.IPackageDescriptor;
import net.firejack.platform.generate.beans.web.api.Api;
import net.firejack.platform.generate.structure.Structure;

import java.io.IOException;

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

public interface IEndpointGenerationService {
    /**
     * @param descriptor
     * @param api
     * @param structure
     * @throws java.io.IOException
     */
    void generationEndpoint(IPackageDescriptor descriptor, Api api, Structure structure) throws IOException;
}
