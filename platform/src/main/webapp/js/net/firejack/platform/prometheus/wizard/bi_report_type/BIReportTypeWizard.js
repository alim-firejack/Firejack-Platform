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

Ext.require([
    'OPF.prometheus.wizard.AbstractWizard',
    'OPF.console.domain.model.BIReportFieldModel',
    'OPF.console.domain.model.BIReportModel',
    'OPF.console.domain.model.EntityModel',
    'OPF.console.domain.model.FieldModel'
]);

Ext.define('OPF.prometheus.wizard.bi_report_type.BIReportTypeWizard', {
    extend: 'OPF.prometheus.wizard.AbstractWizard',
    alias: 'widget.prometheus.wizard.bi-report-type-wizard',

    statics: {
        id: 'biReportTypeWizard'
    },

    title: 'Create BI Report Type',
    iconCls: 'add-report-icon',

    initComponent: function() {
        var me = this;

        this.titleField = Ext.create('OPF.core.component.form.Text', {
            labelAlign: 'top',
            fieldLabel: 'Title',
            name: 'name',
            anchor: '100%',
            disabled: true
        });

        this.factStore = Ext.create('Ext.data.Store', {
            fields: [
                { name: 'id', type: 'int' },
                { name: 'parameters' },
                { name: 'path' },
                { name: 'lookup' },
                { name: 'name', type: 'string',
                    convert: function(value, record) {
                        return record.get('parameters').domainName + '.' + value;
                    }
                }
            ],
            filters: [function(record, id){
                var name = record.get('name').toLowerCase();
                return name.indexOf('.fact') != -1;
            }],
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/entities/by-lookup/' + this.packageLookup),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            }
        });

        this.factEntityCombo = Ext.create('OPF.core.component.form.ComboBox', {
            labelAlign: 'top',
            fieldLabel: 'Entity',
            anchor: '100%',
            editable: false,
            store: this.factStore,
            queryMode: 'remote',
            displayField: 'name',
            valueField: 'id',
            allowBlank: false,
            listeners: {
                select: function(combo, records, eOpts) {
                    me.titleField.setDisabled(records.length == 0);
                },
                change: function(combo) {

                    Ext.getCmp('dimensionNextBtn').disable();
                    Ext.getCmp('measuresNextBtn').disable();

                    me.entitiesStore.removeAll();
                    me.dimTreeStore.getRootNode().removeAll();
                    me.measuresStore.removeAll();

                    me.entitiesStore.load();
                }
            },
            listConfig: {
                cls: 'x-wizards-boundlist'
            }
        });

        this.entitiesStore = Ext.create('Ext.data.Store', {
            autoLoad: false,
            model: 'OPF.core.model.RegistryNodeTreeModel',
            proxy: {
                type: 'ajax',
                url: OPF.Cfg.restUrl('/registry/entity-fields/' + this.packageLookup),
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            folderSort: true,
            listeners: {
                load: function(store, node, models) {
                    var rootNode = me.dimTreeStore.getRootNode();

                    var factEntityModel = node[0];
                    Ext.each(factEntityModel.get('children'), function(dimEntityData) {
                        if(dimEntityData.type == 'ENTITY') {
                            var biReportFieldNodes = [];
                            Ext.each(dimEntityData.children, function(fieldData) {
                                if(!fieldData.parameters.autoGenerated) {
                                    fieldData.entity = {
                                        id: fieldData.parentId
                                    };
                                    fieldData.field = {
                                        id: fieldData.id
                                    };

                                    fieldData.displayName = me.createDisplayName(fieldData.name);
                                    var biReportFieldNode = OPF.ModelHelper.createModelFromData('OPF.console.domain.model.BIReportFieldModel', fieldData);
                                    biReportFieldNodes.push(biReportFieldNode);
                                }
                            });
                            dimEntityData.children = null;
                            dimEntityData.entity = {
                                id: dimEntityData.id
                            };

                            dimEntityData.displayName = me.createDisplayName(dimEntityData.name);
                            var biReportEntityNode = OPF.ModelHelper.createModelFromData('OPF.console.domain.model.BIReportFieldModel', dimEntityData);
                            biReportEntityNode.appendChild(biReportFieldNodes);

                            rootNode.appendChild(biReportEntityNode);
                        } else {
                            if (!dimEntityData.parameters.autoGenerated) {
                                dimEntityData.displayName = me.createDisplayName(dimEntityData.name);
                                var biReportFieldNode = OPF.ModelHelper.createModelFromData('OPF.console.domain.model.BIReportFieldModel', dimEntityData);
                                me.measuresStore.add(biReportFieldNode);
                            }
                        }
                    });
                    rootNode.expand();
                },
                beforeload: function(store) {
                    var entityId = me.factEntityCombo.getValue();
                    if (entityId) {
                        store.proxy.url = OPF.Cfg.restUrl('/registry/entity-fields/' + entityId);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });

        this.dimTreeStore = Ext.create('Ext.data.TreeStore', {
            autoLoad: false,
            model: 'OPF.console.domain.model.BIReportFieldModel',
            root: {}    ,
            getAt: function (index) {
                var view = me.dimTreePanel.getView();
                var record = view.getRecord(view.getNode(index));
                me.selectNodes(record);
                return record;
            }
        });

        this.dimTreePanel = Ext.create('Ext.tree.Panel', {
            store: this.dimTreeStore,
            rootVisible: false,
            ui: 'white',
            flex: 1,
            columns: [
                {
                    xtype: 'treecolumn',
                    text: 'Source',
                    width: 220,
                    sortable: false,
                    dataIndex: 'name'
                },
                {
                    text: 'Title',
                    flex: 1,
                    sortable: false,
                    dataIndex: 'displayName',
                    editor: {
                        xtype: 'textfield',
                        allowBlank: false
                    }
                },
                {
                    text: 'Count',
                    width: 120,
                    sortable: false,
                    dataIndex: 'count'
                },
                {
                    xtype: 'checkcolumn',
                    text: 'Enabled',
                    align: 'center',
                    trueText: 'Yes',
                    falseText: 'No',
                    dataIndex: 'enabled',
                    width: 76,
                    editor: {
                        xtype: 'checkbox',
                        allowBlank: false
                    }
                }
            ],
            selType: 'rowmodel',
            plugins: [
                Ext.create('Ext.grid.plugin.RowEditing', {
                    clicksToEdit: 2
                })
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        {
                            id: 'dimensionNextBtn',
                            xtype: 'button',
                            disabled: true,
                            ui: 'blue',
                            width: 250,
                            height: 60,
                            text: 'Next',
                            handler: function() {
                                me.goToMeasuresPanel();
                            }
                        }
                    ]
                }
            ]
        });

        this.measuresStore = Ext.create('Ext.data.Store', {
            model: 'OPF.console.domain.model.BIReportFieldModel'
        });

        this.measuresGrid = Ext.create('Ext.grid.Panel', {
            store: this.measuresStore,
            flex: 1,
            border: false,
            columns: [
                {
                    xtype: 'opf-column',
                    text: 'Source',
                    width: 220,
                    sortable: false,
                    dataIndex: 'name'
                },
                {
                    xtype: 'opf-column',
                    text: 'Title',
                    flex: 1,
                    sortable: false,
                    dataIndex: 'displayName',
                    editor: {
                        xtype: 'textfield',
                        allowBlank: false
                    }
                },
                {
                    xtype: 'checkcolumn',
                    text: 'Enabled',
                    width: 120,
                    align: 'center',
                    dataIndex: 'enabled',
                    resizable: false,
                    editor: {
                        xtype: 'checkbox',
                        allowBlank: false
                    },
                    listeners:{
                        checkchange : function(column, rowIndex, checked, opts){
                            var nextButton = Ext.getCmp('measuresNextBtn');
                            var selected = me.countSelectedMeasures();
                            if(selected >= 1) {
                                nextButton.enable();
                            } else {
                                nextButton.disable();
                            }
                        }
                    }
                }
            ],
            selType: 'rowmodel',
            plugins: [
                Ext.create('Ext.grid.plugin.RowEditing', {
                    clicksToEdit: 2
                })
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        {
                            id: 'measuresNextBtn',
                            xtype: 'button',
                            disabled: true,
                            ui: 'blue',
                            width: 250,
                            height: 60,
                            text: 'Next',
                            handler: function() {
                                me.goToDeployPanel();
                            }
                        }
                    ]
                }
            ]
        });

        this.descriptionText = Ext.create('Ext.container.Container', {
            html: '<p>Please provide the metadata for this report type. The detailed configuration of the report view will be done by the application\'s users at run time. ' +
                'The report type created here will result in a page that allows end users to create report views based on the type. No data can be view until additional ' +
                'report view specifications are provided by the view. The metadata configured here simply provides the available dimensions and measures that are ' +
                'selectable for a report so illogical or non-performant dimensions can not be choosen by end users. If you are ensure of which measures and dimensions ' +
                'to provide, enable them all.</p>' +
                '<p>A report definition consists of a title, dimensions, and measures. Dimensions and measures are displayable as column ' +
                'headers or row titles. Measures will always be the final categorization in a report view and always be numeric, usually count or dollar amount. A dimension ' +
                'can have different levels to it. For example a creation date can be broken down by years, quarters, or months.</p>'
        });

        this.dimensionsText = Ext.create('Ext.panel.Panel', {
            bodyPadding: 10,
            html: '<p>Dimensions level should be ordered in increasing specificity. Drag and drop the levels for each dimension into the appropriate order. ' +
                'For example a date dimension that has levels for year, quarter, and month should be ordered as such.</p>'
        });

        this.form = Ext.create('Ext.form.Panel', {
            layout: 'anchor',
            border: false,
            bodyPadding: 10,
            items: [
                this.factEntityCombo,
                this.descriptionText,
                this.titleField
            ],
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    items: [
                        '->',
                        {
                            xtype: 'button',
                            ui: 'blue',
                            width: 250,
                            height: 60,
                            text: 'Next',
                            formBind: true,
                            handler: function() {
                                me.goToDimensionsPanel();
                            }
                        }
                    ]
                }
            ],
            listeners: {
                afterrender: function(form) {
                    me.validator = new OPF.core.validation.FormValidator(form, 'OPF.registry.Report', me.messagePanel, {
                        useBaseUrl: false
                    });

                    me.titleField.customValidator = function(value) {
                        var msg = null;
                        if (me.lastNotUniqueName != value) {
                            if (OPF.isNotBlank(value)) {
                                me.checkUniqueNameTask.delay(250);
                            }
                        } else {
                            msg = 'Title is not unique.';
                        }
                        return msg;
                    };
                }
            }
        });

        this.items = [
            {
                title: '1. Select Fact Entity',
                layout: 'fit',
                items: [
                    this.form
                ],
                nextFrameFn: function() {
                    me.goToDimensionsPanel();
                }
            },
            {
                title: '2. Configure Dimensions',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [
                    this.dimensionsText,
                    this.dimTreePanel
                ],
                prevFrameFn: function() {
                    me.goToSelectFactEntityPanel();
                },
                nextFrameFn: function() {
                    me.goToMeasuresPanel();
                }
            },
            {
                title: '3. Configure Measures',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [
                    this.measuresGrid
                ],
                prevFrameFn: function() {
                    me.goToDimensionsPanel();
                },
                nextFrameFn: function() {
                    me.goToDeployPanel();
                }
            },
            {
                title: '4. Deploy Your Function',
                yesFn: function() {
                    me.createReportType(true);
                },
                noFn: function() {
                    me.createReportType(false);
                },
                prevFrameFn: function() {
                    me.goToMeasuresPanel();
                }
            }
        ];

        this.messagePanel = Ext.ComponentMgr.create({
            xtype: 'notice-container',
            border: true,
            form: this.form
        });
        this.form.insert(0, this.messagePanel);

        this.lastNotUniqueName = null;
        this.checkUniqueNameTask = new Ext.util.DelayedTask(function(){
            var name = me.titleField.getValue();
            var rootRecord = me.factEntityCombo.findRecordByValue(me.factEntityCombo.getValue());
            if (OPF.isNotBlank(name) && rootRecord != null) {
                var path = rootRecord.get('lookup');
                var url = OPF.Cfg.restUrl('/registry/check/' + path + '/BI_REPORT', false);
                url = OPF.Cfg.addParameterToURL(url, 'name', name);
                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    success: function (response) {
                        if (me.titleField) {
                            var resp = Ext.decode(response.responseText);
                            if (resp.success) {
                                var activeErrors = me.titleField.activeErrors;
                                if (activeErrors && activeErrors.length == 0) {
                                    me.titleField.clearInvalid();
                                }
                            } else {
                                me.titleField.markInvalid(resp.message);
                                me.lastNotUniqueName = name;
                            }
                        }
                    },
                    failure: function () {
                        Ext.Msg.alert('Error', 'Connection error!');
                    }
                });
            }
        });

        this.callParent(arguments);
    },

    goToSelectFactEntityPanel: function() {
        var layout = this.getCardPanelLayout();
        layout.setActiveItem(0);
    },

    goToDimensionsPanel: function() {
        this.validateForm(function(scope) {
            var layout = scope.getCardPanelLayout();
            layout.setActiveItem(1);
        }, this);
    },

    goToMeasuresPanel: function() {
        var selectedDimensions = this.countSelectedDimensions(this.dimTreeStore.getRootNode().childNodes);
        if(selectedDimensions >= 2) {
            var layout = this.getCardPanelLayout();
            layout.setActiveItem(2);
        }
    },

    goToDeployPanel: function () {
        var selectedDimensions = this.countSelectedDimensions(this.dimTreeStore.getRootNode().childNodes);
        var selectedMeasures = this.countSelectedMeasures();
        if(selectedDimensions >= 2 && selectedMeasures >= 1) {
            var layout = this.getCardPanelLayout();
            layout.setActiveItem(3);
        }
    },

    selectNodes: function(record) {
        var enabled = !record.get('enabled');
        var isParent = isNaN(record.get('parentId'));
        if(isParent) {
            Ext.each(record.childNodes, function(childNode) {
                childNode.set('enabled', enabled);
            });
        } else {
            var parentRecord = record.parentNode;
            var hasSelected = enabled;
            if(!hasSelected) {
                var id = record.get('id');
                Ext.each(parentRecord.childNodes, function(childRecord) {
                    if(childRecord.get('id') !== id && childRecord.get('enabled')) {
                        hasSelected = true;
                    }
                });
            }
            parentRecord.set('enabled', hasSelected);
        }

        var selected = this.countSelectedDimensions(this.dimTreeStore.getRootNode().childNodes);
        if(!isParent) {
            selected = enabled ? selected + 1 : selected - 1;
        }

        var nextButton = Ext.getCmp('dimensionNextBtn');
        if(selected >= 2) {
            nextButton.enable();
        } else {
            nextButton.disable();
        }
    },

    validateForm: function(executeFn, scope) {
        var parentId = this.factEntityCombo.getValue();
        if (OPF.isNotEmpty(parentId) && Ext.isNumeric(parentId)) {
            if (this.form.getForm().isValid()) {
                executeFn(scope);
            }
        } else {
            this.messagePanel.showError(OPF.core.validation.MessageLevel.ERROR, 'Entity has not been selected.');
        }
    },

    countSelectedDimensions: function(nodes) {
        var count = 0;
        var i, len = nodes.length;
        for (i = 0; i < len; i++) {
            var node = nodes[i];
            if(!isNaN(node.get('parentId')) && node.get('enabled')) {
                count++;
            }
            count = count + this.countSelectedDimensions(nodes[i].childNodes);
        }
        return count;
    },

    countSelectedMeasures: function() {
        var count = 0;
        this.measuresStore.each(function(record) {
            if(record.get('enabled')) {
                count++;
            }
        });
        return count;
    },

    createDisplayName: function(name) {
        var displayName = name.toLowerCase();
        displayName = displayName.replace(/[^a-z0-9]/g, ' ');
        return Ext.String.capitalize(displayName);
    },

    createReportType: function(isDeploy) {
        var me = this;

        var reportName = this.titleField.getValue();
        var rootEntityId = this.factEntityCombo.getValue();

        var biReportFields = [];

        Ext.each(this.dimTreeStore.getRootNode().childNodes, function(biReportEntityNode) {
            var isEntityEnabled = false;
            Ext.each(biReportEntityNode.childNodes, function(biReportFieldNode) {
                var isFieldEnabled = biReportFieldNode.get('enabled');
                if (isFieldEnabled) {
                    var data = {
                        entity: {
                            id: biReportFieldNode.get('parentId')
                        },
                        field: {
                            id: biReportFieldNode.get('id')
                        },
                        displayName: biReportFieldNode.get('displayName')
                    };
                    biReportFields.push(data);
                    isEntityEnabled = true;
                }
            });
            if (isEntityEnabled) {
                var data = {
                    entity: {
                        id: biReportEntityNode.get('id')
                    },
                    displayName: biReportEntityNode.get('displayName')
                };
                biReportFields.push(data);
            }
        });

        this.measuresStore.each(function(biReportFieldNode) {
            var enabled = biReportFieldNode.get('enabled');
            if (enabled) {
                var data = {
                    entity: {
                        id: biReportFieldNode.get('parentId')
                    },
                    field: {
                        id: biReportFieldNode.get('id')
                    },
                    displayName: biReportFieldNode.get('displayName')
                };
                biReportFields.push(data);
            }
        });

        var jsonData = {
            name: reportName,
            parentId: rootEntityId,
            fields: biReportFields
        };

        this.getEl().mask();

        this.save(OPF.Cfg.restUrl('/registry/bi/report'), jsonData, isDeploy);
    }
});