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

package net.firejack.platform.core.validation;

import net.firejack.platform.core.utils.MessageResolver;
import net.firejack.platform.core.validation.annotation.NotNull;
import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Component
public class NotNullProcessor implements IMessageRuleProcessor {

    public List<ValidationMessage> validate(Method readMethod, String property, Object value, ValidationMode mode)
            throws RuleValidationException {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        NotNull validateNull = readMethod.getAnnotation(NotNull.class);
        if (validateNull != null && value == null &&
                !(mode == ValidationMode.CREATE && validateNull.autoGeneratedField())) {
            String parameterName = StringUtils.isNotBlank(validateNull.parameterName()) ?
                    validateNull.parameterName() : property;
            validationMessages.add(new ValidationMessage(property, validateNull.msgKey(), parameterName));
        }
        return validationMessages;
    }

    @Override
    public List<Constraint> generate(Method readMethod, String property, Map<String, String> params) {
        List<Constraint> constraints = null;
        Annotation annotation = readMethod.getAnnotation(NotNull.class);
        if (annotation != null) {
            NotNull notNull = (NotNull) annotation;
            Constraint constraint = new Constraint(notNull.annotationType().getSimpleName());
            String errorMessage = MessageResolver.messageFormatting(notNull.msgKey(), Locale.ENGLISH, property); //TODO need to set real locale
            constraint.setErrorMessage(errorMessage);
            constraints = new ArrayList<Constraint>();
            constraints.add(constraint);
        }
        return constraints;
    }

}
