"use strict";
angular.module('myApp.controllers', []).controller('View1Controller', function($scope, $http, $location,myService) {
    $scope.showErrorMsg = false;
    $scope.onClientNameChange =  function(value) {
        $scope.showErrorMsg = !!value ? false:true;
    }
    $scope.getClientDetails = function() {
        if(!!$scope.clientName) {
            // Call the async method and then do stuff with what is returned inside our own then function
             myService.getViewServiceCall($scope.clientName).then(function(d) {
                 myService.set(d.data);
                 $location.path("/view2")
             }, function(error) {
                $scope.showBanner = true;
                $scope.bannerText = error.statusText;
                $scope.bannerType = "error";
             });
             
        } else {
            $scope.showErrorMsg = true;
            return;
        }
               
    }
    $scope.closeBanner = function() {
        $scope.showBanner = false;
    }
}).controller('View2Controller', function($scope, $http, $location,myService, $uibModal) {
	$scope.edit = function (data) {
		            myService.set(data);
		            $location.path("/submitform")
		        }
	
    $scope.data1 = myService.getData();
        var $ctrl = this;
        $ctrl.animationsEnabled = true;
        $ctrl.open = function (val,modalTitle) {
            $ctrl.val = val;
            $ctrl.modalTitle = modalTitle;
            var modalInstance = $uibModal.open({
            animation: $ctrl.animationsEnabled,
            ariaLabelledBy: 'modal-title',
            ariaDescribedBy: 'modal-body',
            templateUrl: 'modal-window.html',
            controller: 'ModalInstanceCtrl',
            controllerAs: '$ctrl',
            resolve: {
                    val: function () {
                    return $ctrl.val;
                    },
                    modalTitle: function() {
                        return $ctrl.modalTitle;
                    }
                }
            });
        };
    
}).controller('FormController', function($scope, myService, $location) {
	$scope.isStaticMock = false;
   $scope.contents = [
    {
        id: 'application/json',
        name: 'JSON'
    },
    {
        id: 'application/xml',
        name: 'XML'
    }
    ];
   var id = null;
   var data  = myService.getData();
       if(!!data) {
           $scope.clientName = data.client;
           $scope.request = data.request;
            $scope.response = data.response;
            $scope.contentsType = data.contenttype;
   			$scope.serviceName = data.serviceName;
   			id = data.id;
   			$scope.isStaticMock = !data.isStaticMock
       }
   /*Start - code to set the radio button value*/
   
   $scope.checkIsStatic = function() {
   $scope.isStaticMock = !$scope.isStaticMock;
   }
   /* End - code to set the radio button value*/
    $scope.SubmitForm = function () {
        $scope.errorMsg = "Enter mandatory fields";
        var requestObj = {
            clientName:$scope.clientName,
            request:$scope.request,
            response:$scope.response,
            contentTtype:$scope.contentsType,
			description:$scope.description,
			serviceName:$scope.serviceName,
			isStatic:!$scope.isStaticMock,
			id:id
        };

        if(!!$scope.clientName && !!$scope.response) {
            // Call the async method and then do stuff with what is returned inside our own then function
             myService.saveRequestDetails(requestObj).then(function(d) {
                 if(!!d.data && d.data.errorList.length === 0) {
                   showBanner('success', "Request Submitted sucessfully.");
                   resetForm();
                 } else {
                    showBanner('error', d.data.errorList[0].errorMessage);
                 }
             });
        } else {
            $scope.showErrorMsg = true;
            return;
        }

    };
    function showBanner(type, msg) {
          $scope.bannerText = msg;
          var ele = document.querySelector('.error-banner');
          type === 'error' ? angular.element(ele.classList.add("redbg")): angular.element(ele.classList.add("greenbg"));
          $scope.showBanner = true;
    }
     function resetForm() {
        $scope.clientName =""; $scope.request = ""; $scope.response = "";
    }
   $scope.closeBanner = function() {
        $scope.showBanner = false;
    }
}).controller('NavController', function($scope, myService, $location) {
    $scope.getActiveClass = function(path) {
        return ($location.path().substr(0, path.length) === path) ? 'active' : '';
    };
     $scope.isActive = function (viewLocation) { 
        return viewLocation === $location.path();
    };
});

angular.module('ui.bootstrap').controller('ModalInstanceCtrl', function ($uibModalInstance, val, modalTitle) {
  var $ctrl = this;
  $ctrl.val = val;
  $ctrl.modalTitle = modalTitle;
  $ctrl.ok = function () {
    $uibModalInstance.close();
  };

  $ctrl.cancel = function () {
    $uibModalInstance.dismiss('cancel');
  };
});
