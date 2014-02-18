package net.firejack.platform.installation.processor.event;

import org.springframework.context.ApplicationEvent;

/**
 * Firejack Platform - Copyright (c) 2011 Firejack Technologies
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

public class InstallSlaveEvent extends ApplicationEvent {
	private static final long serialVersionUID = -2928209144157881395L;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the component that published the event (never <code>null</code>)
	 */
	public InstallSlaveEvent(Object source) {
		super(source);
	}
}
