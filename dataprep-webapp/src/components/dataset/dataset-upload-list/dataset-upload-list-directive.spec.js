'use strict';

describe('Dataset upload list directive', function() {
    var scope, createElement;

    beforeEach(module('data-prep-dataset'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function(directiveScope) {
            var element = angular.element('<datasets-upload-list datasets="datasets"></datasets-upload-list>');
            $compile(element)(directiveScope);
            directiveScope.$digest();
            return element;
        };
    }));

    it('should render progressing upload dataset', function() {
        //given
        scope.datasets = [
            {name: 'Customers (50 lines)', progress: 10, error: false},
        ];

        //when
        var element = createElement(scope);
        var names = element.find('td.inventory-title');
        var progress = element.find('td.inventory-description');

        //then
        expect(names.length).toBe(1);
        expect(names.eq(0).text()).toBe('Customers (50 lines)');
        expect(progress.eq(0).text().trim()).toBe('10 %');
        expect(progress.eq(0).hasClass('error')).toBe(false);
    });

    it('should render upload error dataset', function() {
        //given
        scope.datasets = [
            {name: 'Customers (50 lines)', progress: 10, error: true},
        ];

        //when
        var element = createElement(scope);
        var names = element.find('td.inventory-title');
        var progress = element.find('td.inventory-description');

        //then
        expect(names.length).toBe(1);
        expect(names.eq(0).text()).toBe('Customers (50 lines)');
        expect(progress.eq(0).text().trim()).not.toBe('10 %');
        expect(progress.eq(0).hasClass('error')).toBe(true);
    });

    it('should render multiple datasets', function() {
        //given
        scope.datasets = [
            {name: 'Customers (50 lines)', progress: 10, error: false},
            {name: 'Us states', progress: 20, error: false},
            {name: 'Customers (1K lines)', progress: 30, error: true}
        ];

        //when
        var element = createElement(scope);
        var names = element.find('td.inventory-title');
        var progress = element.find('td.inventory-description');

        //then
        expect(names.length).toBe(3);

        expect(names.eq(0).text()).toBe('Customers (50 lines)');
        expect(progress.eq(0).text().trim()).toBe('10 %');
        expect(progress.eq(0).hasClass('error')).toBe(false);

        expect(names.eq(1).text()).toBe('Us states');
        expect(progress.eq(1).text().trim()).toBe('20 %');
        expect(progress.eq(1).hasClass('error')).toBe(false);

        expect(names.eq(2).text()).toBe('Customers (1K lines)');
        expect(progress.eq(2).text().trim()).not.toBe('10 %');
        expect(progress.eq(2).hasClass('error')).toBe(true);
    });
});