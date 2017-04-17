"use strict";
angular.module('myApp',[
'ngRoute',
'myApp.controllers',
'ui.bootstrap'
]).config(function($routeProvider, $locationProvider){
    $routeProvider.when('/view1',{
    templateUrl:'/partials/view1.html'
    }).when('/default',{
        templateUrl:'/partials/default.html'
    }).when('/view2',{
    templateUrl:'/partials/view2.html',
    reloadOnSearch: false 
    }).when('/submitform',{
    templateUrl:'/partials/submitform.html',
    reloadOnSearch: false 
    }).otherwise({redirectTo:'/default'});
    $locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });
});