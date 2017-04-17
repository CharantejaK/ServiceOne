angular
    .module('myApp')
    .directive("flyoutMessage", function () {
        return {
            restrict: "E",
            replace:true,
            scope: {
                'text': '@',
                'type':'@',
                'showbanner':'='
            },
            templateUrl: 'partials/error-banner.html',
            link: function (scope, elem, attrs) {
                 scope.error = attrs.text ;
                attrs.type === 'error' ? elem.addClass('redbg') : elem.addClass('greenbg');
                attrs.autodismiss === "true" ? setTimeout(function() {scope.showBanner = false},100):scope.showBanner = true;
                scope.closeBanner = function() {
                    scope.showBanner = false;
                } // this works
            }
        };
    });