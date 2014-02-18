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

/**
 *
 */

Ext.define('OPF.console.statistics.view.SystemMetrics', {
    extend: 'OPF.console.statistics.view.BaseMetricsTab',
    alias : 'widget.system-metrics',

    title: 'SYSTEM METRICS',
    iconCls: 'sm-metrics-viewer-icon',
    rowIconUrl: '/images/icons/16/tracking_16.png',
    entityName: 'MetricsEntry',
    registryNodeType: OPF.core.utils.RegistryNodeType.METRICS_ENTRY,
    metricGroupLevel: 'HOUR',

    getAdditionalColumns: function() {
        return [
            OPF.Ui.populateDateColumn('startTime', 'Start', 130),
            OPF.Ui.populateDateColumn('endTime', 'End', 130),
            OPF.Ui.populateColumn('lookup', 'Action', {
                flex: 1, minWidth: 180,
                renderer: OPF.console.statistics.view.BaseMetricsTab.lookupRenderer
            }),
            OPF.Ui.populateColumn('systemAccountName', 'Account', { width: 100 }),
            OPF.Ui.populateColumn('username', 'User', { width: 100 }),
            OPF.Ui.populateNumberColumn('numberOfInvocations', 'Count', 70, { align: 'right', format: '?0?' }),
            OPF.Ui.populateColumn('averageExecutionTime', 'Avg Time', {
                width: 100, align: 'right',
                renderer: OPF.console.statistics.view.BaseMetricsTab.timeRenderer
            }),
            OPF.Ui.populateColumn('minResponseTime', 'Min Response Time', {
                width: 100, align: 'right',
                renderer: OPF.console.statistics.view.BaseMetricsTab.timeRenderer
            }),
            OPF.Ui.populateColumn('maxResponseTime', 'Max Response Time', {
                width: 100, align: 'right',
                renderer: OPF.console.statistics.view.BaseMetricsTab.timeRenderer
            }),
            OPF.Ui.populateColumn('successRate', 'Success ratio', {
                width: 100, align: 'right',
                renderer: function(value) {
                    var valueTimes100 = value * 100;
                    return Ext.util.Format.number(valueTimes100, "?0?") + '%';
                }
            })
        ];
    },

    getToolbarAdditionalItems: function() {
        var instance = this;

        this.logEntryType = Ext.create('Ext.form.ComboBox', {
            store: Ext.create('Ext.data.Store', {
                fields: ['key', 'name'],
                data : [
                    {key: "ALL", name: "All"},
                    {key: "ACTION", name: "Actions"},
                    {key: "NAVIGATION", name: "Navigations"}
                ]
            }),
            queryMode: 'local',
            displayField: 'name',
            valueField: 'key',
            value: 'ALL'
        });

        return [
            {xtype: 'tbfill'},
            this.logEntryType,
            {xtype: 'tbseparator'},
            instance.populateToggleButton('Hour', {pressed: true}),
            {xtype: 'tbseparator'},
            instance.populateToggleButton('Day'),
            {xtype: 'tbseparator'},
            instance.populateToggleButton('Week'),
            {xtype: 'tbseparator'},
            instance.populateToggleButton('Month')
        ];
    },

    getStoreName: function() {
        return 'MetricsStore';
    },

    getPagerEmptyMsg: function() {
        return 'No metrics to display';
    },

    getPagerDisplayMsg: function() {
        return 'Displaying metrics {0} - {1} of {2}';
    },

    populateToggleButton: function(title, cfg) {
        cfg = cfg || {};
        var groupLevel = title.toUpperCase();
        return Ext.create('Ext.button.Button', Ext.apply({
            text: title,
            enableToggle: true,
            toggleGroup: 'detail',
            action: 'group-by-time',
            groupLevel: groupLevel
        }, cfg));
    }

});