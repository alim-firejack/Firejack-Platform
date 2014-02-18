/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
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

package net.firejack.platform.core.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GreaterThan {

    /**
     * @return
     */
    String msgKey() default "validation.parameter.target.should.be.greater";

    /**
     * @return
     */
    String equalityMsgKey() default "validation.parameter.target.should.be.greater.or.equal";

    /**
     * @return
     */
    String nullValueMsgKey() default "validation.parameter.should.not.be.null";

    /**
     * @return
     */
    String parameterName() default "";

    /**
     * @return
     */
    float floatVal();

    /**
     * @return
     */
    double doubleVal();

    /**
     * @return
     */
    int intVal();

    /**
     * @return
     */
    boolean checkEquality() default false;

}