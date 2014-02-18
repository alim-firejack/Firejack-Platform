//@tag opf-model
/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
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

Ext.define('OPF.console.domain.model.BIReportUserFieldModel', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'id', type: 'int', useNull: true },
        { name: 'expanded', type: 'boolean' },
        { name: 'order', type: 'int' },
        { name: 'created', type: 'int' },
        { name: 'location', type: 'string' }
    ],

    associations: [
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.BIReportUserModel',
            name: 'userReport',
            associatedName: 'userReport', associationKey: 'userReport',
            foreignKey: 'id',
            displayName: 'User Report',
            displayDescription: ''
        },
        {
            type: 'belongsTo',
            model: 'OPF.console.domain.model.BIReportFieldModel',
            name: 'field',
            associatedName: 'field', associationKey: 'field',
            foreignKey: 'id',
            displayName: 'Field',
            displayDescription: ''
        }
    ]

});