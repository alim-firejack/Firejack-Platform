package net.firejack.platform.generate.beans.annotation;

import java.lang.annotation.Annotation;

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

public class EmptyProperties implements Properties {
    @Override
    public String subpackage() {
        return "";
    }

    @Override
    public String prefix() {
        return "";
    }

    @Override
    public String suffix() {
        return "";
    }

    @Override
    public String extension() {
        return "";
    }

    @Override
    public int descriptionLength() {
        return 0;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return EmptyProperties.class;
    }
}
