(function() {
    'use strict';

    function ColumnProfileCtrl($scope, DatagridService, StatisticsService, SuggestionsStatsAggregationsService) {
        var vm = this;
        vm.datasetGridService = DatagridService;
        vm.datasetAggregationsService = SuggestionsStatsAggregationsService;
        vm.chartConfig = {};
        vm.barchartClickFn = function barchartClickFn (item){
            return StatisticsService.addFilter(item.data);
        };

        vm.processedData = null;

        /*
        * Update the Barchart data
        * */
        $scope.$watch(function(){
            return StatisticsService.data;
        }, function(newData){
            vm.processedData = newData;
        });
        //------------------------------------------------------------------------------------------------------
        //----------------------------------------------AGGREGATION---------------------------------------------
        //------------------------------------------------------------------------------------------------------

        vm.calculationsList =  [
            {id: 'sum', name: 'SUM'},
            {id: 'max', name: 'MAX'},
            {id: 'min', name: 'MIN'},
            {id: 'count', name: 'COUNT'},
            {id: 'average', name: 'AVERAGE'},
            {id: 'median', name: 'MEDIAN'}
        ];

        vm.updateCharts = function (column, calculation) {
            vm.datasetAggregationsService.updateAggregationsChanges(column, calculation);
        };




        //------------------------------------------------------------------------------------------------------
        //----------------------------------------------CHARTS OPTIONS------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Common highcharts options
         * @param clickFn - the click callback
         * @returns {{exporting: {enabled: boolean}, legend: {enabled: boolean}}}
         */
        var initCommonChartOptions = function(clickFn) {
            return {
                credits: {
                    enabled: false
                },
                exporting: {
                    enabled: false
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    series: {
                        cursor: 'pointer',
                        point: {
                            events: {
                                click: clickFn
                            }
                        }
                    }
                }
            };
        };

        /**
         * Geo specific highcharts options
         * @param clickFn - the click callback
         * @param min - min value (defined for color)
         * @param max - max value (defined for color)
         * @returns {{exporting, legend}|{exporting: {enabled: boolean}, legend: {enabled: boolean}}}
         */
        var initGeoChartOptions = function(clickFn, min, max) {
            var options = initCommonChartOptions(clickFn);

            options.tooltip = {
                enabled: true,
                headerFormat: '',
                pointFormat: '{point.name}: <b>{point.value}</b>'
            };
            options.colorAxis = {
                min: min,
                max: max
            };
            options.mapNavigation = {
                enabled: true,
                buttonOptions: {
                    verticalAlign: 'bottom'
                }
            };

            return options;
        };

        //------------------------------------------------------------------------------------------------------
        //-----------------------------------------------CHARTS BUILD-------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Init a geo distribution chart
         * @param column
         */
        var buildGeoDistribution = function(column) {
            var geoChartAction = function() {
                StatisticsService.addFilter(this['hc-key'].substring(3));
                console.log('State: '  + this['hc-key'] + ', value: ' + this.value);
            };

            vm.stateDistribution = StatisticsService.getGeoDistribution(column);

            var data = vm.stateDistribution.data;
            var min = data[data.length - 1].value;
            var max = data[0].value;

            vm.chartConfig = {
                options: initGeoChartOptions(geoChartAction, min, max),
                chartType: 'map',
                title: {text: column.name + ' distribution'},
                series: [
                    {
                        id: column.id,
                        data: data,
                        mapData: Highcharts.maps[vm.stateDistribution.map],
                        joinBy: 'hc-key',
                        states: {
                            hover: {
                                color: '#BADA55'
                            }
                        }
                    }
                ]
            };
        };

        //------------------------------------------------------------------------------------------------------
        //-------------------------------------------------WATCHERS---------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Init chart on column selection change
         */
        $scope.$watch(
            function() {
                return StatisticsService.stateDistribution;
            },
            function(column) {
                vm.stateDistribution = null;
                if(column) {
                    buildGeoDistribution(column);
                }
            }
        );

        Object.defineProperty(ColumnProfileCtrl.prototype,
            'selectedColumn', {
                enumerable: true,
                configurable: false,
                get: function () {
                    return this.datasetGridService.selectedColumn;
                }
            });
    }



    angular.module('data-prep.column-profile')
        .controller('ColumnProfileCtrl', ColumnProfileCtrl);
})();